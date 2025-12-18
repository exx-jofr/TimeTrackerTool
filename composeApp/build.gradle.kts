import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("org.apache.poi:poi-ooxml:5.5.0")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            implementation("org.apache.logging.log4j:log4j-api:2.25.2")
            implementation("org.apache.logging.log4j:log4j-core:2.25.2")
            implementation("androidx.datastore:datastore-preferences:1.2.0")
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.exxjofr.timetracker.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "TimeTracker"
            packageVersion = "1.0.0"

            windows {
                menuGroup = "TimeTracker"
                dirChooser = true
                perUserInstall = true
            }
        }
    }
}
