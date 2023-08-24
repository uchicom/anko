// (C) 2023 uchicom
package com.uchicom.memo.dto.response;

import jakarta.validation.ConstraintViolation;

public class ViolationDto {
  public int index;
  public String propertyPath;
  public String message;

  public static <T> ViolationDto create(ConstraintViolation<T> constraintViolation) {
    var dto = new ViolationDto();
    dto.propertyPath = constraintViolation.getPropertyPath().toString();
    dto.message = constraintViolation.getMessage();
    return dto;
  }

  public static <T> ViolationDto create(int index, ConstraintViolation<T> constraintViolation) {
    var dto = new ViolationDto();
    dto.index = index;
    dto.propertyPath = constraintViolation.getPropertyPath().toString();
    dto.message = constraintViolation.getMessage();
    return dto;
  }
}
