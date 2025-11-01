// (C) 2023 uchicom
package com.uchicom.tracker.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.iciql.Db;
import com.iciql.Query;
import com.iciql.QueryWhere;
import com.iciql.util.Utils;
import com.uchicom.tracker.AbstractTest;
import com.uchicom.tracker.Context;
import com.uchicom.tracker.dao.helper.DbHelper;
import com.uchicom.tracker.entity.AbstractTable;
import com.uchicom.util.ThrowingFunction;
import com.uchicom.util.ThrowingRunnable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
  List<Query<U>> queryList = new ArrayList<>();
  DbHelper<U> helper;

  @Captor ArgumentCaptor<Query<U>> queryCaptor;
  @Captor ArgumentCaptor<QueryWhere<U>> queryWhereCaptor;
  @Captor ArgumentCaptor<String> sqlCaptor;
  @Captor ArgumentCaptor<U> entityCaptor;
  @Captor ArgumentCaptor<Class<?>> classCaptor;
  @Captor ArgumentCaptor<Object[]> paramsCaptor;
  @Captor ArgumentCaptor<U> fromCaptor;

  AbstractDaoTest(Supplier<U> u, Function<DbHelper<U>, V> function) {
    helper = spy(new DbHelper<U>(u.get()));
    dao = function.apply(helper);
  }

  /** テスト事前準備. */
  @BeforeEach
  public void setUp() throws Exception {
    Utils.AS_COUNTER.set(0);

    doReturn(databaseMetaData).when(conn).getMetaData();
    doReturn("H2").when(databaseMetaData).getDatabaseProductName();
    doReturn(true).when(helper).insert(entityCaptor.capture());
    doReturn(true).when(helper).update(entityCaptor.capture());
  }

  protected String getSQL() {
    return getSQL(queryList.size() - 1);
  }

  protected String getSQL(int i) {
    if (!queryList.isEmpty()) {
      var query = queryList.get(i);
      String sql = query.toSQL();
      if (sql != null) {
        return sql.replaceAll(" +", " ")
            .replaceAll("\\( +", "(")
            .replaceAll(" +\\)", ")")
            .replaceAll(" ,", ",")
            .replace(" (false) OR", "")
            .trim();
      }
    }
    if (!sqlCaptor.getAllValues().isEmpty()) {
      return sqlCaptor
          .getValue()
          .replaceAll(" +", " ")
          .replaceAll("\\( +", "(")
          .replaceAll(" +\\)", ")")
          .replaceAll(" ,", ",")
          .replace(" (false) OR", "")
          .trim();
    }
    return null;
  }

  public U test(ThrowingFunction<Db, U, Throwable> function) {
    try (Db db = spy(Db.open(conn))) {
      doReturn(true).when(db).getSkipCreate();
      doReturn(null)
          .when(db)
          .executeQuery(Mockito.<Class<U>>any(), sqlCaptor.capture(), paramsCaptor.capture());
      doReturn(statement).when(conn).createStatement();
      doReturn(resultSet).when(statement).executeQuery(sqlCaptor.capture());
      doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture(), anyInt());
      doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture());
      doReturn(resultSet).when(preState).executeQuery();
      doAnswer(
              invocation -> {
                var query = db.from((U) invocation.getArguments()[0]);
                queryList.add(query);
                return query;
              })
          .when(helper)
          .from(any());
      doAnswer(
              invocation -> {
                var query = db.from((U) invocation.getArguments()[0]);
                queryList.add(query);
                return query;
              })
          .when(helper)
          .updateFrom(any());
      return function.apply(db);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
    }
  }

  public void test(ThrowingRunnable<Throwable> runnable) {
    try (Db db = spy(Db.open(conn))) {
      doReturn(true).when(db).getSkipCreate();
      doReturn(null)
          .when(db)
          .executeQuery(Mockito.<Class<U>>any(), sqlCaptor.capture(), paramsCaptor.capture());
      doReturn(statement).when(conn).createStatement();
      doReturn(resultSet).when(statement).executeQuery(sqlCaptor.capture());
      doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture(), anyInt());
      doReturn(preState).when(conn).prepareStatement(sqlCaptor.capture());
      doReturn(resultSet).when(preState).executeQuery();
      Context.db.set(db);
      doAnswer(
              invocation -> {
                var query = db.from((U) invocation.getArguments()[0]);
                queryList.add(query);
                return query;
              })
          .when(helper)
          .from(any());
      doAnswer(
              invocation -> {
                var query = db.from((U) invocation.getArguments()[0]);
                queryList.add(query);
                return query;
              })
          .when(helper)
          .updateFrom(any());
      runnable.run();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      Context.db.remove();
    }
  }
}
