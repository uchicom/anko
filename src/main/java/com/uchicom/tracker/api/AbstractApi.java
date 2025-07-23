// (C) 2023 uchicom
package com.uchicom.tracker.api;

import com.uchicom.tracker.dto.response.ErrorDto;
import com.uchicom.tracker.factory.di.DIFactory;
import com.uchicom.tracker.util.AbstractDb;
import com.uchicom.util.ThrowingSupplier;
import jakarta.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractApi extends AbstractDb {

  protected static final Logger logger = DIFactory.logger();

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

  public Object trans(HttpServletRequest req, ThrowingSupplier<Object, Throwable> function) {
    return handling(
        () -> transaction(String.valueOf((Long) req.getAttribute("accountId")), function));
  }

  public ErrorDto errorApi(Throwable e) {
    logger.log(Level.SEVERE, "APIエラー", e);
    return new ErrorDto("エラーが発生しました。管理者に連絡してください。");
  }

  public long getAccountId(HttpServletRequest req) {
    return (long) req.getAttribute("accountId");
  }
}
