// (C) 2023 uchicom
package com.uchicom.memo.dto.response.account;

import com.uchicom.memo.enumeration.ApiResult;

public class ResultDto {
  public ApiResult result;

  public ResultDto(ApiResult result) {
    this.result = result;
  }
}
