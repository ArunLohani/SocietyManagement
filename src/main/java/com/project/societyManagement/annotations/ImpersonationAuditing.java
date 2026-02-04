package com.project.societyManagement.annotations;

import com.project.societyManagement.entity.types.ImpersonationAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImpersonationAuditing {
    ImpersonationAction action();
    String reason() default "";
}
