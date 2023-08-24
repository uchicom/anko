package com.uchicom.anko.dto.response;

import java.util.List;

public class ErrorDto {
  public String errorMessage;
  public String form;
  public List<ViolationDto> violationList;

  public ErrorDto(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ErrorDto(String form, List<ViolationDto> violationList) {
    this.form = form;
    this.violationList = violationList;
  }
}
