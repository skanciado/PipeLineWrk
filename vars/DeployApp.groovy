import apb.jenkins.utils.OpenshiftClient
import apb.jenkins.utils.EmailClient
import apb.jenkins.utils.Constants
import java.util.HashMap
/**
    Metodo para extraer informacion del thread
*/
void info() {
    println "-" * 80
    println " Info Jenkins Job" 
    println "-" * 80
    def config = new HashMap()
     sh "printenv | sort"      
    println "-" * 80
    println "-" * 80
}

void call(String appKey, String sourcePath = ".", String version = "latest", String[] emailList = []) {
    OpenshiftClient ocClient
    EmailClient emailClient 
    pipeline {
        
        agent {
            label "maven"
        }
        options {
            disableConcurrentBuilds()
        }
        triggers { pollSCM('* * * * *') }
        stages {
            // TODO Descomentar estas lineas
            /*
            stage("No changes in project folder") {
                when {
                    anyOf {
                        not { changeset "${sourcePath}/**" }
                        not { changeset "${testPath}/**" }
                    }
                }
                steps {
                    script {
                        currentBuild.result = "NOT_BUILT"
                        error("Cambios no detectados en el proyecto")
                    } 
                }
            }
            */
            stage("Init") {
                steps {
                    script {
                        info();
                        println "Init Variable Open Shift Client" 
                        ocClient = new OpenshiftClient(this)
                        println "Init Variable Email Client" 
                        emailClient = new EmailClient(this)
                    }
                }
                
            }
            stage ("Build & Publish") {
                steps {
                    script {
                        println "Build > ${appKey} "
                        ocClient.buildApp(appKey, sourcePath, version)
                        println "Current build > ${currentBuild.result}"
                        if (currentBuild.result == "FAILURE") {
                            error("Error en la construcción de la aplicacion en OpenShift")
                        }
                    }
                }
            }
            stage ("Deploy") {
                when {
                    expression {
                        currentBuild.result != "FAILURE"
                    }
                }
                steps {
                   script {
               
                       ocClient.deployApp(appKey)
                    }
                }
            }
        }
        post {
            success {
                script {
                    emailClient.notifySuccess(emailList)
                }
            }
            failure {
                script {
                    emailClient.notifyFail(emailList)
                }
            }
        }
    }
}
