package apb.jenkins.utils
/**
 Email Client - Clase para notificar por email 
*/
class EmailClient {

    // Script Context del PipeLine
    def scriptContext

    public EmailClient(def scriptContext) {
        this.scriptContext = scriptContext 
        scriptContext.println "Constructor EmailClient"
    }
    /**
    Enviar notificación OK
    */
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
    /**
    Enviar notificación Error
    */
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
