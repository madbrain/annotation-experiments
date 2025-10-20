package org.github.madbrain.demo.rulez.api;

import org.github.madbrain.demo.rulez.api.parser.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuleEvaluator {
    public RuleEvaluator(Class<?> ...rulesClasses) {
        for (Class<?> ruleClass : rulesClasses) {
            Set<Class<?>> imports = new HashSet<>();
            Optional.ofNullable(ruleClass.getAnnotation(Imports.class)).ifPresent(i -> {
                imports.addAll(Arrays.asList(i.value()));
            });
            for (Method m : ruleClass.getDeclaredMethods()) {
                Optional.ofNullable(m.getAnnotation(Rule.class)).ifPresent(ruleAnnot -> {
                    addRuleMethod(m, ruleAnnot.value(), new HashSet<>(imports));
                });
            }
        }
    }

    private void addRuleMethod(Method method, String spec, Set<Class<?>> imports) {
        System.out.println("RULE " + method.getName());
        Optional.ofNullable(method.getAnnotation(Imports.class)).ifPresent(i -> {
            imports.addAll(Arrays.asList(i.value()));
        });
        Map<String, Class<?>> argTypes = new HashMap<>();
        for (var p : method.getParameters()) {
            if (!RuleContext.class.isAssignableFrom(p.getType())) {
                argTypes.put(p.getName(), p.getType());
                imports.add(p.getType());
            }
        }
        var importMap = imports.stream().collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
        var parser = new RuleParser(spec);
        var ast = parser.parse();
        ast.matches().forEach(match -> {
            Optional.ofNullable(match.varName()).ifPresent(varName -> {
                var argType = argTypes.get(varName);
                Class<?> varType = null;
                if (argType != null) {
                    if (!argType.getSimpleName().equals(match.typeName())) {
                        throw new RuntimeException("Incompatible type for variable '" + varName + "'");
                    }
                    varType = argType;
                }
                if (varType == null) {
                    throw new RuntimeException("Unknown type '" + match.typeName() + "'");
                }
            });
            var matchType = Optional.ofNullable(importMap.get(match.typeName())).orElseThrow();
            match.conditions().forEach(cond -> {
                // TODO make a visitor which also verify types
                cond.visit(new ExpressionVisitor() {
                    @Override
                    public void visitIdentifier(Identifier identifier) {
                        var typeRef = importMap.get(identifier.name());
                        if (typeRef == null) {
                            try {
                                var field = matchType.getDeclaredField(identifier.name());
                            } catch (NoSuchFieldException e) {
                                throw new RuntimeException("Unknown field '" + identifier.name() + "'");
                            }
                        }
                    }
                });
            });
        });
        // TODO build network
        System.out.println(ast);
    }

    public void fireRules(Object ...objects) {
        throw new RuntimeException();
    }
}
