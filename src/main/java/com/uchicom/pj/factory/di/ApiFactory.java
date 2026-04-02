// (C) 2025 uchicom
package com.uchicom.pj.factory.di;

import com.uchicom.pj.api.AccountApi;
import com.uchicom.pj.api.CustomerApi;
import com.uchicom.pj.api.ProjectApi;
import com.uchicom.pj.api.TaskApi;

public class ApiFactory {

  static AccountApi accountApi() {
    return new AccountApi(ServiceFactory.accountService());
  }

  static CustomerApi customerApi() {
    return new CustomerApi(ServiceFactory.customerService());
  }

  static ProjectApi projectApi() {
    return new ProjectApi(ServiceFactory.projectService());
  }

  static TaskApi taskApi() {
    return new TaskApi(ServiceFactory.taskService());
  }
}
