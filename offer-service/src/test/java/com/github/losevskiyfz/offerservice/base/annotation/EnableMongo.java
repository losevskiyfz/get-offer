package com.github.losevskiyfz.offerservice.base.annotation;

import com.github.losevskiyfz.offerservice.base.initializer.MongoContainerInitializer;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = MongoContainerInitializer.class)
public @interface EnableMongo {
}