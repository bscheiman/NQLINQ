package org.nqlinq.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JdbcConnection {
    String url() default "";

    String driver() default "oracle.jdbc.OracleDriver";

    String user() default "";

    String password() default "";
}
