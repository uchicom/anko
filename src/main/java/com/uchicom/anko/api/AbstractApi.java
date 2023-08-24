package com.uchicom.anko.api;


import com.uchicom.util.ThrowingSupplier;
import dagger.Component;
import java.util.logging.Logger;
import com.uchicom.anko.dto.response.ErrorDto;
import com.uchicom.anko.module.MainModule;
import com.uchicom.anko.util.AbstractDb;
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
    return new ErrorDto("エラーが発生しました。管理者に連絡してください。");
  }
}
