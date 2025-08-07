plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.4"
}

repositories {
    mavenCentral()
    google() // required for Dagger
}

dependencies {
    // Testing
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Application
    implementation(libs.guava)

    // AWS Lambda
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.4")
    
    // AWS SDK v2
    implementation("software.amazon.awssdk:s3:2.32.6")
    implementation("software.amazon.awssdk:bedrockagentruntime:2.32.6")
    implementation("software.amazon.awssdk:bedrock:2.32.6")
    implementation("software.amazon.awssdk:bedrockruntime:2.32.6")
    implementation("software.amazon.awssdk:url-connection-client:2.32.6")
    implementation("software.amazon.awssdk:dynamodb:2.32.6")
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.32.6")
    
    // AWS Lambda events
    implementation("com.amazonaws:aws-lambda-java-events:3.11.4")

    // JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.15.2")

    // Dagger
    implementation("com.google.dagger:dagger:2.48.1")
    annotationProcessor("com.google.dagger:dagger-compiler:2.48.1")
    
    // Log4j2
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    
    // AWS Lambda Log4j2 adapter
    implementation("com.amazonaws:aws-lambda-java-log4j2:1.5.1")
    
    // Lombok - annotation processor
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // Lombok for tests
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}



java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "org.example.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveBaseName.set("lambda")
        archiveClassifier.set("")
        archiveVersion.set("")
        mergeServiceFiles()
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer())
    }

    register<Zip>("buildLambdaZip") {
        dependsOn("shadowJar")
        from("${layout.buildDirectory}/libs/lambda.jar")
        archiveFileName.set("lambda-deployment.zip")
        destinationDirectory.set(file("${layout.buildDirectory}/dist"))
    }

    register<JavaExec>("generateSchema") {
        dependsOn("compileJava")
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("com.sorta.service.utils.SchemaGeneratorMain")
        args = listOf("${layout.buildDirectory.get()}/resources/sorta-agent-message-schema.json")
    }

    build {
        dependsOn("buildLambdaZip", "generateSchema")
    }

    compileJava {
        options.release.set(17)
    }
}