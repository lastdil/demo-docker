import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.1"

project {

    vcsRoot(DemoDocker)

    buildType(Build_2)
    buildType(BuildDockerImage)

    template(Build)
}

object BuildDockerImage : BuildType({
    name = "build docker image"

    vcs {
        root(DemoDocker)
    }

    steps {
        dockerCommand {
            commandType = build {
                source = path {
                    path = "Dockerfile"
                }
                namesAndTags = "lastdil/spring-test-app:${Build_2.depParamRefs.buildNumber}"
                commandArgs = "--pull"
            }
            param("dockerImage.platform", "linux")
        }
        dockerCommand {
            commandType = push {
                namesAndTags = "lastdil/spring-test-app:${Build_2.depParamRefs.buildNumber}"
            }
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${Build_2.id}"
            successfulOnly = true
            branchFilter = "+:test"
        }
    }

    features {
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_6"
            }
        }
    }

    dependencies {
        artifacts(Build_2) {
            buildRule = lastSuccessful("test")
            artifactRules = "demo-0.0.1-SNAPSHOT.jar"
        }
    }
})

object Build_2 : BuildType({
    name = "Build"

    artifactRules = "target/*.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        script {
            enabled = false
            scriptContent = "git ls-remote origin 'pull/*/merge'"
        }
        dockerCommand {
            enabled = false
            commandType = build {
                source = path {
                    path = "Dockerfile"
                }
                namesAndTags = "lastdil/spring-test:%build.number%"
                commandArgs = "--pull"
            }
        }
        dockerCommand {
            enabled = false
            commandType = push {
                namesAndTags = "lastdil/spring-test:%build.number%"
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_6"
            }
        }
    }
})

object Build : Template({
    name = "Build"

    vcs {
        root(DemoDocker)
    }

    triggers {
        vcs {
            id = "vcsTrigger"
        }
    }
})

object DemoDocker : GitVcsRoot({
    name = "demo-docker"
    url = "https://github.com/lastdil/demo-docker.git"
    branch = "refs/heads/test"
    branchSpec = "+:refs/heads/*"
    authMethod = password {
        userName = "lastdil"
        password = "credentialsJSON:9f11828d-8751-44ad-98a6-7f1c6d72711a"
    }
})
