package apb.jenkins.utils

class Constants {
    public static final String PROD_BRANCH = "master"
    public static final String PREPROD_BRANCH = "preprod"
    public static final String TST_BRANCH = "training"

    public static final String PROD_PROJECT = "prod"
    public static final String PREPROD_PROJECT = "preprod"
    public static final String TST_PROJECT = "preprod"

    public static final String[] emailList = []//["debora.martin@portdebarcelona.cat", "joan.ferrer@portdebarcelona.cat"]

    @NonCPS
    public static String getProjectName(String branch) {
        if (branch == "origin/${PROD_BRANCH}") {
            return PROD_PROJECT
        } else if (branch == "origin/${PREPROD_BRANCH}") {
            return PREPROD_PROJECT
        }

        return TST_PROJECT
    }
}
