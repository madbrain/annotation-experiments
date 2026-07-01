package com.github.madbrain.playmobuild.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.github.madbrain.playmobuild.api.PlaymoBuild")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class PlaymoBuildProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.forEach(element -> {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Annotation on " + element);
            });
        });
        return true;
    }

}
