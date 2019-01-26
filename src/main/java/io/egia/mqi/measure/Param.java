package io.egia.mqi.measure;

public @interface Param {
    String name() default "";

    String type() default "";
}
