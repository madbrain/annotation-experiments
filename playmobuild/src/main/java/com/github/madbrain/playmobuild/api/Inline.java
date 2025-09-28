package com.github.madbrain.playmobuild.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.RECORD_COMPONENT)
public @interface Inline {
    String value() default "";
}
