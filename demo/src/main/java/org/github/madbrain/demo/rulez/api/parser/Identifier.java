package org.github.madbrain.demo.rulez.api.parser;

public record Identifier(String name) implements Expression {

    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visitIdentifier(this);
    }
}
