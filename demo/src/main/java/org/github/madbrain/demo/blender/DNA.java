package org.github.madbrain.demo.blender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Collected by <a href="https://github.com/blender/blender/blob/main/source/blender/makesdna/intern/makesdna.cc#L1594">makesdna</a>
 * tool to build a compact description of all structures
 * to be embedded in a .blend file.
 * Allows forward and backward compatibility
 */
@Target(ElementType.TYPE)
public @interface DNA {
}
