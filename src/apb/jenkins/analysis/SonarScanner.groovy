package apb.jenkins.analysis

class SonarScanner {

    def scriptContext

    private final String sonarServer = "sonar-server"
    private final String sonarServerBaseUrl = "http://sonarqube-tools-cicd.apps.appdockspro.apb.es"

    public SonarScanner(def scriptContext) {
        this.scriptContext = scriptContext 
        scriptContext.println "Constructor SonarScanner"
    }

    void run(String appKey, String sourcePath, String  project_Type) {
        scriptContext.println "Run > Analisis > Start "
        scriptContext.env.ANALYSIS_URL = "${this.sonarServerBaseUrl}/dashboard?id=${appKey}"
        scriptContext.println "URL Analisis ${scriptContext.env.ANALYSIS_URL} "
        scriptContext.dir(sourcePath) {
            switch (project_type) {
            case ProjectTypes.MAVEN.name():
                scriptContext.withSonarQubeEnv(this.sonarServer) {
                    scriptContext.sh "mvn sonar:sonar -Dsonar.projectKey=${appKey} -Dsonar.projectName=${appKey} -Dsonar.languaje=java"
                } 
                break;
            case ProjectTypes.DOTNET.name():
                scriptContext.println "Run > Analisis > NETCORE "
                scriptContext.withSonarQubeEnv(this.sonarServer) {
                    scriptContext.sh "dotnet ${scriptContext.env.MSBUILD_SQ_SCANNER_HOME}/SonarScanner.MSBuild.dll begin /k:'${appKey}'"
                    scriptContext.sh "dotnet build"
                    scriptContext.sh "dotnet ${scriptContext.env.MSBUILD_SQ_SCANNER_HOME}/SonarScanner.MSBuild.dll end"
                }
                break;
            }
        }
        scriptContext.println "Run > Analisis > End "
     
    }

    void qualityGate() {
        scriptContext.timeout(time: 1, unit: 'HOURS') {
            scriptContext.waitForQualityGate abortPipeline: true
        }
    }
}
