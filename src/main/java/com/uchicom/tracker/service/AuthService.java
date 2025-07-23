// (C) 2023 uchicom
package com.uchicom.tracker.service;

import com.auth0.jwt.JWT;
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
    var algorithm = Algorithm.HMAC256(Constants.SECRET);
    var builder =
        JWT.create()
            .withExpiresAt(
                dateTimeService.getLocalDateTime().plusHours(1).toInstant(Constants.ZONE_OFFSET));
    builder.withSubject(String.valueOf(accountId));
    return builder.sign(algorithm);
  }

  public String subject(String token) {
    var algorithm = Algorithm.HMAC256(Constants.SECRET);
    return JWT.require(algorithm).build().verify(token).getSubject();
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
