// (C) 2023 uchicom
package com.uchicom.pj.service;

import com.uchicom.pj.Constants;
import com.uchicom.pj.dao.AccountDao;
import com.uchicom.pj.dto.request.account.AccountRegisterDto;
import com.uchicom.pj.dto.request.account.LoginDto;
import com.uchicom.pj.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AccountService {

  private final AuthService authService;
  private final CookieService cookieService;
  private final RefreshTokenService refreshTokenService;
  private final AccountDao accountDao;

  public AccountService(
      AuthService authService,
      CookieService cookieService,
      RefreshTokenService refreshTokenService,
      AccountDao accountDao) {
    this.authService = authService;
    this.cookieService = cookieService;
    this.refreshTokenService = refreshTokenService;
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
  public boolean login(LoginDto dto, HttpServletResponse res) throws NoSuchAlgorithmException {
    var account = accountDao.findByLoginId(dto.id);

    // アカウントが存在しない場合
    if (account == null) {
      return false;
    }
    // ログインチェック
    if (!verifyPassword(account, dto.pass)) {
      // ログイン失敗
      return false;
    }
    var refreshToken = refreshTokenService.register(account.id);
    setToken(res, account, refreshToken);
    return true;
  }

  void setToken(HttpServletResponse res, Account account, String refreshToken)
      throws NoSuchAlgorithmException {

    cookieService.addRefreshToken(res, refreshToken);

    var token = authService.publish(account.id);
    cookieService.addJwt(res, token);
  }

  String createSalt(String loginId) {
    // 再現可能な複雑な文字列を使用するとよい
    return loginId + "/" + Constants.SALT_SUFFIX;
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

  public boolean refresh(HttpServletRequest req, HttpServletResponse res)
      throws NoSuchAlgorithmException {
    var refreshToken = cookieService.getRefreshToken(req);
    if (refreshToken == null) {
      return false;
    }
    var token = refreshTokenService.get(refreshToken);
    if (token == null) {
      return false;
    }
    var account = accountDao.findById(token.account_id);
    if (account == null) {
      return false;
    }
    setToken(res, account, refreshToken);
    return true;
  }

  public void logout(HttpServletRequest req, HttpServletResponse res) {
    cookieService.removeJwt(req, res);
    cookieService.removeRefreshToken(req, res);
    if (authService.auth(req)) {
      refreshTokenService.delete((Long) req.getAttribute("accountId"));
    }
  }
}
