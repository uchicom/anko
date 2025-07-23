// (C) 2023 uchicom
package com.uchicom.tracker.dto.response.account;

import com.uchicom.tracker.enumeration.ApiResult;

public class ResultDto {
  public ApiResult result;

  public ResultDto(ApiResult result) {
    this.result = result;
  }
}
