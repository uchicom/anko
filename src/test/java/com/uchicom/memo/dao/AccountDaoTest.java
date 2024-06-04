// (C) 2023 uchicom
package com.uchicom.memo.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.memo.entity.Account;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link AccountDao}のテストケース.
 *
 * @author shigeki.uchiyama
 */
@Tag("dao")
public class AccountDaoTest extends AbstractDaoTest<Account, AccountDao> {

  public AccountDaoTest() {
    super(Account::new, helper -> new AccountDao(helper));
  }

  /** {@link AccountDao#findAll()}のテスト. */
  @Test
  public void findAll() throws Exception {
    test(
        () -> {
          dao.findAll();
          assertThat(getSQL()).isEqualTo("SELECT * FROM memo.account ORDER BY id");
        });
  }

  /** {@link AccountDao#findByLoginId(String)}のテスト. */
  @Test
  public void findByLoginId() throws Exception {
    test(
        () -> {
          dao.findByLoginId("loginId");
          assertThat(getSQL()).isEqualTo("SELECT * FROM memo.account WHERE login_id = 'loginId'");
        });
  }
}
