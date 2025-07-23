// (C) 2023 uchicom
package com.uchicom.tracker.exception;

import com.uchicom.tracker.annotation.Form;
import com.uchicom.tracker.dto.response.ErrorDto;
import com.uchicom.tracker.dto.response.ViolationDto;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViolationException extends Exception {
  final List<ViolationDto> violationList;
  final String form;

  ViolationException(String form, List<ViolationDto> violationList) {
    this.form = form;
    this.violationList = violationList;
  }

  public static <T> ViolationException create(Set<ConstraintViolation<T>> violationSet) {
    return new ViolationException(
        violationSet.stream()
            .findFirst()
            .map(violation -> violation.getRootBean().getClass().getAnnotation(Form.class))
            .map(Form::value)
            .orElse(null),
        violationSet.stream().map(ViolationDto::create).toList());
  }

  public static <T> ViolationException create(
      Map<Integer, Set<ConstraintViolation<T>>> violationMap) {

    return new ViolationException(
        violationMap.entrySet().stream()
            .findFirst()
            .map(
                entry ->
                    entry
                        .getValue()
                        .iterator()
                        .next()
                        .getRootBean()
                        .getClass()
                        .getAnnotation(Form.class))
            .map(Form::value)
            .orElse(null),
        violationMap.entrySet().stream()
            .flatMap(
                entry ->
                    entry.getValue().stream()
                        .map(violation -> ViolationDto.create(entry.getKey(), violation))
                        .toList()
                        .stream())
            .toList());
  }

  public ErrorDto getErrorDto() {
    return new ErrorDto(form, violationList);
  }
}
