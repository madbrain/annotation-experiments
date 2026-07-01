package com.github.madbrain.playmobuild.processor;

import com.github.madbrain.playmobuild.api.Inline;
import com.github.madbrain.playmobuild.api.Required;

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
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("com.github.madbrain.playmobuild.api.PlaymoBuild")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class PlaymoBuildProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.forEach(element -> {
                if (element.getKind() != ElementKind.RECORD) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@PlaymoBuild annotation only apply to records");
                } else {
                    generateBuilder((TypeElement) element);
                }
            });
        });
        return true;
    }

    private void generateBuilder(TypeElement element) {
        var className = element.getQualifiedName().toString();

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        var fields = element.getRecordComponents().stream()
                .map(e -> {
                    return new FieldModel(e.asType(), e.getSimpleName());
                })
                .toList();

        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Generate " + builderClassName + " for " + className + " / " + fields);
    }

    public record FieldModel(TypeMirror type, Name name) { }

}
