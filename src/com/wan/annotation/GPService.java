package com.wan.annotation;

import java.lang.annotation.*;

/**
 * @Author: WanWenSheng
 * @Description:
 * @Dete: Created in 10:18 2020/9/29
 * @Modified By:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GPService {

    String value() default "";
}
