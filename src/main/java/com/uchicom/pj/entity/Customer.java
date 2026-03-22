// (C) 2025 uchicom
package com.uchicom.pj.entity;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;

/** 顧客. */
@IQSchema("pj")
@IQTable(name = "customer", inheritColumns = true)
public class Customer extends AbstractTable {

  /** 会社名. */
  @IQColumn(length = 64)
  public String company_name;

  /** 担当者名. */
  @IQColumn(length = 64)
  public String pic_name;

  /** メールアドレス. */
  @IQColumn(length = 256)
  public String email_address;

  /** 電話番号. */
  @IQColumn(length = 11)
  public String telephon_number;

  /** FAX番号. */
  @IQColumn(length = 11)
  public String fax_number;

  /** 住所. */
  @IQColumn(length = 128)
  public String address;

  /** 建物. */
  @IQColumn(length = 64)
  public String building;

  public Customer() {}
}
