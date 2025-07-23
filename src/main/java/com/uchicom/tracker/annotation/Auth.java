// (C) 2023 uchicom
package com.uchicom.tracker.annotation;

import com.uchicom.tracker.enumeration.Position;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
  Position[] value() default {Position.TESTER, Position.USER};
}
