// (C) 2025 uchicom
package com.uchicom.tracker.factory.di;

import com.uchicom.tracker.api.AccountApi;
import com.uchicom.tracker.api.IssueApi;

public class ApiFactory {

  static AccountApi accountApi() {
    return new AccountApi(ServiceFactory.accountService(), ServiceFactory.cookieService());
  }

  static IssueApi issueApi() {
    return new IssueApi(ServiceFactory.issueService());
  }
}
