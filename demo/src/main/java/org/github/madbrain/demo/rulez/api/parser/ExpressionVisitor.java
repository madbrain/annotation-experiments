package org.github.madbrain.demo.rulez.api.parser;

public interface ExpressionVisitor {
    default void visitIdentifier(Identifier identifier) {
    }

    default void visitIntegerLiteral(IntegerLiteral integerLiteral) {
    }

    default void visitOperationCall(OperationCall operationCall) {
        operationCall.left().visit(this);
        operationCall.right().visit(this);
    }

    default void visitFieldAccess(FieldAccess fieldAccess) {
        fieldAccess.expr().visit(this);
    }

    default void visitBooleanLiteral(BooleanLiteral booleanLiteral) {
    }
}
