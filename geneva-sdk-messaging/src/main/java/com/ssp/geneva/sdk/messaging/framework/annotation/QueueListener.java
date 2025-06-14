package com.ssp.geneva.sdk.messaging.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueueListener {
  String[] value() default {};

  SqsMessageDeletionPolicy policy() default SqsMessageDeletionPolicy.ALWAYS;

  String[] filter() default {};
}
