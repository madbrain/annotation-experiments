package com.github.madbrain.blenderdna.processor;

import org.apache.commons.lang3.StringUtils;
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
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("org.github.madbrain.demo.blender.DNA")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class DnaGeneratorProcessor extends AbstractProcessor {
    private VelocityEngine velocityEngine;
    private ToolManager toolManager;
    private final Set<String> loaderNames = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        annotations.forEach(annotation -> {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.forEach(element -> {
                try {
                    generateLoader((TypeElement) element);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        if (roundEnv.processingOver()) {
            try {
                var serviceFile = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
                        "",
                        "META-INF/services/org.github.madbrain.demo.blender.loader.StructLoader");
                try (PrintWriter out = new PrintWriter(serviceFile.openWriter())) {
                    loaderNames.forEach(out::println);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    private void generateLoader(TypeElement element) throws IOException {
        var structType = new StructType(element);
        element.getEnclosedElements()
                .stream().filter(e -> e.getKind() == ElementKind.FIELD)
                .forEach(fieldElement -> {
                    structType.addField(fieldElement.getSimpleName(), makeType(fieldElement.asType()));
        });
        processingEnv.getMessager().printNote("Generate loder for " + element.getSimpleName());

        var className = element.getQualifiedName().toString();
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String loaderClassName = className + "Loader";
        String loaderSimpleClassName = loaderClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(loaderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            Template template = getVelocityEngine().getTemplate("loader.vm");
            Context context = toolManager.createContext();
            context.put("packageName", packageName);
            context.put("className", className);
            context.put("simpleClassName", simpleClassName);
            context.put("loaderClassName", loaderClassName);
            context.put("loaderSimpleClassName", loaderSimpleClassName);
            context.put("fields", structType.fields);
            template.merge(context, out);
        }
        loaderNames.add(loaderClassName);
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

    private Type makeType(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return new BasicType((PrimitiveType)type);
        }
        if (type.getKind() == TypeKind.DECLARED) {
            var declaredType = (DeclaredType) type;
            var declaredTypeElement = (TypeElement)declaredType.asElement();
            if (declaredTypeElement.getKind() == ElementKind.ENUM) {
                return new EnumType(declaredTypeElement);
            }
            if (declaredTypeElement.getQualifiedName().toString().equals("java.util.List")) {
                var componentType = declaredType.getTypeArguments().getFirst();
                return new ArrayType(componentType);
            } else {
                return new ReferenceType(declaredTypeElement);
            }
        }
        throw new RuntimeException("TODO " + type);
    }

    public interface Type {
        default boolean isReference() {
            return false;
        }
        default boolean isArray() {
            return false;
        }
        default boolean isEnum() {
            return false;
        }
    }

    public static class BasicType implements Type {

        private final PrimitiveType type;

        public BasicType(PrimitiveType type) {
            this.type = type;
        }

        public String primitiveType() {
            return StringUtils.capitalize(this.type.toString());
        }
    }

    public static class EnumType implements Type {

        private final TypeElement type;

        public EnumType(TypeElement type) {
            this.type = type;
        }

        @Override
        public boolean isEnum() {
            return true;
        }

        public String enumType() {
            return type.getQualifiedName().toString();
        }
    }

    public static class ReferenceType implements Type {

        private final TypeElement type;

        public ReferenceType(TypeElement type) {
            this.type = type;
        }

        @Override
        public boolean isReference() {
            return true;
        }

        public String structType() {
            return type.getQualifiedName().toString();
        }
    }

    public static class ArrayType implements Type {

        private final TypeMirror type;

        public ArrayType(TypeMirror type) {
            this.type = type;
        }

        @Override
        public boolean isArray() {
            return true;
        }

        public String componentType() {
            return ((TypeElement)((DeclaredType)type).asElement()).getQualifiedName().toString();
        }
    }

    public static class StructType implements Type {

        private final TypeElement element;
        private final List<StructField> fields = new ArrayList<>();

        public StructType(TypeElement element) {
            this.element = element;
        }

        public void addField(Name name, Type type) {
            this.fields.add(new StructField(name, type));
        }
    }

    public record StructField(Name name, Type type) {
    }
}
