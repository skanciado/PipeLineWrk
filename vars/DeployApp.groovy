import apb.jenkins.utils.OpenshiftClient
import apb.jenkins.utils.EmailClient
import apb.jenkins.utils.Constants
import apb.jenkins.utils.ProjectTypes
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
            label project_type == "" ?  ProjectTypes.MAVEN.name() : project_type.toLowerCase()
        }
        options {
            disableConcurrentBuilds()
        }
        triggers { pollSCM('* * * * *') }
         environment {
            MSBUILD_SQ_SCANNER_HOME = tool name: 'sonar-net'
            PROJECT_TYPE = "${project_type}"
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
            stage("Init") {
                steps {
                    script {
                        /*Mostrar Info del Proceso*/
                        info(); 
                        println "-" * 80
                        //println "Tipo de proyecto ${params.PROJECT_TYPE}" 
                        println "Tipo de proyecto ${project_type}" 
                        println "-" * 80
                    }
                }
                steps {
                    script {
                        /*Inicializar Variables del proceso */                       
                        ocClient = new OpenshiftClient(this) 
                        emailClient = new EmailClient(this)
                        sonarScanner = new SonarScanner(this)
                    }
                }
                steps {
                      script {
                            /*Restore remote package, se descarga todas las librerias remotas*/
                            switch(env.PROJECT_TYPE) {  
                                case ProjectTypes.DOTNET.name():
                                    println 'Restore NETCORE'
                                    sh "dotnet restore --configfile NuGet.Config"
                                    break; 
                                default:
                                    println " Restore no necesarió "
                                    break;
                            }
                      } 
                } 
                
            }
            stage ("Test") {
                when {  
                    anyOf {
                        environment name: 'GIT_BRANCH', value: "origin/${Constants.PROD_BRANCH}"
                        environment name: 'GIT_BRANCH', value: "origin/${Constants.PREPROD_BRANCH}"
                    }
                }
                steps {
                    script { 
                        dir(testPath) { 
                            switch(env.PROJECT_TYPE) { 
                                case ProjectTypes.MAVEN.name():
                                    println 'Pruebas MAVEN' 
                                    sh "mvn -B test"
                                    junit "**/target/**/*.xml"
                                    break;
                                case ProjectTypes.DOTNET.name():
                                    println 'Pruebas NETCORE'
                                    sh "dotnet test --logger trx -r ."
                                    xunit(
                                    [MSTest(deleteOutputFiles: true,
                                            failIfNotNew: false,
                                            pattern: '*.trx',
                                            skipNoTestFiles: true,
                                            stopProcessingIfError: false)
                                    ])
                                    break; 
                                default:
                                    println " No hay pruebas de código"
                                    break;
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
                       switch(env.PROJECT_TYPE) { 
                                case ProjectTypes.MAVEN.name(): 
                                case ProjectTypes.DOTNET.name():
                                    println 'Analisis DOTNET '
                                    sonarScanner.run(appKey, sourcePath,env.PROJECT_TYPE)
                                    break; 
                                default:
                                    println " No hay analisis de código"
                                    break;
                            }
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
