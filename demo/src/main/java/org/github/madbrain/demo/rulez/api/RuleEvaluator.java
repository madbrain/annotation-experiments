package org.github.madbrain.demo.rulez.api;

import org.github.madbrain.demo.rulez.api.parser.RuleParser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RuleEvaluator {
    public RuleEvaluator(Class<?> ...rulesClasses) {
        for (Class<?> ruleClass : rulesClasses) {
            for (Method m : ruleClass.getDeclaredMethods()) {
                Optional.ofNullable(m.getAnnotation(Rule.class)).ifPresent(ruleAnnot -> {
                    addRuleMethod(m, ruleAnnot.value());
                });
            }
        }
    }

    private void addRuleMethod(Method method, String spec) {
        System.out.println("RULE " + method.getName());
        Map<String, Class<?>> argTypes = new HashMap<>();
        for (var p : method.getParameters()) {
            if (!RuleContext.class.isAssignableFrom(p.getType())) {
                argTypes.put(p.getName(), p.getType());
            }
        }
        var parser = new RuleParser(spec);
        var ast = parser.parse();
        ast.matches().forEach(match -> {
            Optional.ofNullable(match.varName()).ifPresent(varName -> {
                var argType = argTypes.get(varName);
                Class<?> varType = null;
                if (argType != null) {
                    if (!argType.getSimpleName().equals(match.typeName())) {
                        throw new RuntimeException("incompatible type for variable '" + varName + "'");
                    }
                    varType = argType;
                }
                if (varType == null) {
                    throw new RuntimeException("Unknown type '" + match.typeName() + "'");
                }
            });
        });
        // TODO check that attributes are known
        // TODO build network
        System.out.println(ast);
    }

    public void fireRules(Object ...objects) {
        throw new RuntimeException();
    }
}
