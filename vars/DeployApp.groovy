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
    sh "printenv | sort"      
    println "-" * 80
    println "-" * 80
}

void call(String project_type, String appKey, String sourcePath = ".", String testPath = ".", String version = "latest", String[] emailList = []) {
    OpenshiftClient ocClient
    EmailClient emailClient 
    pipeline {
        
        agent {
            label project_type == "" ? "MAVEN" : project_type 
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
                        
                       println "-" * 80
                       //println "Tipo de proyecto ${params.PROJECT_TYPE}" 
                       println "Tipo de proyecto ${project_type}" 
                       println "-" * 80
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
