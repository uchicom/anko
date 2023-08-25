// (C) 2023 uchicom
package com.uchicom.memo.dto.response;

import java.util.List;

public class ListDto<T> {
  public List<T> list;

  public ListDto(List<T> list) {
    this.list = list;
  }
}
