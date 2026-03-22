// (C) 2025 uchicom
package com.uchicom.pj.dao;

import com.uchicom.pj.dao.helper.DbHelper;
import com.uchicom.pj.entity.Customer;

public class CustomerDao extends AbstractDao<Customer> {

  /** コンストラクタ. */
  public CustomerDao(DbHelper<Customer> helper) {
    super(helper);
  }
}
