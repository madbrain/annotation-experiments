package com.github.madbrain.playmobuild.processor;

import com.github.madbrain.playmobuild.api.Inline;
import com.github.madbrain.playmobuild.api.Required;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.config.EasyFactoryConfiguration;
import org.apache.velocity.tools.generic.DisplayTool;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes("com.github.madbrain.playmobuild.api.PlaymoBuild")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class PlaymoBuildProcessor extends AbstractProcessor {

    private VelocityEngine velocityEngine;
    private ToolManager toolManager;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.forEach(element -> {
                if (element.getKind() != ElementKind.RECORD) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@PlaymoBuild annotation only apply to records");
                } else {
                    try {
                        generateBuilder((TypeElement) element);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        return true;
    }

    private VelocityEngine getVelocityEngine() {
        if (velocityEngine == null) {
            var config = new EasyFactoryConfiguration();
            config.toolbox("application").tool(DisplayTool.class);
            toolManager = new ToolManager(true);
            toolManager.configure(config);
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init();
            this.velocityEngine = velocityEngine;
        }
        return velocityEngine;
    }

    private void generateBuilder(TypeElement element) throws IOException {
        var className = element.getQualifiedName().toString();

        var fields = element.getRecordComponents().stream()
                .map(e -> {
                    var isRequired = e.getAnnotation(Required.class) != null;
                    var isInline = e.getAnnotation(Inline.class);
                    if (isInline != null && (e.asType().getKind() != TypeKind.DECLARED
                            || !((DeclaredType)e.asType()).asElement().getSimpleName().toString().equals("List"))) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Inline only apply to List", e);
                        isInline = null;
                    }
                    return new FieldModel(e.asType(), e.getSimpleName(), isRequired, isInline);
                })
                .toList();

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                "Generate builder for " + className);

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            Template t = getVelocityEngine().getTemplate("builder.vm");
            Context context = toolManager.createContext();
            context.put("packageName", packageName);
            context.put("className", className);
            context.put("simpleClassName", simpleClassName);
            context.put("builderClassName", builderClassName);
            context.put("builderSimpleClassName", builderSimpleClassName);
            context.put("fields", fields);
            context.put("requiredFields", fields.stream().filter(FieldModel::isRequired).toList());
            context.put("optionalFields", fields.stream().filter(f -> !f.isRequired()).toList());

            t.merge(context, out);
        }
    }

    public record FieldModel(TypeMirror type, Name name, boolean isRequired, Inline isInline) { }
}
