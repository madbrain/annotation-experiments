package org.github.madbrain.demo.rulez.api.parser;

import java.util.ArrayList;

public record ObjectMatch(String varName, String typeName, ArrayList<Object> conditions) {
}
