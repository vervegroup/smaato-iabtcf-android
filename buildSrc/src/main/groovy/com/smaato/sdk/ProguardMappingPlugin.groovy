package com.smaato.sdk

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip

class ProguardMappingPlugin implements Plugin<Project> {

    public static final String MAPPING_TASK = "zipProguardMapping"

    @Override
    void apply(Project project) {
        registerZipProguardMappingTasks(project)
        project.afterEvaluate {
            def moveMapping = registerMoveProguardMappingTasks(project)
            project.tasks.withType(AbstractPublishToMaven) { task ->
                def publishingRestricted = task instanceof PublishToMavenRepository && "Public" == task.name
                if (!publishingRestricted) {
                    task.dependsOn moveMapping.get()
                }
            }
        }
    }

    private static TaskProvider registerMoveProguardMappingTasks(Project project) {
        return project.tasks.register("moveMapping", Copy) {
            group 'Packaging'
            def dir = new File("${project.buildDir}/outputs/mapping/release/")
            from dir
            include '*.txt'
            into "${project.buildDir}/outputs/mapping/archive/"
        }
    }

    private static TaskProvider registerZipProguardMappingTasks(Project project) {
        return project.tasks.register(MAPPING_TASK, Zip) {
            group 'Packaging'
            description "Save Proguard mapping into zip archive"
            def dir = new File("${project.buildDir}/outputs/mapping/archive/")
            from dir
            include '*.txt'
            archiveClassifier.set("mapping")
            into "${project.name}-mapping"
            enabled = dir.exists()
            doLast {
                project.delete(dir)
            }
        }
    }
}