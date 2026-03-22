package com.github.losevskiyfz.calculationservice.base.annotation;

import com.github.losevskiyfz.calculationservice.base.initializer.RedisContainerInitializer;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = RedisContainerInitializer.class)
public @interface EnableRedis {
}
