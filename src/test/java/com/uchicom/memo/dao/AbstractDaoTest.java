// (C) 2023 uchicom
package com.uchicom.memo.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.iciql.Db;
import com.iciql.Query;
import com.iciql.QueryWhere;
import com.iciql.util.Utils;
import com.uchicom.memo.AbstractTest;
import com.uchicom.memo.Context;
import com.uchicom.memo.dao.helper.DbHelper;
import com.uchicom.memo.entity.AbstractTable;
import com.uchicom.util.ThrowingFunction;
import com.uchicom.util.ThrowingRunnable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

@SuppressWarnings("unchecked")
public abstract class AbstractDaoTest<U extends AbstractTable, V> extends AbstractTest {

  @Mock Connection conn;
  @Mock PreparedStatement preState;
  @Mock Statement statement;
  @Mock ResultSet resultSet;

  @Mock DatabaseMetaData databaseMetaData;

  V dao;
  Query<U> query;
  DbHelper<U> helper;

  @Mock QueryWhere<U> queryWhere;

  @Captor ArgumentCaptor<Query<U>> queryCaptor;

  @Captor ArgumentCaptor<QueryWhere<U>> queryWhereCaptor;

  @Captor ArgumentCaptor<String> sqlCaptor;

  @Captor ArgumentCaptor<U> entityCaptor;

  AbstractDaoTest(Supplier<U> u, Function<DbHelper<U>, V> function) {
    helper = Mockito.spy(new DbHelper<U>(u.get()));
    dao = function.apply(helper);
  }

  /** テスト事前準備. */
  @BeforeEach
  public void setUp() throws Exception {
    Utils.AS_COUNTER.set(0);

    Mockito.doReturn(databaseMetaData).when(conn).getMetaData();
    Mockito.doReturn("H2").when(databaseMetaData).getDatabaseProductName();
    doReturn(true).when(helper).insert(entityCaptor.capture());
    doReturn(true).when(helper).update(entityCaptor.capture());
  }

  protected String getSQL() {
    String sql = query.toSQL();
    if (sql != null) {
      return sql.replace(" (false) OR", "")
          .replaceAll("\\(", " ( ")
          .replaceAll("\\)", " )")
          .replaceAll(" +", " ")
          .replaceAll(" +,", ",");
    }
    return null;
  }

  public U test(ThrowingFunction<Db, U, Throwable> function) {
    try (Db db = Mockito.spy(Db.open(conn))) {
      Mockito.doReturn(true).when(db).getSkipCreate();
      Mockito.doReturn(null)
          .when(db)
          .executeQuery(Mockito.<Class<U>>any(), sqlCaptor.capture(), Mockito.<Object>any());
      Mockito.doReturn(statement).when(conn).createStatement();
      Mockito.doReturn(resultSet).when(statement).executeQuery(sqlCaptor.capture());
      Mockito.doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture(), Mockito.anyInt());
      Mockito.doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture());
      Mockito.doReturn(resultSet).when(preState).executeQuery();
      Context.db.set(db);
      Mockito.doAnswer(
              invocation -> {
                query = db.from((U) invocation.getArguments()[0]);
                return query;
              })
          .when(helper)
          .from(any());
      return function.apply(db);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
    }
  }

  public void test(ThrowingRunnable<Throwable> runnable) {
    try (Db db = Mockito.spy(Db.open(conn))) {
      Mockito.doReturn(true).when(db).getSkipCreate();
      Mockito.doReturn(null)
          .when(db)
          .executeQuery(Mockito.<Class<U>>any(), sqlCaptor.capture(), Mockito.<Object>any());
      Mockito.doReturn(statement).when(conn).createStatement();
      Mockito.doReturn(resultSet).when(statement).executeQuery(sqlCaptor.capture());
      Mockito.doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture(), Mockito.anyInt());
      Mockito.doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture());
      Mockito.doReturn(resultSet).when(preState).executeQuery();
      Context.db.set(db);
      Mockito.doAnswer(
              invocation -> {
                query = db.from((U) invocation.getArguments()[0]);
                return query;
              })
          .when(helper)
          .from(any());
      runnable.run();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
    }
  }
}
