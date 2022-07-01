package apb.jenkins.utils 
/*
Clase que gestión de las conexiones con OpenShift
*/
class OpenshiftClient { 
    // Script Context del PipeLine
    def scriptContext   

    // Nombre del proyecto (namespace)
    String projectName

    /*
    Constructor de la clase
    args scriptContext Consola de Jenkins (clase para peticiones associadas a recursos de pipeline)
    */
    public OpenshiftClient(def scriptContext ) {
        this.scriptContext = scriptContext 
        this.projectName = Constants.getProjectName(scriptContext.env.GIT_BRANCH)
        scriptContext.println "Constructor OpenshiftClient ${projectName}"
    }
    /**
        Contruir el Building Config de la app.
    */
    void buildApp(String appKey, String sourcePath, String version) {
        scriptContext.println "App building > ${appKey} ${sourcePath} ${version}"
        scriptContext.openshift.withCluster() {
            scriptContext.println " >> App Project > ${this.projectName}"
            scriptContext.openshift.withProject(this.projectName) {
                def imageStream = scriptContext.openshift.selector('is', appKey)
                scriptContext.println "Oc build imageStream> ${imageStream.exists()}"
                if (!imageStream.exists()) {
                    // Se crea la imagen para que el ConfigBuild no se quede colgado 
                    //(se queda en estado "new" todo el rato si no està creada la imagen)
                    scriptContext.openshift.create('imagestream', appKey)
                }
                // Creacio del Building Config
                def bcTemplate = this.getBuildConfig(appKey, sourcePath, version)
                // Creacio del Building Config
                scriptContext.openshift.create(bcTemplate)
                def bc = scriptContext.openshift.selector('bc', appKey)
                scriptContext.println "dc appKey> ${appKey}"
                // Iniciem el build del BuildingConfig
                def buildSelector = bc.startBuild() 
                scriptContext.println "Log Build Selector"
                buildSelector.logs('-f')
                String result = buildSelector.object().status.phase
                // No tengo ni idea porque  (??)
                bc.delete()
                if (result == "Failed") {
                    this.scriptContext.println "Build Failed"
                    scriptContext.currentBuild.result = 'FAILURE' 
                }
            }
        }
    }
    /*
     Desplegar la aplicacion al OpenShift
    */
    void deployApp(String appKey) {
        scriptContext.openshift.withCluster() {
           scriptContext.println "Oc deploy Project> ${this.projectName}"
            scriptContext.openshift.withProject(this.projectName) {
                   scriptContext.println "dc appKey> ${appKey}"
                    scriptContext.openshift.selector('dc', "${appKey}").rollout().latest()
                    def dc = scriptContext.openshift.selector('dc', "${appKey}")
                    // this will wait until the desired replicas are available
                    dc.rollout().status()

                    // Get App URL to send it via email
                    def route = scriptContext.openshift.selector('route', "${appKey}")  
                    def existRute = route.exists()
                   scriptContext.println "Oc Route> ${existRute}"
                    if (existRute) {
                        def routeObject = route.object()
                        String appUrl = "${routeObject.spec.host}${routeObject.spec.path}"
                        scriptContext.env.APP_URL = appUrl
                    }
            }
        }
    }
    /**
        Configuracio OpenShift del projecte
    */
    private getBuildConfig(String appKey, String sourcePath, String version) {
        scriptContext.println "appKey: ${scriptContext.env.GIT_URL} URL : ${scriptContext.env.GIT_URL} version: ${version} "
        def bc = [[
            "kind": "BuildConfig",
            "apiVersion": "build.openshift.io/v1",
            "metadata": [
                "name": "${appKey}",
                "labels": [
                    "app": "${appKey}"
                ]
            ],
            "spec": [
                "output": [
                    "to": [
                        "kind": "ImageStreamTag",
                        "name": "${appKey}:latest"
                    ]
                ],
                "strategy": [
                    "type": "Docker" /*,
                    "dockerStrategy": [
                        "buildArgs": [
                            [
                                "name": "VERSION",
                                "value": "${version}"
                            ]
                        ]
                    ]*/
                ],
                "source": [
                    "type": "Git",
                    "git": [
                        "uri": "${scriptContext.env.GIT_URL}",
                        "ref": "${scriptContext.env.GIT_BRANCH}"
                    ],
                    "contextDir": "${sourcePath}",
                    "sourceSecret": [
                        "name": "git-auth"
                    ]
                ]
            ]
        ]]
        scriptContext.println "${bc}"
        return bc
    }
}
