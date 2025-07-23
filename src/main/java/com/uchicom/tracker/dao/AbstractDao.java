// (C) 2023 uchicom
package com.uchicom.tracker.dao;

import com.uchicom.tracker.dao.helper.DbHelper;
import com.uchicom.tracker.entity.AbstractTable;
import java.util.List;

/**
 * リポジトリの基底クラス.
 *
 * @author uchicom
 * @param <T> エンティティ
 */
public abstract class AbstractDao<T extends AbstractTable> {

  DbHelper<T> helper;

  /** コンストラクタ. */
  AbstractDao(DbHelper<T> helper) {
    this.helper = helper;
  }

  /**
   * IDを条件にエンティティを取得します.
   *
   * @param id ID
   * @return エンティティ
   */
  public T findById(long id) {
    return helper.findById(id);
  }

  /**
   * エンティティリストを全件取得します.
   *
   * @return エンティティリスト
   */
  public List<T> findAll() {
    return helper.findAll();
  }

  /**
   * エンティティリストを全件取得します.<br>
   * IDの降順で取得します.
   *
   * @return エンティティリスト
   */
  public List<T> findAllDesc() {
    return helper.findAllDesc();
  }

  /**
   * エンティティを登録します.
   *
   * @param entity エンティティ
   * @return 登録に成功した場合はtrue,それ以外はfalseを返します
   */
  public boolean insert(T entity) {
    return helper.insert(entity);
  }

  /**
   * エンティティを登録してIDを取得します.
   *
   * @param entity エンティティ
   * @return ID
   */
  public long insertAndGetKey(T entity) {
    return helper.insertAndGetKey(entity);
  }

  /**
   * エンティティリストを登録してIDリストを取得します.
   *
   * @param entityList エンティティリスト
   * @return IDリスト
   */
  public List<Long> insertAllAndGetKeys(List<T> entityList) {
    return helper.insertAllAndGetKeys(entityList);
  }

  /**
   * エンティティを更新します.
   *
   * @param entity エンティティ
   * @return 更新に成功した場合はtrue,それ以外はfalseを返します
   */
  public boolean update(T entity) {
    return helper.update(entity);
  }

  /**
   * エンティティを更新します.
   *
   * @param entityList エンティティ
   */
  public void updateAll(List<T> entityList) {
    helper.updateAll(entityList);
  }

  /**
   * エンティティを削除します.
   *
   * @param entity エンティティ
   */
  public void delete(T entity) {
    helper.delete(entity);
  }

  /**
   * エンティティリストを全て削除します.
   *
   * @param entityList エンティティリスト
   */
  public void deleteAll(List<T> entityList) {
    helper.deleteAll(entityList);
  }
}
