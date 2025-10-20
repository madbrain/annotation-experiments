package org.github.madbrain.demo.rulez.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Imports {
    Class<?>[] value();
}
