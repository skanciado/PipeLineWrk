 
## Table of Contents
1. [Infomación General](#general-info)
2. [Construcción](#Construcción)
3. [Objetos en Jenkins](#ObjetosenJenkins)
4. [Plugins necesarios](#Pluginsnecesarios)
5. [Colavoración](#Colavoración)
  

## Infomación General
Este PipeLine está creada para ejecutar proyectos 
* __JAVA__ basados en proyectos Maven
* __.NET Core__  
* Despliegue __generico__ basado en DockerFile. 

El despliegue se realiza en un OpenShift.

## Construcción
Todos los proyectos que se incluyen en la pipiline deben tener un JenkinsFile , como este: 
   ```
   library('pipeline-common')
   // Lanzar la pipeline 
    DeployApp("DOCKER",  "sensors-52n-sos-2", "images/52n-sos/", ".", "1.0.0", "daniel.horta-vidal@t-systems.com")
   ```

>>> Donde la libreria "pipeline-common" està registrada en Jenkins (General > Ajustes > Librerias Externas.

Al ejectura el DeployApp se lanzara la Pipeline. Los parametros de este metodo son los siguientes :
* **Tipo de proyecto** (DOCKER,MAVEN o DOTNET). El tipo de pipeline que se ejecutarà.
* **Nompre del proyecto**. Como se llama el proyecto, es importante definir un nombre que encaje con el ConfigBuild de OpenShift.
* **SRC**. Carpeta fuente del proyecto
* **Test**. Carpeta del proyecto de test
* **Version**. Versión del despliegue de OpenShift
* **Mails**. Lista de correos para ser informados.
    
## Objetos en Jenkins
* Credenciales de openshift.
* Nodo maven (jnlp openshift/jenkins-agent-maven:latest ${computer.jnlpmac} ${computer.name})
* Nodo docker (jnlp  tools-cicd/docker-agent  ${computer.jnlpmac} ${computer.name})
* Nodo dotnet (jnlp dotnet tools-cicd/dotnet-slave:latest  ${computer.jnlpmac} ${computer.name})

## Plugins necesarios	
* Ant Adds Apache Ant support to Jenkins 1.13	
* Apache HttpComponents Client 4.x API Plugin 4.5.13-1.0	
>> Bundles Apache HttpComponents Client 4.x and allows it to be used by Jenkins plugins.
>> This plugin is up for adoption! We are looking for new maintainers. Visit our Adopt a Plugin initiative for more information.		
* Authentication Tokens API Plugin This plugin provides an API for converting credentials into authentication tokens in Jenkins. 1.4		
* Autofavorite for Blue Ocean Automatically favorites multibranch pipeline jobs when user is the author 1.2.4	
* Bitbucket Branch Source 
>> Allows to use Bitbucket Cloud and Bitbucket Server as sources for multi-branch projects. It also provides the required connectors for Bitbucket Cloud Team and Bitbucket Server Project folder (also known as repositories auto-discovering).
* Bitbucket Pipeline for Blue Ocean BlueOcean Bitbucket pipeline creator 1.25.2	
* Blue Ocean BlueOcean Aggregator 1.25.2	
* Blue Ocean Core JS  Blue Ocean Core JS Plugin. This plugin is a part of the Blue Ocean Plugin set. 1.25.2	
* Blue Ocean Pipeline Editor The Blue Ocean Pipeline Editor is the simplest way for anyone wanting to get started with creating Pipelines in Jenkins 1.25.2	
* Bootstrap 4 API Provides Bootstrap 4 for Jenkins plugins. 4.6.0-3		
* Bootstrap 5 API Provides Bootstrap 5 for Jenkins plugins. 5.1.3-3	
* bouncycastle API This plugin provides a stable API to Bouncy Castle related tasks. 2.25	
* Branch API This plugin provides an API for multiple branch based projects. 2.7.0	
* Caffeine API  Caffeine api plugin for use by other Jenkins plugins. 2.9.2-29.v717aac953ff3	
* Checks API  This plugin defines an API for Jenkins to publish checks to SCM platforms. 1.7.2		
* Command Agent Launcher Allows agents to be launched using a specified command. 1.6		
* Common API for Blue Ocean This plugin is a part of Blue Ocean UI 1.25.2	
* Config API for Blue Ocean BlueOcean Analytics Tools plugin 1.25.2	
* Config File Provider  3.8.2	
>> Ability to provide configuration files (e.g. settings.xml for maven, XML, groovy, custom files,...) loaded through the UI which will be copied to the job workspace.	
* Configuration as Code 1.55
>> This plugin allows configuration of Jenkins based on human-readable declarative configuration files.	
* Configuration as Code Plugin - Groovy Scripting Extension 1.1
>>Extension for Configuration as Code that allows running Groovy scripts.		
* Credentials This plugin allows you to store credentials in Jenkins. 2.6.2	
* Credentials Binding Allows credentials to be bound to environment variables for use from miscellaneous build steps. 1.27	
* Dashboard for Blue Ocean Blue Ocean Dashboard 1.25.2	
* Design Language Jenkins Design Language Plugin. This plugin is a part of the Blue Ocean Plugin set. 1.25.2	
* Display URL API Provides the DisplayURLProvider extension point to provide alternate URLs for use in notifications 2.3.5	
*  Display URL for Blue Ocean This plugin generates BlueOcean specific URLs for the Display URL plugin. 2.4.1	
* Docker Commons Provides the common shared functionality for various Docker-related plugins. 1.17	
* Docker Pipeline Build and use Docker containers from pipelines.1.26	
* DTKit 2 API  This plugin exposes the DTKit 2 APIs to other Jenkins plugins. 3.0.0		
* Durable Task Library offering an extension point for processes which can run outside of Jenkins yet be monitored. 493.v195aefbb0ff2	
* ECharts API Provides ECharts for Jenkins Plugins, a JavaScript visualization tool to create intuitive, interactive, and highly-customizable charts. 5.2.2-1	
* Email Extension This plugin is a replacement for Jenkins's email publisher. It allows to configure every aspect of email notifications: when an email is sent, who should receive it and what the email says 2.86	
* Email Extension Template This plugin allows administrators to create global templates for the Extended Email Publisher.This plugin is up for adoption! We are looking for new maintainers. Visit our Adopt a Plugin initiative for more information. 1.2		
* Events API for Blue Ocean Blue Ocean Events 1.25.2	
* Favorite This plugin allows users to favorite a job. 2.3.3	
* Folders This plugin allows users to create "folders" to organize jobs. Users can define custom taxonomies (like by project type, organization type etc). Folders are nestable and you can define views within folders. Maintained by CloudBees, Inc. 6.16	
* Font Awesome API Provides the free fonts of Font Awesome for Jenkins plugins. 5.15.4-4	
* Git This plugin integrates Git with Jenkins. 4.10.1		
* Git client Utility plugin for Git support in Jenkins 3.10.0	
* Git Pipeline for Blue Ocean BlueOcean Git SCM pipeline creator 1.25.2	
* GIT server Allows Jenkins to act as a Git server. 1.10	
* GitHub This plugin integrates GitHub to Jenkins. 1.34.1	
* GitHub API This plugin provides GitHub API for other plugins. 1.301-378.v9807bd746da5	
* GitHub Branch Source Multibranch projects and organization folders from GitHub. Maintained by CloudBees, Inc. 2.11.3	
* GitHub Integration GitHub Integration Plugin for Jenkins 0.3.0		
* GitHub Pipeline for Blue Ocean BlueOcean GitHub organization pipeline creator 1.25.2	
* Google OAuth Credentials plugin This plugin implements the OAuth Credentials interfaces to surface Google Service Account credentials to Jenkins. 1.0.6	
* Groovy This plugin executes Groovy code. 2.4	
* Handy Uri Templates 2.x API Bundles Handy Uri Templates 2.x and allows it to be used by Jenkins plugins 2.1.8-1.0	
* HTML Publisher This plugin publishes HTML reports. 1.28	
* i18n for Blue Ocean Blue Ocean Internationalization (i18n) Plugin. This plugin is a part of the Blue Ocean Plugin set. 1.25.2	
* Jackson 2 API This plugin exposes the Jackson 2 JSON APIs to other Jenkins plugins. 2.13.0-230.v59243c64b0a5	
* Java JSON Web Token (JJWT)
Bundles the Java JSON Web Token (JJWT) library.0.11.2-9.c8b45b8bb173		 	
JavaScript GUI Lib: ACE Editor bundle plugin
JavaScript GUI Lib: ACE Editor bundle plugin.
1.1		
	
* JavaScript GUI Lib: Handlebars bundle plugin
JavaScript GUI Lib: Handlebars bundle plugin.
3.0.8		
	
* JavaScript GUI Lib: jQuery bundles (jQuery and jQuery UI) plugin
JavaScript GUI Lib: jQuery bundles (jQuery and jQuery UI) plugin.
1.2.1		
	
* JavaScript GUI Lib: Moment.js bundle plugin
JavaScript GUI Lib: Moment.js bundle plugin.
1.1.1		
	
* JAXB
JAXB packaging for more transparent Java 9+ compatibility
This plugin is up for adoption! We are looking for new maintainers. Visit our Adopt a Plugin initiative for more information.
2.3.0.1	
	
* Jira
This plugin integrates Jenkins to Atlassian Jira.
3.6	
	
* Job Configuration History
* Job history plugin for Jenkins.
2.28.1		
	
* Job DSL
This plugin allows Jobs and Views to be defined via DSLs
1.78.1	
	
* JQuery3 API
Provides jQuery 3 for Jenkins Plugins. jQuery is a fast, small, and feature-rich JavaScript library.
3.6.0-2	
	
* JSch dependency plugin
Jenkins plugin that brings the JSch library as a plugin dependency, and provides an SSHAuthenticatorFactory for using JSch with the ssh-credentials plugin.
0.1.55.2		
	
* JUnit
Allows JUnit-format test results to be published.
1.53	
	
* JWT for Blue Ocean
BlueOcean JWT plugin: Enables JWT based BlueOcean API authentication
1.25.2	
	
* Kubernetes
This plugin integrates Jenkins with Kubernetes
1.31.0	
	
* Kubernetes Client API
Kubernetes Client API plugin for use by other Jenkins plugins.
5.10.1-171.vaa0774fb8c20	
	
* Kubernetes Credentials Plugin
Common classes for Kubernetes credentials
0.9.0	
	
* Lockable Resources
This plugin allows to define external resources (such as printers, phones, computers) that can be locked by builds. If a build requires an external resource which is already locked, it will wait for the resource to be free.
2.12	
	
* Mailer
This plugin allows you to configure email notifications for build results. 
1.34		
	
* MapDB API Plugin
This plugin provides a shared dependency on the MapDB library so that other plugins can co-operate when using this library.
1.0.9.0		
	
* Matrix Authorization Strategy
Offers matrix-based security authorization strategies (global and per-project).
2.6.11	
	
* Matrix Project
Multi-configuration (matrix) project type. 
1.19	
	
* Mercurial
This plugin integrates Mercurial SCM with Jenkins. It includes repository browsing support for hg serve/hgweb, as well as hosted services like Google Code. Features include guaranteed clean builds, named branch support, module lists, Mercurial tool installation, and automatic caching.
 
2.16	
	
* Metrics
This plugin exposes the Metrics API to Jenkins plugins.  
4.0.2.8	
	
* OAuth Credentials plugin
This plugin provides interfaces for OAuth credentials in Jenkins.
0.5	
	
OkHttp Plugin
This plugin provides OkHttp for other plugins.
4.9.3-105.vb96869f8ac3a	
	
OpenShift Client
This plugin provides Jenkins pipeline DSL interactions for OpenShift.
1.0.35		
	
OpenShift Login
Allows you to log in to Jenkins via OAuth to an OpenShift installation
1.0.26		
	
OpenShift Sync
Sync your OpenShift BuildConfigs with Jenkins jobs.
1.0.50	
	
Oracle Java SE Development Kit Installer Plugin
Allows the Oracle Java SE Development Kit (JDK) to be installed via download from Oracle's website.
1.5	
	
OWASP Markup Formatter
Uses the OWASP Java HTML Sanitizer to allow safe-seeming HTML markup to be entered in project descriptions and the like.
2.5	
	
PAM Authentication
Adds Unix Pluggable Authentication Module (PAM) support to Jenkins
1.6.1	
	
Personalization for Blue Ocean
Blue Ocean Personalization
1.25.2	
	
Pipeline Graph Analysis
Provides a REST API to access pipeline and pipeline run data.
1.12	
	
Pipeline implementation for Blue Ocean
This plugin is a part of BlueOcean Plugin
1.25.2	
	
Pipeline SCM API for Blue Ocean
This plugin is a part of BlueOcean Plugin 
1.25.2	
	
Pipeline Utility Steps
Utility steps for pipeline jobs.
2.11.0		
	
Pipeline: API
Plugin that defines Pipeline API.
1105.v3de5e2efac97	
	
Pipeline: Basic Steps
Commonly used steps for Pipelines.
2.24	
	
Pipeline: Build Step
Adds the Pipeline step build to trigger builds of other jobs. 
2.15	
	
Pipeline: Declarative
An opinionated, declarative Pipeline.
1.9.3	
	
Pipeline: Declarative Extension Points API
APIs for extension points used in Declarative Pipelines.
1.9.3	
	
Pipeline: Deprecated Groovy Libraries
Shared libraries for Pipeline scripts. 552.vd9cc05b8a2e1	
	
Pipeline: Groovy
Pipeline execution engine based on continuation passing style transformation of Groovy scripts. 
2640.v00e79c8113de	
	
Pipeline: Input Step
Adds the Pipeline step input to wait for human input or approval.
427.va6441fa17010	
	
Pipeline: Job
Defines a new job type for pipelines and provides their generic user interface.
2.42	
	
Pipeline: Milestone Step
Plugin that provides the milestone step
1.3.2	
	
Pipeline: Model API
Model API for Declarative Pipeline.
1.9.3	
	
Pipeline: Multibranch
Enhances Pipeline plugin to handle branches better by automatically grouping builds from different branches.
 
2.26	
	
Pipeline: Nodes and Processes
Pipeline steps locking agents and workspaces, and running external processes that may survive a Jenkins restart or agent reconnection.
1102.v9c8d2f466adb	
	
Pipeline: REST API
Provides a REST API to access pipeline and pipeline run data.
2.19	
	
Pipeline: SCM Step
Adds a Pipeline step to check out or update working sources from various SCMs (version control).
2.13	
	
Pipeline: Stage Step
Adds the Pipeline step stage to delineate portions of a build.
2.5	
	
Pipeline: Stage Tags Metadata
Library plugin for Pipeline stage tag metadata.
1.9.3	
	
Pipeline: Stage View
Pipeline Stage View Plugin.
2.19		
	
Pipeline: Step API
API for asynchronous build step primitive.
613.v375732a042b1	
	
Pipeline: Supporting APIs
Common utility implementations to build Pipeline Plugin
3.8	
	
Plain Credentials
Allows use of plain strings and files as credentials.
1.7		
	
Plugin Utilities API
Provides utility classes that can be used to accelerate plugin development.
2.6.0	
	
Popper.js 2 API
Provides Popper.js for Jenkins Plugins. Popper can easily position tooltips, popovers or anything else with just a line of code.
2.10.2-1		
	
Popper.js API
Provides Popper.js for Jenkins plugins.
1.16.1-2		
	
Pub-Sub "light" Bus
A simple Publish-Subscribe light-weight event bus for Jenkins
1.16	
	
REST API for Blue Ocean
This plugin is a part of Blue Ocean UI
1.25.2	
	
REST Implementation for Blue Ocean
This plugin is a part of Blue Ocean UI
1.25.2	
	
Role-based Authorization Strategy
Enables user authorization using a Role-Based strategy. Roles can be defined globally or for particular jobs or nodes selected by regular expressions.
This plugin is up for adoption! We are looking for new maintainers. Visit our Adopt a Plugin initiative for more information.
3.2.0		
	
SCM API
This plugin provides a new enhanced API for interacting with SCM systems.
2.6.5	
	
Script Security
Allows Jenkins administrators to control what in-process scripts can be run by less-privileged users. 
1.78	
	
Server Sent Events (SSE) Gateway
Server Sent Events (SSE) Gateway.
1.24		
	
SnakeYAML API
This plugin provides Snakeyaml for other plugins.
1.29.1	
	
SonarQube Scanner for Jenkins
This plugin allows an easy integration of SonarQube, the open source platform for Continuous Inspection of code quality.
2.14	
	
SSH Credentials
Allows storage of SSH credentials in Jenkins
1.19	
	
SSH server
Adds SSH server functionality to Jenkins, exposing CLI commands through it.
3.1.0	
	
Structs
Library plugin for DSL plugins that need names for Jenkins objects.
308.v852b473a2b8c	
	
Subversion
 
2.15.1	
	
Token Macro
This plug-in adds reusable macro expansion capability for other plug-ins to use.
267.vcdaea6462991	
	
Trilead API
Trilead API Plugin provides the Trilead library to any dependent plugins in an easily update-able manner.
1.0.13		
	
Variant Plugin
This user-invisible library plugin allows other multi-modal plugins to behave differently depending on where they run.
1.4		
	
Web for Blue Ocean
Blue Ocean core
1.25.2	
	
WMI Windows Agents
Allows you to setup agents on Windows machines over Windows Management Instrumentation (WMI)
 
1.8	
	
xUnit
This plugin makes it possible to record xUnit test reports.
3.0.5	 
## Colaboración
  Pipeline realizada por T-Systems, con APB.
