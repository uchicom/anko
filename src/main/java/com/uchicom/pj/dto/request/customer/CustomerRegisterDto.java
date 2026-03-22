// (C) 2025 uchicom
package com.uchicom.pj.dto.request.customer;

import com.uchicom.pj.annotation.Form;

@Form("customerRegister")
public class CustomerRegisterDto {
  public String companyName;

  public String picName;

  public String emailAddress;

  public String telephonNumber;

  public String faxNumber;

  public String address;

  public String building;
}
