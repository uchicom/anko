// (C) 2023 uchicom
package com.uchicom.memo;

import com.iciql.Db;
import com.uchicom.memo.enumeration.Config;
import com.uchicom.memo.factory.di.DIFactory;
import com.uchicom.util.ResourceUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * コンテキスト.
 *
 * @author uchicom Shigeki Uchiyama
 */
public class Context {

  public static final ThreadLocal<Db> db = new ThreadLocal<Db>();
  public static final ThreadLocal<String> executor = new ThreadLocal<String>();

  private static final Logger logger = DIFactory.logger();

  private static final Context context = new Context();

  private final Properties dbProperties;
  private final Properties configProperties;

  private HikariDataSource ds;

  private Context() {
    // Hikari設定
    logger.info(System.getProperty("com.uchicom.memo.db", "src/main/resources/db.properties"));
    dbProperties =
        ResourceUtil.createProperties(
            new File(System.getProperty("com.uchicom.memo.db", "src/main/resources/db.properties")),
            "UTF-8");
    // 設定
    logger.info(
        System.getProperty("com.uchicom.memo.config", "src/main/resources/config.properties"));
    configProperties =
        ResourceUtil.createProperties(
            new File(
                System.getProperty(
                    "com.uchicom.memo.config", "src/main/resources/config.properties")),
            "UTF-8");
  }

  private synchronized HikariDataSource getDs(boolean create) {
    if (create && (ds == null || ds.isClosed())) {
      HikariConfig config = new HikariConfig(dbProperties);
      ds = new HikariDataSource(config);
    }
    return ds;
  }

  /**
   * データベースをオープンして取得します。
   *
   * @return DBオブジェクト
   */
  public static Db openDb() {
    return Db.open(context.getDs(true));
  }

  public static Db openDbTransaction() throws SQLException {
    Db db = openDb();
    db.setSkipCreate(true);
    db.setAutoSavePoint(false);
    db.getConnection().setAutoCommit(false);
    return db;
  }

  /** 設定 */
  public static String get(Config config) {
    return context.configProperties.getProperty(config.name());
  }

  /** 設定 */
  public static Integer getInteger(Config config) {
    return Integer.parseInt(context.configProperties.getProperty(config.name()));
  }

  /** 設定 */
  public static Boolean getBoolean(Config config) {
    return Boolean.parseBoolean(context.configProperties.getProperty(config.name()));
  }

  public static void close() {
    var ds = context.getDs(false);
    if (ds != null) {
      ds.close();
    }
  }
}
