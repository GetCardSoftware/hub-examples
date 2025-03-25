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
            url = uri("https://dev-hubpay.tefbr.com.br/repo/repository/libs-getcard/")
            credentials {
                username = "admin"
                password = providers.gradleProperty("GETCARD_HOMOLOG_PASSWORD").orNull ?: ""
            }
        }
        maven {
            isAllowInsecureProtocol = true
            url = uri("https://dev-hubpay.tefbr.com.br/repo/repository/getcard-public/")
            credentials {
                username = "getcard-public"
                password = "!V4V4xEDUl0GC\$HKnVMtvQ93mjzQKZn*6U%g\$"
            }
        }
    }
}

rootProject.name = "Hub Complete POS Example"
include(":app")
 