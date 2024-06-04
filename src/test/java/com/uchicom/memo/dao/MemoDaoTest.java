// (C) 2023 uchicom
package com.uchicom.memo.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.memo.entity.Memo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link MemoDao}のテストケース.
 *
 * @author shigeki.uchiyama
 */
@Tag("dao")
public class MemoDaoTest extends AbstractDaoTest<Memo, MemoDao> {

  public MemoDaoTest() {
    super(Memo::new, helper -> new MemoDao(helper));
  }

  /** {@link MemoDao#findAll()}のテスト. */
  @Test
  public void findAll() throws Exception {
    test(
        () -> {
          dao.findAll();
          assertThat(getSQL()).isEqualTo("SELECT * FROM memo.memo ORDER BY id");
        });
  }

  /** {@link MemoDao#findByAccountId(long)}のテスト. */
  @Test
  public void findByAccountId() throws Exception {
    test(
        () -> {
          dao.findByAccountId(1L);
          assertThat(getSQL()).isEqualTo("SELECT * FROM memo.memo WHERE account_id = 1");
        });
  }

  /** {@link MemoDao#findByIdAndAccountId(long)}のテスト. */
  @Test
  public void findByIdAndAccountId() throws Exception {
    test(
        () -> {
          dao.findByIdAndAccountId(1L, 2L);
          assertThat(getSQL()).isEqualTo("SELECT * FROM memo.memo WHERE id = 1 AND account_id = 2");
        });
  }
}
