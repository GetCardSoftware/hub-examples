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

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol = true
            url = uri("https://dev-hubpay-getcard.cloudsg.com.br/repo/repository/getcard-public/")
            credentials {
                username = "getcard-public"
                password = "!V4V4xEDUl0GC\$HK*nVMtvi*Q93mjzQKZn*6U%g\$"
            }
        }
    }
}

rootProject.name = "Hub Core Pinpad PDV Example"
include(":app")
 