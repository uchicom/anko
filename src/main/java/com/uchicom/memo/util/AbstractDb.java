// (C) 2022 uchicom
package com.uchicom.memo.util;

import com.iciql.Db;
import com.uchicom.memo.Context;
import com.uchicom.util.ThrowingConsumer;
import com.uchicom.util.ThrowingFunction;
import com.uchicom.util.ThrowingRunnable;
import com.uchicom.util.ThrowingSupplier;
import java.sql.Connection;

public abstract class AbstractDb {

  public <T> T reference(ThrowingFunction<Db, T, Throwable> function) {
    try (Db db = Context.openDb()) {
      Context.db.set(db);
      return function.apply(db);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
    }
  }

  public <T> T reference(ThrowingSupplier<T, Throwable> function) {
    try (Db db = Context.openDb()) {
      Context.db.set(db);
      return function.get();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
    }
  }

  public <T> T transaction(String executor, ThrowingFunction<Db, T, Throwable> function) {
    try (Db db = Context.openDbTransaction();
        Connection con = db.getConnection(); ) {
      Context.db.set(db);
      Context.executor.set(executor);
      var result = function.apply(db);
      con.commit();
      return result;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
      Context.executor.remove();
    }
  }

  public <T> T transaction(String executor, ThrowingSupplier<T, Throwable> function) {
    try (Db db = Context.openDbTransaction();
        Connection con = db.getConnection(); ) {
      Context.db.set(db);
      Context.executor.set(executor);
      var result = function.get();
      con.commit();
      return result;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
      com.uchicom.memo.Context.executor.remove();
    }
  }

  public void transactionConsumer(String executor, ThrowingConsumer<Db, Throwable> consumer) {
    try (Db db = Context.openDbTransaction();
        Connection con = db.getConnection(); ) {
      Context.db.set(db);
      Context.executor.set(executor);
      consumer.accept(db);
      con.commit();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
      Context.executor.remove();
    }
  }

  public void transactionRunnable(String executor, ThrowingRunnable<Throwable> runnable) {
    try (Db db = Context.openDbTransaction();
        Connection con = db.getConnection(); ) {
      Context.db.set(db);
      Context.executor.set(executor);
      runnable.run();
      con.commit();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
      Context.executor.remove();
    }
  }

  public static void shutdown() {
    Context.close();
  }
}
