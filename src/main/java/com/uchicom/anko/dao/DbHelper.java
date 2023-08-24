package com.uchicom.anko.dao;

import com.iciql.Db;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import com.iciql.Query;
import com.iciql.util.Utils;
import com.uchicom.anko.Constants;
import com.uchicom.anko.Context;
import com.uchicom.anko.entity.AbstractTable;

import java.time.LocalDateTime;
import java.util.List;
import javax.inject.Inject;

public class DbHelper<T extends AbstractTable> {

  /** エンティティ */
  protected final ThreadLocal<T> meta;

  Class<T> clazz;

  /** コンストラクタ. */
  @Inject
  @SuppressWarnings("unchecked")
  public DbHelper(T t) {
    this.clazz = (Class<T>) t.getClass();
    this.meta = Utils.newThreadLocal(clazz);
  }

  String getSchema() {
    var annotation = clazz.getAnnotation(IQSchema.class);
    if (annotation == null) {
      return null;
    }
    return annotation.value();
  }

  String getTableName() {
    return clazz.getAnnotation(IQTable.class).name();
  }

  public String getReferenceTableName() {
    var schema = getSchema();
    var tableName = getTableName();
    if (schema == null || schema.length() == 0) {
      return tableName;
    }
    return schema + "." + tableName;
  }

  protected T meta() {
    return meta.get();
  }

  /**
   * Dbを取得します.
   *
   * @return DB
   */
  public static Db getDb() {
    return Context.db.get();
  }

  /**
   * 実行者を取得します.
   *
   * @return 実行者
   */
  public static String getExecutor() {
    return Context.executor.get();
  }

  /**
   * IDを条件にエンティティを取得します.
   *
   * @param id ID
   * @return エンティティ
   */
  public T findById(long id) {
    return from().where(meta().id).is(id).selectFirst();
  }

  /**
   * エンティティリストを全件取得します.
   *
   * @return エンティティリスト
   */
  public List<T> findAll() {
    return from().orderBy(meta().id).select();
  }

  /**
   * エンティティリストを全件取得します.<br>
   * IDの降順で取得します.
   *
   * @return エンティティリスト
   */
  public List<T> findAllDesc() {
    return from().orderByDesc(meta().id).select();
  }

  /**
   * このエンティティのクエリを取得します.
   *
   * @return クエリ
   */
  public Query<T> from() {
    return from(meta());
  }

  /**
   * 別のエンティティのクエリを取得します.
   *
   * @param <D> 別のエンティティ
   * @param entity 別のエンティティ
   * @return クエリ
   */
  public <D> Query<D> from(D entity) {
    return getDb().from(entity);
  }

  /**
   * エンティティを登録します.
   *
   * @param entity エンティティ
   * @return 登録に成功した場合はtrue,それ以外はfalseを返します
   */
  public boolean insert(T entity) {
    // システムカラムを設定します
    setInsertSystemColumn(entity);
    return getDb().insert(entity);
  }

  /**
   * エンティティを登録してIDを取得します.
   *
   * @param entity エンティティ
   * @return ID
   */
  public long insertAndGetKey(T entity) {
    // システムカラムを設定します
    setInsertSystemColumn(entity);
    return getDb().insertAndGetKey(entity);
  }

  /**
   * エンティティリストを登録してIDリストを取得します.
   *
   * @param entityList エンティティリスト
   * @return IDリスト
   */
  public List<Long> insertAllAndGetKeys(List<T> entityList) {
    // システムカラムを設定します
    entityList.forEach(this::setInsertSystemColumn);
    return getDb().insertAllAndGetKeys(entityList);
  }

  /** insertのシステムカラムを設定します */
  private void setInsertSystemColumn(T entity) {
    entity.insert_datetime = LocalDateTime.now(Constants.ZONE_ID);
    entity.inserted = getExecutor();
  }

  /**
   * エンティティを変更チェックしてから更新します.
   *
   * @param entity エンティティ
   * @return 更新に成功した場合はtrue,それ以外はfalseを返します
   */
  public boolean checkUpdate(T entity) {
    T before = findById(entity.id);
    if (before.equals(entity)) {
      return false;
    }
    return update(entity);
  }

  /**
   * エンティティを更新します.
   *
   * @param entity エンティティ
   * @return 更新に成功した場合はtrue,それ以外はfalseを返します
   */
  public boolean update(T entity) {
    // システムカラムを設定します
    setUpdateSystemColumn(entity);
    return getDb().update(entity);
  }

  /**
   * エンティティを更新します.
   *
   * @param entityList エンティティ
   */
  public void updateAll(List<T> entityList) {
    // システムカラムを設定します
    entityList.forEach(this::setUpdateSystemColumn);
    getDb().updateAll(entityList);
  }

  /** updateのシステムカラムを設定します */
  void setUpdateSystemColumn(T entity) {
    entity.update_datetime = LocalDateTime.now(Constants.ZONE_ID);
    entity.updated = getExecutor();
    if (entity.update_seq == null) {
      entity.update_seq = 1;
    } else {
      entity.update_seq++;
    }
  }

  /**
   * エンティティを削除します.
   *
   * @param entity エンティティ
   */
  public void delete(T entity) {
    getDb().delete(entity);
  }

  /**
   * エンティティリストを全て削除します.
   *
   * @param entityList エンティティリスト
   */
  public void deleteAll(List<T> entityList) {
    getDb().deleteAll(entityList);
  }
}

