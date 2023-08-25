// (C) 2023 uchicom
package com.uchicom.memo.annotation;

import com.uchicom.memo.enumeration.Position;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
  Position[] value() default {Position.TESTER, Position.USER};
}
