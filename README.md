
# template-acceptance-tests

This repo contain the acceptance tests for the [template] service.

It is built using:

cucumber 1.2.4

java 1.8

Scala 2.11.7

SBT to build 0.13.16

Getting started
Ensure that you have a working linux environment.

Execution
In /src/test/scala/uk/gov/hmrc/integration/cucumber there are scala classes which control what is run according to the tests tagged with the below tags. The main class is Runner which selects and runs tests marked @. You can run ./run_integration_local.sh to run tests against a local version of the application

###  Test Automation environment setup

    1. Ensure that you have installed InteliJ Idea
    2. Once you have installed IDEA install Cucumber for scala
    3. Clone this repo.
    4. Import the project into InteliJ
    
###  Project structure
Each part of the application's functionality is described by feature files. The feature files are arranged into folders under src/test/features and grouped into the main areas of the application.
Each step of the feature files is defined by executable test steps in the scala code under the src/test/scala/uk/gov/hmrc/integration/test/stepdefs area and those utilise Page object models under src/test/scala/uk/gov/hmrc/integration/cucumber/pages which are the single place where page specific properties and variables are configured.

###  Example Feature
The example feature calls the Authority Wizard page and relies on the following services being started :


    ASSETS_FRONTEND
    AUTH
    AUTH_LOGIN_API
    AUTH_LOGIN_STUB
    USER_DETAILS


### Browser Testing
In order to run the tests via BrowserStack you need to create the following files with your BrowserStack username and automate key: 
 
  ~/Applications/hmrc-development-environment/hmrc/itr-acceptance-tests/src/test/resources/browserConfig.properties
 

To get your username and automate key go [here](https://www.browserstack.com/accounts/settings)
Alternatively if you access[www.browserstack.com/automate](http://www.browserstack.com/automate)and select **Username and Access Keys** on the left tab your credentials will be displayed 

**Note** browserstack.com/accounts/settings displays the automate key as the access key.


Then you need to change the project name and description within the SingletonDriver.scala file.
You can use the search everywhere function within IntelliJ (Ctrl + Shift + F) to find these entries.
 - desCaps.setCapability("project", "projectName")
 - desCaps.setCapability("build", "your project name Build_1.X")
 
For our first run this was set to:
 - desCaps.setCapability("project", "projectName")
 - desCaps.setCapability("build", "Local Complete TestBuild_0.1")

To add a browser to be tested via BrowserStack, a json object needs to be created within the itr-acceptance-tests/src/test/resources/browserstackdata folder with the following information:
Note ideally these should be representing the latest versions of the chosen browser and/or devices.
 
    Desktop browser:
        - browser
        - browser_version
        - os
        - os_version
           
    Mobile Browser:
        - browserName
        - platform
        - device

The title of the JSON file should be as follows: 
BS_OS/Device_Version_Browser_BrowserVersion

For Example:

    BS_Win_8_1_Chrome_38

Once the JSON objects have been created, these need to be added to the run_browserstack.sh script.

To execute select **run_browserstack.sh** script

Note i - if you only wish to run either the browsers or devices you need to remove the relevant entry from within run_browserstack.sh

Note ii - the changes made to browserConfig.properties should not be pushed to GitHub and therefore you should make sure that this file is included on the gitignore file for your project

 ### Messages
    
 If there is a requirement to verify any text within your application users should create a messages.properties file and place it in the following location where it can be referenced:
  
  ~/Applications/pe/template-acceptance-tests/src/test/resources/   
