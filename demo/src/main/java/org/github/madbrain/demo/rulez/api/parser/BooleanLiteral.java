package org.github.madbrain.demo.rulez.api.parser;

public record BooleanLiteral(boolean value) implements Expression {
    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visitBooleanLiteral(this);
    }
}
