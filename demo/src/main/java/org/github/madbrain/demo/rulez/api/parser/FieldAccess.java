package org.github.madbrain.demo.rulez.api.parser;

public record FieldAccess(Expression expr, String field) implements Expression {
    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visitFieldAccess(this);
    }
}
