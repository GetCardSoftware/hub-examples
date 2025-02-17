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
                // Defina as credenciais a serem usadas na publicação
                username = "admin"
                password = "web123"
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
                // Defina as credenciais a serem usadas na publicação
                username = "admin"
                password = "web123"
            }
        }
    }
}

rootProject.name = "Hub Core Pinpad Complete Example"
include(":app")
 