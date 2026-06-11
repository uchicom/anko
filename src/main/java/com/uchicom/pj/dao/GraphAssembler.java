// (C) 2026 uchicom
package com.uchicom.pj.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 検索結果（Row DTO）をグラフ構造の DTO に変換するアセンブラの基底クラス.
 *
 * @author uchicom
 * @param <R> Row DTO
 * @param <K> グルーピングキーの型
 * @param <D> グラフ DTO
 */
public abstract class GraphAssembler<R, K, D> {

  /**
   * 行リストをグラフ DTO リストに変換します.
   *
   * @param rows 行リスト
   * @return グラフ DTO リスト
   */
  public List<D> assemble(List<R> rows) {
    Map<K, D> map = new LinkedHashMap<>();
    for (R row : rows) {
      K key = key(row);
      D dto = map.get(key);
      if (dto == null) {
        map.put(key, create(row));
      } else {
        merge(dto, row);
      }
    }
    return new ArrayList<>(map.values());
  }

  /**
   * 行からグルーピングキーを取得します.
   *
   * @param row 行
   * @return キー
   */
  protected abstract K key(R row);

  /**
   * 行からグラフ DTO を生成します.
   *
   * @param row 行
   * @return グラフ DTO
   */
  protected abstract D create(R row);

  /**
   * グラフ DTO に行データをマージします.
   *
   * @param dto グラフ DTO
   * @param row 行
   */
  protected abstract void merge(D dto, R row);
}
