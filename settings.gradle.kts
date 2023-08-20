import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}

rootProject.name = "FetchCookie"
include(":app")
include(":platform")
include(":platform:imaotai")
include(":platform:core")
include(":platform:mini_maotai")
include(":MiniProgramMaoTai")
