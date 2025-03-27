// (C) 2025 uchicom
package com.uchicom.memo.factory.di;

import com.uchicom.memo.api.AccountApi;
import com.uchicom.memo.api.MemoApi;

public class ApiFactory {

  static AccountApi accountApi() {
    return new AccountApi(ServiceFactory.accountService(), ServiceFactory.cookieService());
  }

  static MemoApi memoApi() {
    return new MemoApi(ServiceFactory.memoService());
  }
}
