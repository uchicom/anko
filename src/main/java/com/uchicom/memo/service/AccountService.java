// (C) 2023 uchicom
package com.uchicom.memo.service;

import com.uchicom.memo.dao.AccountDao;
import com.uchicom.memo.entity.Account;
import com.uchicom.memo.util.AuthUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.inject.Inject;
import spark.Request;

public class AccountService {

  private final DateTimeService dateTimeService;
  private final AccountDao accountDao;

  @Inject
  public AccountService(DateTimeService dateTimeService, AccountDao accountDao) {
    this.dateTimeService = dateTimeService;
    this.accountDao = accountDao;
  }

  public Account getAccount(long accountId) {
    return accountDao.findById(accountId);
  }

  public Account getAccount(Request req) {
    return getAccount(getAccountId(req));
  }

  public long getAccountId(Request req) {
    return (long) req.attribute("accountId");
  }

  public Account register(String loginId, String password, String name)
      throws NoSuchAlgorithmException {
    if (accountDao.findByLoginId(loginId) != null) {
      return null;
    }
    var account = new Account();
    account.login_id = loginId;
    account.password = createPasswordHash(loginId, password);
    account.name = name;
    account.id = accountDao.insertAndGetKey(account);
    return account;
  }

  /**
   * ログイン認証を実施します.
   *
   * @param loginId Eメールアドレス
   * @param password パスワード
   * @return トークン
   */
  public String login(String loginId, String password) throws NoSuchAlgorithmException {
    var account = accountDao.findByLoginId(loginId);

    // アカウントが存在しない場合
    if (account == null) {
      return null;
    }
    // ログインチェック
    if (!verifyPassword(account, password)) {
      // ログイン失敗
      return null;
    }
    return AuthUtil.publish(dateTimeService.getLocalDateTime(), account.id);
  }

  String createSalt(String loginId) {
    return loginId + "/abcdefghijklmnop";
  }

  public byte[] createPasswordHash(String loginId, String password)
      throws NoSuchAlgorithmException {
    return getHash(password, createSalt(loginId));
  }

  public static byte[] getHash(String org, String salt) throws NoSuchAlgorithmException {
    var messageDigest = MessageDigest.getInstance("SHA3-512");
    messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));
    return messageDigest.digest(org.getBytes(StandardCharsets.UTF_8));
  }

  public boolean verifyPassword(Account account, String password) throws NoSuchAlgorithmException {
    return Arrays.equals(account.password, createPasswordHash(account.login_id, password));
  }
}
