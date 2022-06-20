import apb.jenkins.utils.OpenshiftClient
import apb.jenkins.utils.EmailClient
import apb.jenkins.utils.Constants
import java.util.HashMap
import apb.jenkins.analysis.SonarScanner
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
    SonarScanner sonarScanner
    pipeline {
        
        agent {
            label project_type == "" ?  ProjectTypes.MAVEN : project_type.toLowerCase()
        }
        options {
            disableConcurrentBuilds()
        }
        triggers { pollSCM('* * * * *') }
         environment {
            MSBUILD_SQ_SCANNER_HOME = tool name: 'sonar-net'
        }
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
            
            stage ("Test") {
                when { 
                    expression{
                        project_type ==  ProjectTypes.MAVEN || project_type ==   ProjectTypes.DOTNET
                    }
                    anyOf {
                        environment name: 'GIT_BRANCH', value: "origin/${Constants.PROD_BRANCH}"
                        environment name: 'GIT_BRANCH', value: "origin/${Constants.PREPROD_BRANCH}"
                    }
                }
                steps {
                    script {
                        dir(testPath) { 
                            if (project_type ==   ProjectTypes.DOTNET) {
                                println 'Pruebas NETCORE'
                                sh "dotnet test --logger trx -r ."
                                xunit(
                                [MSTest(deleteOutputFiles: true,
                                        failIfNotNew: false,
                                        pattern: '*.trx',
                                        skipNoTestFiles: true,
                                        stopProcessingIfError: false)
                                ])
                            }else {
                                 println 'Pruebas JAVA'
                                //TODO JAVA TEST
                            }
                        }
                    }
                }
            }
            stage ("Analysis") {
                when {
                    environment name: 'GIT_BRANCH', value: "origin/${Constants.PREPROD_BRANCH}"
                }
                steps {
                    script {
                       sonarScanner.run(appKey, sourcePath)
                    }
                }
            }
            stage ("Quality Gate") {
                when {
                    environment name: 'GIT_BRANCH', value: "origin/${Constants.PREPROD_BRANCH}"
                }
                steps {
                    script {
                       sonarScanner.qualityGate()
                    }
                }
            }
            stage("Init") {
                steps {
                    script {
                        info(); 
                        println "-" * 80
                        //println "Tipo de proyecto ${params.PROJECT_TYPE}" 
                        println "Tipo de proyecto ${project_type}" 
                        println "-" * 80
                        ocClient = new OpenshiftClient(this) 
                        emailClient = new EmailClient(this)
                        sonarScanner = new SonarScanner(this)
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
