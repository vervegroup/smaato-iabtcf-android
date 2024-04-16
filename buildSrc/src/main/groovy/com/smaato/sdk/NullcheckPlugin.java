package com.smaato.sdk;

import com.android.annotations.NonNull;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;


public class NullcheckPlugin implements Plugin<Project> {

    @Override
    public void apply(@NonNull Project project) {
        project.getPluginManager().withPlugin("com.android.library", plugin -> {
            final LibraryExtension android = project.getExtensions().getByType(LibraryExtension.class);
            final Configuration compileOnly = project.getConfigurations().findByName("compileOnly");
            if (compileOnly != null) {
                project.getConfigurations().create("nullcheck").extendsFrom(compileOnly);
            }
            android.registerTransform(new NullcheckTransform(project, android));
        });
    }

}
