// (C) 2025 uchicom
package com.uchicom.pj.dto.request.customer;

import com.uchicom.pj.annotation.Form;
import jakarta.validation.constraints.NotNull;

@Form("customerUpdate")
public class CustomerUpdateDto extends CustomerRegisterDto {
  @NotNull(message = "必須です。")
  public Long customerId;
}
