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
            url = uri("https://dev-hubpay.tefbr.com.br/repo/repository/getcard-public/")
            credentials {
                username = "getcard-public"
                password = "aq:z.wQy^w#.Zg[)1k>|D#QNK}JgL5GL"
            }
        }
    }
}

rootProject.name = "Hub Simple Example"
include(":app")
 