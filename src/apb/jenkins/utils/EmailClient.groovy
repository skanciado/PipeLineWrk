package apb.jenkins.utils

class EmailClient {

    def scriptContext

    public EmailClient(def scriptContext) {
        this.scriptContext = scriptContext 
        scriptContext.println "Constructor EmailClient ${projectName}"
    }

    public void notifySuccess(String[] emailList) {
        String [] fullList = Constants.emailList + emailList
        String emails = String.join(",", fullList)
        scriptContext.emailext body: '${JELLY_SCRIPT,template="managed:notification_other_template"}',
                            recipientProviders: [scriptContext.requestor()],
                            subject: "Build ${scriptContext.env.BUILD_TAG} succeeded",
                            to: "${emails}",
                            attachLog: true,
                            mimeType: "text/html"
    }

    public void notifyFail(String[] emailList) {
        String [] fullList = Constants.emailList + emailList
        String emails = String.join(",", fullList)
        scriptContext.emailext body: '${JELLY_SCRIPT,template="managed:notification_other_template"}',
                            recipientProviders: [scriptContext.developers(), scriptContext.requestor()],
                            subject: "Build ${scriptContext.env.BUILD_TAG} failed",
                            to: "${emails}",
                            attachLog: true,
                            mimeType: "text/html"
    }
}
