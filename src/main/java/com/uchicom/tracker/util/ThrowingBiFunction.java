// (C) 2024 uchicom
package com.uchicom.tracker.util;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, S extends Throwable> {
  R apply(T var1, U var2) throws S;
}
