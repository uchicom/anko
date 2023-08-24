package com.uchicom.anko.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import com.uchicom.anko.enumeration.Position;

@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
  Position[] value() default {Position.OWNER, Position.ADMIN, Position.MEMBER};
}
