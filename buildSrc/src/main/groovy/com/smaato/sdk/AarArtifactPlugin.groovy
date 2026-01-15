package com.smaato.sdk

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.AbstractModuleDependency
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocMemberLevel
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

class AarArtifactPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply("com.android.library")
        project.pluginManager.apply('proguard-mapping')

        def android = project.extensions.findByType(LibraryExtension)

        configureDefaultConfig(project, android)
        configureBuildTypes(project, android)
        configureCompileOptions(android)

        configureTestOptions(android)
        configureVariantTasks(project, android)

        configureLintOptions(project, android)
        configureCheckstyleTasks(project, android)

        project.dependencies {
            api Deps.ax.annotation
        }
    }

    private static void configureDefaultConfig(Project project, LibraryExtension android) {
        android.compileSdk(33)
        android.defaultConfig {
            minSdkVersion 16
            targetSdkVersion 33
            versionCode project.versionCode
            versionName project.versionName
            testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        }

        android.sourceSets.maybeCreate("test").getResources().srcDir(
                project.getRootProject().fileTree("buildSrc/robolectric")
        )
    }

    private static void configureBuildTypes(Project project, LibraryExtension android) {
        android.buildTypes {
            release {
                minifyEnabled true
                proguardFile android.getDefaultProguardFile("proguard-android.txt")
                proguardFile project.rootProject.file("proguard/proguard-rules.pro")
                proguardFile project.file("proguard-rules.pro")
                consumerProguardFile project.rootProject.file("proguard/consumer-proguard-rules.pro")
                consumerProguardFile project.file("consumer-proguard-rules.pro")
            }
        }
    }

    private static void configureCompileOptions(LibraryExtension android) {
        android.compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
        }
    }

    private static void configureTestOptions(LibraryExtension android) {
        android.testOptions {
            unitTests {
                includeAndroidResources = true
                returnDefaultValues = true
                all {
                    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
                    testLogging {
                        events 'failed', 'standardError'
                        outputs.upToDateWhen { false }
                    }
                    jvmArgs '-noverify'

                    //https://stackoverflow.com/a/71834475/1442571
                    jacoco {
                        includeNoLocationClasses = true
                        jacoco.excludes = ['jdk.internal.*']
                    }
                }
            }
        }
    }

    private static void configureVariantTasks(Project project, LibraryExtension android) {
        project.pluginManager.apply("jacoco")
        project.extensions.findByType(JacocoPluginExtension).with {
            toolVersion = Deps.versions.test.jacoco
        }
        project.extensions.create("javadoc", JavadocExtension)
        android.libraryVariants.all { variant ->
            registerJacocoTasks(project, variant)
            registerJavadocTasks(project, android, variant)
            project.pluginManager.withPlugin("maven-publish") {
                registerPublishTasks(project, variant)
            }
        }
    }

    private static configureLintOptions(Project project, LibraryExtension android) {
        android.lintOptions {
            lintConfig project.rootProject.file("quality/lint/lint.xml")
            checkGeneratedSources false
            ignoreTestSources true
            abortOnError false
            xmlReport true
            xmlOutput project.file("${project.buildDir}/reports/lint/lint-report.xml")
            htmlReport false
        }
    }

    private static configureCheckstyleTasks(Project project, LibraryExtension android) {
        project.pluginManager.apply("checkstyle")
        project.extensions.findByType(CheckstyleExtension).with {
            toolVersion = Deps.versions.checkstyle
            configFile = project.rootProject.file("quality/checkstyle/checkstyle.xml")
            configProperties.checkstyleSuppressionFilterPath = project.rootProject.file("quality/checkstyle/suppressions.xml").absolutePath
            ignoreFailures = true
        }
        project.tasks.register("checkstyle", Checkstyle) {
            group 'Verification'
            description "Run Checkstyle analysis"
            source android.sourceSets.main.java.sourceFiles
            exclude '**/R.java'
            exclude '**/BuildConfig.java'
            classpath = project.files(android.bootClasspath.join(File.pathSeparator))
            reports {
                xml.enabled true
                xml.destination project.file("${project.buildDir}/reports/checkstyle/checkstyle-report.xml")
            }
            outputs.upToDateWhen { false }
        }
    }

    private static void registerJacocoTasks(Project project, LibraryVariant variant) {
        project.tasks.register("jacoco${variant.name.capitalize()}UnitTestReport", JacocoReport) {
            group = 'Reporting'
            description = "Generates Jacoco coverage reports on the ${variant.name} variant"
            reports {
                xml.enabled = true
                html.enabled = true
            }
            sourceDirectories.from(project.files(variant.sourceSets.java.srcDirs))
            classDirectories.from(project.fileTree(dir: variant.javaCompileProvider.get().destinationDir, exclude: [
                    '**/R.class',
                    '**/R$*.class',
                    '**/BuildConfig.*',
                    '**/Manifest*.*',
                    '**/*Test*.*',
                    'android/**/*.class',
                    '**/testapp/**/*.class'
            ]))
            executionData.from(project.fileTree(dir: "${project.buildDir}/jacoco"))
            dependsOn project.tasks.named("test${variant.name.capitalize()}UnitTest")
        }
    }

    private static void registerJavadocTasks(Project project, LibraryExtension android, LibraryVariant variant) {
        def javadoc = project.extensions.findByType(JavadocExtension)
        def javadocTask = project.tasks.register("javadoc${variant.name.capitalize()}", Javadoc) {
            group = "Documentation"
            description = "Generates Javadoc"
            title = null
            failOnError false
            source = android.sourceSets.main.java.srcDirs
            include(javadoc.sources)
            classpath = project.files(
                    android.bootClasspath.join(File.pathSeparator),
                    variant.javaCompileProvider.get().classpath
            )

            options.memberLevel = JavadocMemberLevel.PUBLIC

            if (JavaVersion.current().isJava9Compatible()) {
                options.addBooleanOption('html5', true)
            }

            enabled = !javadoc.sources.isEmpty()
        }

        project.tasks.register("javadoc${variant.name.capitalize()}Jar", Jar) {
            group = "Packaging"
            dependsOn javadocTask
            archiveClassifier.set("javadoc")
            from javadocTask.get().destinationDir
            enabled = javadocTask.get().enabled
        }
    }

    private static void registerPublishTasks(Project project, LibraryVariant variant) {
        def publishing = project.extensions.findByType(PublishingExtension)
        publishing.publications {
            register(variant.name.capitalize(), MavenPublication) {
                groupId project.group
                artifactId project.name
                version project.version
                artifact variant.packageLibraryProvider.get()
                def javadocJar = project.tasks.named("javadoc${variant.name.capitalize()}Jar").get()
                if (javadocJar.enabled) {
                    artifact javadocJar
                }
                def zipProguardMapping = project.tasks.named(ProguardMappingPlugin.MAPPING_TASK).get()
                if (zipProguardMapping.enabled) {
                    artifact zipProguardMapping
                }

                pom.withXml {
                    def dependencies = asNode().appendNode('dependencies')
                    [
                            (project.configurations.api)           : "compile",
                            (project.configurations.runtimeOnly)   : "compile",
                            (project.configurations.implementation): "runtime"
                    ].each { configuration, scope ->
                        configuration.dependencies.withType(AbstractModuleDependency).each {
                            def dependency = dependencies.appendNode('dependency')
                            dependency.appendNode('groupId', it.group)
                            dependency.appendNode('artifactId', it.name)
                            dependency.appendNode('version', it.version)
                            dependency.appendNode('scope', scope)
                        }
                    }
                }
            }
        }
    }
}