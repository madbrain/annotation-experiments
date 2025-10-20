package org.github.madbrain.demo.rulez.api.parser;

import java.util.List;

public record ObjectMatch(String varName, String typeName, List<Expression> conditions) {
}
