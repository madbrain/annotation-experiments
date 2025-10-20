package org.github.madbrain.demo.rulez.api.parser;

public record OperationCall(Expression left, Operation operation, Expression right) {
}
