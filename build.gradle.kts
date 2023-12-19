plugins {
    id("java")
    id("maven-publish")
//    id("net.minecraftforge.gradle") version "6.+"
}

group = "equilinoxmodkit"
version = "0.5.0-SNAPSHOT"

val os = System.getProperty("os.name").lowercase()
val equilinoxExecutableJar = when {
    os.contains("win") -> "${projectDir}/Equilinox/EquilinoxWindows.jar"
    os.contains("nix") || os.contains("nux") -> "${projectDir}/Equilinox/input.jar"
    os.contains("mac") -> "${projectDir}/Equilinox/EquilinoxMac.app/Contents/Java/EquilinoxWindows.jar"
    else -> throw GradleException("Unsupported operating system")
}

repositories {
    mavenCentral()
    maven {
        name = "spongepowered-repo"
        url = uri("https://repo.spongepowered.org/maven/")
    }
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
    
    
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.22.0")
    implementation("net.sf.jopt-simple:jopt-simple:4.9")
    implementation("net.minecraft:launchwrapper:1.12")
//    implementation("net.minecraftforge.gradle:ForgeGradle:6.+") // Forge Gradle plugin
    implementation("org.spongepowered:mixin:0.7.10-SNAPSHOT") {
        exclude(module = "launchwrapper")
    }
    
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly(files(equilinoxExecutableJar))
    
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to "equilinoxmodkit.Main",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
        )
    }
}

// Configure Javadoc and source tasks
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    
    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }
    
    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }
}
