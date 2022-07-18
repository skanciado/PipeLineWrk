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
            scriptContext.println " >> Into the Project > ${this.projectName}"
            scriptContext.openshift.withProject(this.projectName) {
                scriptContext.println "Oc find imageStream"
                def imageStream = scriptContext.openshift.selector('is', appKey)
                scriptContext.println "Oc build imageStream> ${imageStream.exists()}"
                if (!imageStream.exists()) {
                    // Se crea la imagen para que el ConfigBuild no se quede colgado 
                    //(se queda en estado "new" todo el rato si no està creada la imagen)
                    scriptContext.openshift.create('imagestream', appKey)
                }               
                def bc = scriptContext.openshift.selector('bc', appKey)
                // Si no existe el BuildingConfig lo creamos
                if (bc.exists()) 
                {
                    //Borramos el BuildingConfig para evitar que que exista un building apuntando a otro repository no definido por el proceso
                    bc.delete()
                 
                } 
                scriptContext.println "Oc BuildingConfig no existe"
                    // Creacio del Building Config
                def bcTemplate = this.getBuildConfig(appKey, sourcePath, version)
                // Creación del Building Config
                scriptContext.println "Oc BuildingConfig create BuildingConfig"
                scriptContext.openshift.create(bcTemplate)
               
                bc = scriptContext.openshift.selector('bc', appKey)
                scriptContext.println "startBuild BuildingConfig: ${appKey}"
                // Iniciamos el build del BuildingConfig
                def buildSelector = bc.startBuild() 
                scriptContext.println "Log Build Selector"
                buildSelector.logs('-f')
                String result = buildSelector.object().status.phase
                 this.scriptContext.println "Status Phase : ${result}"
               
                bc.delete()
                if (result == "Failed") {
                    this.scriptContext.println "Build Failed"
                    scriptContext.currentBuild.result = 'FAILURE' 
                }
            }
        }
    }

    /*
     Copiar Imagenes entre Clusters
    */
    void copyImage () {
       
        scriptContext.withDockerRegistry([credentialsId: "source-credentials", url: "source-registry-url"]) {

            scriptContext.withDockerRegistry([credentialsId: "destination-credentials", url: "destination-registry-url"]) {

                scriptContext.sh """
                    oc image mirror mysourceregistry.com/myimage:latest mydestinationegistry.com/myimage:latest
                """

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
                "namespace": "${this.projectName}",
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
