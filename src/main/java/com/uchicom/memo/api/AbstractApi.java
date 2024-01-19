// (C) 2023 uchicom
package com.uchicom.memo.api;

import com.uchicom.memo.dto.response.ErrorDto;
import com.uchicom.memo.module.MainModule;
import com.uchicom.memo.util.AbstractDb;
import com.uchicom.util.ThrowingSupplier;
import dagger.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.Request;

public abstract class AbstractApi extends AbstractDb {

  protected static final Logger logger = DaggerAbstractApi_MainComponent.create().logger();

  @Component(modules = MainModule.class)
  interface MainComponent {
    Logger logger();
  }

  public <T> Object handling(ThrowingSupplier<T, Throwable> function) {
    try {
      return function.get();
    } catch (Throwable e) {
      return errorApi(e.getCause());
    }
  }

  public Object refer(ThrowingSupplier<Object, Throwable> function) {
    return handling(() -> reference(function));
  }

  public Object trans(Request req, ThrowingSupplier<Object, Throwable> function) {
    return handling(() -> transaction(String.valueOf((Long) req.attribute("accountId")), function));
  }

  public ErrorDto errorApi(Throwable e) {
    logger.log(Level.SEVERE, "APIエラー", e);
    return new ErrorDto("エラーが発生しました。管理者に連絡してください。");
  }

  public long getAccountId(Request req) {
    return (long) req.attribute("accountId");
  }
}
