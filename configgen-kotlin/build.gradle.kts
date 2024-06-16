import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

}

sourceSets {
    main {
        java {
            srcDir("${rootProject.rootDir.absolutePath}/configgen-core/src/main/java")
        }
    }
    test {
        kotlin {
            srcDir("${rootProject.rootDir.absolutePath}/configgen-core/src/test/kotlin")
        }
    }

}



dependencies {
    implementation(libs.bundles.codeGenCore)
    implementation(libs.poet.kotlin)
    testImplementation(libs.junit)
    testImplementation(libs.truth)

}

val ARTIFACT_ID = "com.location.configGen-kotlin"

gradlePlugin {
    plugins {
        create("com.location.configGen-kotlin") {
            id = ARTIFACT_ID
            implementationClass = "com.location.configgen.core.ConfigGenPlugin"
        }
    }
}
tasks.register("publishSourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

tasks.register("publishJavadocsJar", Jar::class) {
    dependsOn("publishSourcesJar")
    archiveClassifier.set("javadoc")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                groupId = "com.location.configGen-kotlin"
                artifactId = "$ARTIFACT_ID.gradle.plugin"

                version = "1.0.1"
                artifact(tasks.getByName("publishSourcesJar"))
                artifact(tasks.getByName("publishJavadocsJar"))
            }
        }
        repositories {
            maven {
                url = uri("${rootDir.absolutePath}/localRepo")
            }
        }
    }

}