// (C) 2023 uchicom
package com.uchicom.tracker.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.uchicom.tracker.Constants;
import jakarta.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 認可サービス.
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class AuthService {

  private final DateTimeService dateTimeService;
  private final CookieService cookieService;
  private final Logger logger;

  public AuthService(DateTimeService dateTimeService, CookieService cookieService, Logger logger) {
    this.dateTimeService = dateTimeService;
    this.cookieService = cookieService;
    this.logger = logger;
  }

  public String publish(long accountId) {
    return getJwtCreatorBuilder()
        .withExpiresAt(
            dateTimeService.getLocalDateTime().plusHours(1).toInstant(Constants.ZONE_OFFSET))
        .withSubject(String.valueOf(accountId))
        .sign(getAlgorithm());
  }

  Algorithm getAlgorithm() {
    return Algorithm.HMAC256(Constants.SECRET);
  }

  JWTCreator.Builder getJwtCreatorBuilder() {
    return JWT.create();
  }

  public String subject(String token) {
    return getJwtVerifier().verify(token).getSubject();
  }

  JWTVerifier getJwtVerifier() {
    return JWT.require(getAlgorithm()).build();
  }

  /**
   * 認証チェック.
   *
   * @param req リクエスト
   * @return 認証OKの場合はtrue,それ以外はfalseを返します
   */
  public boolean auth(HttpServletRequest req) {
    var token = cookieService.getValue(req.getCookies(), "jwt");
    if (token == null) {
      return false;
    }
    try {
      String subject = subject(token);
      if (subject.isBlank()) {
        logger.warning("id is blank");
        return false;
      }
      req.setAttribute("accountId", Long.parseLong(subject));
      return true;
    } catch (Exception e) {
      logger.log(Level.INFO, "認証エラー", e);
      return false;
    }
  }
}
