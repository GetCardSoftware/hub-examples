pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://192.168.1.250:8000/repository/libs-getcard/")
            credentials {
                username = "getcard-public"
                password = "!V4V4xEDUl0GC\$HK*nVMtvi*Q93mjzQKZn*6U%g\$"
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://192.168.1.250:8000/repository/libs-getcard/")
            credentials {
                username = "getcard-public"
                password = "!V4V4xEDUl0GC\$HK*nVMtvi*Q93mjzQKZn*6U%g\$"
            }
        }
    }
}

rootProject.name = "Hub Core Pinpad Complete Example"
include(":app")
 