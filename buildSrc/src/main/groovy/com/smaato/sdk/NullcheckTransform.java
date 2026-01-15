package com.smaato.sdk;

import com.android.SdkConstants;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.LibraryExtension;
import com.android.utils.FileUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import kotlin.io.FilesKt;

public class NullcheckTransform extends Transform {

    private final Project project;

    private final LibraryExtension android;

    NullcheckTransform(Project project, LibraryExtension android) {
        this.project = project;
        this.android = android;
    }

    @Override
    public String getName() {
        return "nullcheck";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(QualifiedContent.DefaultContentType.CLASSES);
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return ImmutableSet.of(QualifiedContent.Scope.PROJECT);
    }

    @Override
    public Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return ImmutableSet.of(
                QualifiedContent.Scope.EXTERNAL_LIBRARIES,
                QualifiedContent.Scope.SUB_PROJECTS
        );
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        try {
            final File outputDir = transformInvocation.getOutputProvider().getContentLocation(
                    SdkConstants.FD_CLASSES_OUTPUT, getOutputTypes(), getScopes(), Format.DIRECTORY);
            final ClassPool classPool = classPoolOf(transformInvocation);
            for (TransformInput input : transformInvocation.getInputs()) {
                for (DirectoryInput dir : input.getDirectoryInputs()) {
                    for (Map.Entry<File, Status> entry : dir.getChangedFiles().entrySet()) {
                        if (Status.NOTCHANGED == entry.getValue() || Status.REMOVED == entry.getValue()) {
                            continue;
                        }
                        if (entry.getKey().getAbsolutePath().endsWith(SdkConstants.DOT_CLASS)) {
                            transform(dir.getFile(), entry.getKey(), classPool, outputDir);
                        }
                    }
                    if (!transformInvocation.isIncremental()) {
                        transformInvocation.getOutputProvider().deleteAll();
                        for (File file : FileUtils.getAllFiles(dir.getFile())) {
                            if (file.getAbsolutePath().endsWith(SdkConstants.DOT_CLASS)) {
                                transform(dir.getFile(), file, classPool, outputDir);
                            }
                        }
                    }
                }
            }
        } catch (NotFoundException e) {
            throw new IOException(e);
        } catch (CannotCompileException e) {
            throw new TransformException(e);
        }
    }

    private void transform(File inputDir, File file, ClassPool classPool, File outputDir)
            throws NotFoundException, CannotCompileException, IOException {
        final CtClass clazz = classPool.get(
                relativePath(file, inputDir)
                        .replace(SdkConstants.DOT_CLASS, "")
                        .replace(File.separator, "."));
        for (CtMethod method : clazz.getDeclaredMethods()) {
            final int accessFlags = method.getMethodInfo().getAccessFlags();
            if (isAbstract(accessFlags) || isSyntheticOrBridged(accessFlags)) {
                continue;
            }
            final Map<String, String> params = getNonNullParameters(method);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                method.insertBefore("if (" + entry.getValue() + " == null) " +
                        "throw new NullPointerException(\"'" + entry.getKey() + "' specified as non-null is null\");");
            }
        }
        clazz.writeFile(outputDir.getAbsolutePath());
        clazz.defrost();
    }

    private ClassPool classPoolOf(TransformInvocation invocation) throws NotFoundException {
        final ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();
        for (File file : android.getBootClasspath()) {
            classPool.appendClassPath(file.getAbsolutePath());
        }
        appendClassPath(classPool, invocation.getInputs());
        appendClassPath(classPool, invocation.getReferencedInputs());
        final Configuration nullcheck = project.getConfigurations().findByName("nullcheck");
        project.getLogger().lifecycle("nullcheck: " + nullcheck);
        if (nullcheck != null && nullcheck.isCanBeResolved()) {
            for (File file : nullcheck.resolve()) {
                classPool.appendClassPath(file.getAbsolutePath());
            }
        }
        return classPool;
    }

    private void appendClassPath(ClassPool classPool,
                                 Iterable<? extends TransformInput> inputs) throws NotFoundException {
        for (TransformInput input : inputs) {
            for (DirectoryInput dir : input.getDirectoryInputs()) {
                classPool.appendClassPath(dir.getFile().getAbsolutePath());
            }
            for (JarInput jar : input.getJarInputs()) {
                classPool.appendClassPath(jar.getFile().getAbsolutePath());
            }
        }
    }

    private static Map<String, String> getNonNullParameters(CtBehavior method) throws NotFoundException, CannotCompileException {
        final ImmutableMap.Builder<String, String> map = ImmutableMap.builder();
        final MethodInfo methodInfo = method.getMethodInfo();
        final CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        if (codeAttribute == null) {
            return Collections.emptyMap();
        }

        final LocalVariableAttribute parameters = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (parameters == null) {
            return Collections.emptyMap();
        }

        final ParameterAnnotationsAttribute annotations = (ParameterAnnotationsAttribute) methodInfo
                .getAttribute(ParameterAnnotationsAttribute.invisibleTag);
        if (annotations == null) {
            return Collections.emptyMap();
        }

        final CtClass[] parameterTypes = method.getParameterTypes();
        boolean isStatic = Modifier.isStatic(method.getModifiers());

        for (int i = parameters.tableLength() - 1; i >= 0; --i) {
            final int index = parameters.index(i) - (isStatic ? 0 : 1);
            if (index < 0 || index >= parameterTypes.length) {
                continue;
            }
            if (parameterTypes[index].isPrimitive()) {
                continue;
            }
            final String variableName = parameters.variableName(i);
            if (Arrays
                    .stream(annotations.getAnnotations()[index])
                    .map(Annotation::getTypeName)
                    .anyMatch(a -> a.endsWith("NonNull"))) {
                map.put(variableName, "$" + (index + 1));
            }
        }

        return map.build();
    }

    private static boolean isSyntheticOrBridged(int access) {
        return (((access & AccessFlag.SYNTHETIC) | (access & AccessFlag.BRIDGE)) > 0);
    }

    private static boolean isAbstract(int access) {
        return ((access & AccessFlag.ABSTRACT) > 0);
    }

    private static String relativePath(File file, File dir) {
        Preconditions.checkArgument(file.isFile() || file.isDirectory(), "%s is not a file nor a directory.", file.getPath());
        Preconditions.checkArgument(dir.isDirectory(), "%s is not a directory.", dir.getPath());
        return FilesKt.toRelativeString(file, dir);
    }
}
