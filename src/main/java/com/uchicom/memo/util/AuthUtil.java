// (C) 2023 uchicom
package com.uchicom.memo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uchicom.memo.Constants;
import com.uchicom.memo.module.MainModule;
import dagger.Component;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.Request;

/**
 * 認証ユーティリティ.
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class AuthUtil {
  /** ロガー */
  private static final Logger logger = DaggerAuthUtil_MainComponent.create().logger();

  @Component(modules = MainModule.class)
  interface MainComponent {
    Logger logger();
  }

  public static String publish(LocalDateTime now, long accountId) {
    var algorithm =
        Algorithm.HMAC256(Constants.SECRET); // ここではあえて単純化しています。SECRETは複雑な文字列や、可変にしたりすると良いです。
    var builder = JWT.create().withExpiresAt(now.plusHours(1).toInstant(Constants.ZONE_OFFSET));
    builder.withSubject(String.valueOf(accountId));
    return builder.sign(algorithm);
  }

  public static String subject(String token) {
    var algorithm = Algorithm.HMAC256(Constants.SECRET);
    return JWT.require(algorithm).build().verify(token).getSubject();
  }

  /**
   * 認証チェック.
   *
   * @param req リクエスト
   * @return 認証OKの場合はtrue,それ以外はfalseを返します
   */
  public static boolean auth(Request req) {
    String token = req.headers("token");
    if (token == null) {
      return false;
    }
    try {
      String subject = subject(token);
      if (subject.isBlank()) {
        logger.warning("id is blank");
        return false;
      }
      req.attribute("accountId", Long.parseLong(subject));
      return true;
    } catch (Exception e) {
      logger.log(Level.INFO, "認証エラー", e);
      return false;
    }
  }
}
