package org.nqlinq.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JndiConnection {
    String url() default "";

    String source() default "";
}
