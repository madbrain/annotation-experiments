package org.github.madbrain.demo.rulez.api.parser;

public interface Expression {
    void visit(ExpressionVisitor visitor);
}
