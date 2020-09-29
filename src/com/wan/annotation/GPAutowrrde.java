package com.wan.annotation;

import java.lang.annotation.*;

/**
 * @Author: WanWenSheng
 * @Description:
 * @Dete: Created in 10:18 2020/9/29
 * @Modified By:
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GPAutowrrde {

    String value() default "";
}
