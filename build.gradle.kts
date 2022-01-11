plugins {
    java
}

allprojects {
    group = "org.glavo.galerio"
    version = "0.1.0-alpha" + "SNAPSHOT"

    apply {
        plugin("java")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(Dependencies.JETBRAINS_ANNOTATIONS)

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    tasks.compileJava {
        options.release.set(8)
        options.encoding = "UTF-8"
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
    }

    tasks.test {
        useJUnitPlatform()
    }
}
