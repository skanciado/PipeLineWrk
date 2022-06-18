package apb.jenkins.utils 
class OpenshiftClient { 
    // Script Context del PipeLine
    def scriptContext   

    // Nombre del proyecto (namespace)
    String projectName

    public OpenshiftClient(def scriptContext ) {
        this.scriptContext = scriptContext 
        this.projectName = Constants.getProjectName(scriptContext.env.GIT_BRANCH)
        scriptContext.println "Constructor OpenshiftClient ${projectName}"
    }
    /**
        Contruir el Building Config de la app.
    */
    void buildApp(String appKey, String sourcePath, String version) {
        scriptContext.println "buildApp > ${appKey} ${sourcePath} ${version}"
        scriptContext.openshift.withCluster() {
            scriptContext.println "Oc build Project> ${this.projectName}"
            scriptContext.openshift.withProject(this.projectName) {
                def imageStream = scriptContext.openshift.selector('is', appKey)
                scriptContext.println "Oc build imageStream> ${imageStream.exists()}"
                if (!imageStream.exists()) {
                    scriptContext.openshift.create('imagestream', appKey)
                }
                def bcTemplate = this.getBuildConfig(appKey, sourcePath, version)
                scriptContext.openshift.create(bcTemplate)
                def bc = scriptContext.openshift.selector('bc', appKey)
                scriptContext.println "dc appKey> ${appKey}"
                def buildSelector = bc.startBuild() 
                scriptContext.println "Log Build Selector"
                buildSelector.logs('-f')
                String result = buildSelector.object().status.phase
                bc.delete()
                if (result == "Failed") {
                    this.scriptContext.println "Build Failed"
                    scriptContext.currentBuild.result = 'FAILURE' 
                }
            }
        }
    }

    void deployApp(String appKey) {
        scriptContext.openshift.withCluster() {
           // println "Oc deploy Project> ${this.projectName}"
            scriptContext.openshift.withProject(this.projectName) {
                   //  println "dc appKey> ${appKey}"
                    scriptContext.openshift.selector('dc', "${appKey}").rollout().latest()
                    def dc = scriptContext.openshift.selector('dc', "${appKey}")
                    // this will wait until the desired replicas are available
                    dc.rollout().status()

                    // Get App URL to send it via email
                    def route = scriptContext.openshift.selector('route', "${appKey}")  
                    def existRute = route.exists()
                   // println "Oc Route> ${existRute}"
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
                    "type": "Docker",
                    "dockerStrategy": [
                        "buildArgs": [
                            [
                                "name": "VERSION",
                                "value": "${version}"
                            ]
                        ]
                    ]
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
