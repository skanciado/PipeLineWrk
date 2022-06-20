package apb.jenkins.analysis

class SonarScanner {

    def scriptContext

    private final String sonarServer = "sonar-server"
    private final String sonarServerBaseUrl = "http://sonarqube-tools-cicd.apps.appdockspro.apb.es"

    public SonarScanner(def scriptContext) {
        this.scriptContext = scriptContext
    }

    void run(String appKey, String sourcePath) {
        scriptContext.env.ANALYSIS_URL = "${this.sonarServerBaseUrl}/dashboard?id=${appKey}"
        scriptContext.dir(sourcePath) {
            scriptContext.withSonarQubeEnv(this.sonarServer) {
                scriptContext.sh "dotnet ${scriptContext.env.MSBUILD_SQ_SCANNER_HOME}/SonarScanner.MSBuild.dll begin /k:'${appKey}'"
                scriptContext.sh "dotnet build"
                scriptContext.sh "dotnet ${scriptContext.env.MSBUILD_SQ_SCANNER_HOME}/SonarScanner.MSBuild.dll end"
            }
        }
    }

    void qualityGate() {
        scriptContext.timeout(time: 1, unit: 'HOURS') {
            scriptContext.waitForQualityGate abortPipeline: true
        }
    }
}
