import org.jooq.meta.jaxb.MatcherTransformType

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jooq.jooq-codegen-gradle") version "3.19.21"
}

group = "io.github.kamilperczynski"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    jooqCodegen("org.jooq:jooq-meta-extensions:3.19.21")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.kafka:spring-kafka")
    // add sl4j and logback
    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    java {
        sourceSets.main {
            java.srcDir("build/generated-jooq")
        }
    }
}

tasks.compileKotlin {
    dependsOn(tasks.jooqCodegen)
}

jooq {
    configuration {
        generator {
            strategy {
                matchers {
                    tables {
                        table {
                            expression = "([\\w_]+)"
                            tableClass {
                                transform = MatcherTransformType.PASCAL
                                expression = "T_$1"
                            }
                        }
                    }
                }
            }

            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {

                    property {
                        key = "scripts"
                        value = "src/main/resources/database.sql"
                    }

                    property {
                        key = "sort"
                        value = "semantic"
                    }

                    property {
                        key = "unqualifiedSchema"
                        value = "none"
                    }

                    property {
                        key = "defaultNameCase"
                        value = "as_is"
                    }
                }
            }
            target {
                packageName = "io.github.kamilperczynski.kreversejoin.db"

                // The destination directory of your generated classes
                directory = "build/generated-jooq"
            }
        }
    }
}
