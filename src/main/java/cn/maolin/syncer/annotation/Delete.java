// ==========================
// Copyright (c) 2023-04-23 Sioux
// All rights reserved.
// ==========================
package cn.maolin.syncer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Delete {

}
