// (C) 2023 uchicom
package com.uchicom.memo.dao;

import com.uchicom.memo.entity.Account;
import javax.inject.Inject;

public class AccountDao extends AbstractDao<Account> {

  /** コンストラクタ. */
  @Inject
  public AccountDao(DbHelper<Account> helper) {
    super(helper);
  }

  public Account findByLoginId(String loginId) {
    //     helper.getDb().executeUpdate("""
    // create table account(
    //   id bigint auto_increment,
    //   inserted varchar(256) not null,
    //   insert_datetime timestamp not null,
    //   updated varchar(256),
    //   update_datetime timestamp,
    //   update_seq integer,
    //   login_id varchar(128) not null,
    //   password varbinary(128) not null,
    //   name varchar(128) not null,
    //   primary key(id)
    //   );
    //   create table memo(
    //     id bigint auto_increment,
    //     inserted varchar(256) not null,
    //     insert_datetime timestamp not null,
    //     updated varchar(256),
    //     update_datetime timestamp,
    //     update_seq integer,
    //     account_id bigint not null,
    //     title varbinary(1024) not null,
    //     content varchar(1024) not null,
    //     primary key(id)
    //     );
    //       """);
    var account = new Account();
    return helper.from(account).where(account.login_id).is(loginId).selectFirst();
  }
}
