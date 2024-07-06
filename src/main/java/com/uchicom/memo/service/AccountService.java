// (C) 2023 uchicom
package com.uchicom.memo.service;

import com.uchicom.memo.dao.AccountDao;
import com.uchicom.memo.dto.request.account.AccountRegisterDto;
import com.uchicom.memo.dto.request.account.LoginDto;
import com.uchicom.memo.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.inject.Inject;

public class AccountService {

  private final AuthService authService;
  private final AccountDao accountDao;

  @Inject
  public AccountService(AuthService authService, AccountDao accountDao) {
    this.authService = authService;
    this.accountDao = accountDao;
  }

  public Account register(AccountRegisterDto dto) throws NoSuchAlgorithmException {
    if (accountDao.findByLoginId(dto.id) != null) {
      return null;
    }
    var account = new Account();
    account.login_id = dto.id;
    account.password = createPasswordHash(dto.id, dto.pass);
    account.name = dto.name;
    account.id = accountDao.insertAndGetKey(account);
    return account;
  }

  /**
   * ログイン認証を実施します.
   *
   * @return トークン
   */
  public String login(LoginDto dto) throws NoSuchAlgorithmException {
    var account = accountDao.findByLoginId(dto.id);

    // アカウントが存在しない場合
    if (account == null) {
      return null;
    }
    // ログインチェック
    if (!verifyPassword(account, dto.pass)) {
      // ログイン失敗
      return null;
    }
    return authService.publish(account.id);
  }

  String createSalt(String loginId) {
    // 再現可能な複雑な文字列を使用するとよい
    return loginId + "/abcdefghijklmnop";
  }

  byte[] createPasswordHash(String loginId, String password) throws NoSuchAlgorithmException {
    return getHash(password, createSalt(loginId));
  }

  byte[] getHash(String org, String salt) throws NoSuchAlgorithmException {
    var messageDigest = MessageDigest.getInstance("SHA3-512");
    messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));
    return messageDigest.digest(org.getBytes(StandardCharsets.UTF_8));
  }

  boolean verifyPassword(Account account, String password) throws NoSuchAlgorithmException {
    return Arrays.equals(account.password, createPasswordHash(account.login_id, password));
  }

  public boolean isLogin(HttpServletRequest req) {
    return authService.auth(req);
  }
}
