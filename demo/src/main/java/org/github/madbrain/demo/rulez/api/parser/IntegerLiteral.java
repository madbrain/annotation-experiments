package org.github.madbrain.demo.rulez.api.parser;

public record IntegerLiteral(int value) implements Expression {
    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visitIntegerLiteral(this);
    }
}
