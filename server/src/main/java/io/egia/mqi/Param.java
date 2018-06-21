package io.egia.mqi;

public @interface Param {
    String name() default "";
    String type() default "";
}