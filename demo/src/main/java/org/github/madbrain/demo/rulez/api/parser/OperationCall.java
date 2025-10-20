package org.github.madbrain.demo.rulez.api.parser;

public record OperationCall(Expression left, Operation operation, Expression right) implements Expression {
    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visitOperationCall(this);
    }
}
