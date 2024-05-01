import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import com.apadmi.mockzilla.JavaConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
}

kotlin {
    jvm {
        jvmToolchain(JavaConfig.toolchain)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":mockzilla-management-ui:common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "mockzilla-management-ui"
            packageVersion = rootProject.version.toString().split("-").first()
        }
    }
}