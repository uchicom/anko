// (C) 2026 uchicom
package com.uchicom.pj.service;

import com.uchicom.pj.Constants;
import com.uchicom.pj.dao.RefreshTokenDao;
import com.uchicom.pj.entity.RefreshToken;
import com.uchicom.pj.util.SecurityUtil;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/** リフレッシュトークンサービス. */
public class RefreshTokenService {

  private final DateTimeService dateTimeService;
  private final RefreshTokenDao refreshTokenDao;

  public RefreshTokenService(DateTimeService dateTimeService, RefreshTokenDao refreshTokenDao) {
    this.dateTimeService = dateTimeService;
    this.refreshTokenDao = refreshTokenDao;
  }

  public RefreshToken get(String token) throws NoSuchAlgorithmException {
    var tokenHash = SecurityUtil.getHash(token, Constants.REFRESH_TOKEN_SALT);
    var refreshToken =
        refreshTokenDao.findRefreshable(tokenHash, dateTimeService.getLocalDateTime());
    if (refreshToken == null) {
      return null;
    }
    refreshToken.expire_datetime =
        dateTimeService.getLocalDateTime().plusDays(Constants.REFRESH_TOKEN_MAX_AGE_DAYS);
    refreshTokenDao.update(refreshToken);
    return refreshToken;
  }

  public String register(Long accountId) throws NoSuchAlgorithmException {
    var refreshToken = refreshTokenDao.findByAccountId(accountId);
    var token = generateToken();
    var now = dateTimeService.getLocalDateTime();
    if (refreshToken == null) {
      refreshToken = new RefreshToken();
      refreshToken.account_id = accountId;
      refreshToken.token_hash = SecurityUtil.getHash(token, Constants.REFRESH_TOKEN_SALT);
      refreshToken.expire_datetime = now.plusDays(Constants.REFRESH_TOKEN_MAX_AGE_DAYS);
      refreshTokenDao.insert(refreshToken);
    } else if (refreshToken.inactive_datetime == null) {
      refreshToken.token_hash = SecurityUtil.getHash(token, Constants.REFRESH_TOKEN_SALT);
      refreshToken.expire_datetime = now.plusDays(Constants.REFRESH_TOKEN_MAX_AGE_DAYS);
      refreshTokenDao.update(refreshToken);
    } else {
      return null;
    }
    return token;
  }

  String generateToken() {
    var bytes = new byte[32];
    new SecureRandom().nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public void delete(Long accountId) {
    var refreshToken = refreshTokenDao.findByAccountId(accountId);
    if (refreshToken == null) {
      return;
    }
    if (refreshToken.inactive_datetime == null) {
      refreshTokenDao.delete(refreshToken);
    }
  }
}
