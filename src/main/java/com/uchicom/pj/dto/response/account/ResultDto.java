// (C) 2023 uchicom
package com.uchicom.pj.dto.response.account;

import com.uchicom.pj.enumeration.ApiResult;

public class ResultDto {
  public ApiResult result;

  public ResultDto(ApiResult result) {
    this.result = result;
  }
}
