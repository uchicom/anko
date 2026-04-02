// (C) 2025 uchicom
package com.uchicom.pj.factory.di;

import com.uchicom.pj.dao.AccountDao;
import com.uchicom.pj.dao.CustomerDao;
import com.uchicom.pj.dao.ProjectDao;
import com.uchicom.pj.dao.RefreshTokenDao;
import com.uchicom.pj.dao.TaskDao;
import com.uchicom.pj.dao.helper.DbHelper;
import com.uchicom.pj.entity.AbstractTable;
import com.uchicom.pj.entity.Account;
import com.uchicom.pj.entity.Customer;
import com.uchicom.pj.entity.Project;
import com.uchicom.pj.entity.RefreshToken;
import com.uchicom.pj.entity.Task;

public class DaoFactory {

  static <T extends AbstractTable> DbHelper<T> helper(T entity) {
    return new DbHelper<>(entity);
  }

  static AccountDao accountDao() {
    return new AccountDao(helper(new Account()));
  }

  static ProjectDao projectDao() {
    return new ProjectDao(helper(new Project()));
  }

  static CustomerDao customerDao() {
    return new CustomerDao(helper(new Customer()));
  }

  static RefreshTokenDao refreshTokenDao() {
    return new RefreshTokenDao(helper(new RefreshToken()));
  }

  static TaskDao taskDao() {
    return new TaskDao(helper(new Task()));
  }
}
