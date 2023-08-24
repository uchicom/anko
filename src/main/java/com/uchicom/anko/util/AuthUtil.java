package com.uchicom.anko.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uchicom.anko.Constants;
import com.uchicom.anko.enumeration.Position;
import com.uchicom.anko.module.MainModule;

import dagger.Component;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
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

  public static String publish(
      LocalDateTime now, long accountId) {
    LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
    Random random = new Random(nowHour.toEpochSecond(Constants.ZONE_OFFSET) * 1000);
    String secret = Constants.SECRET_PREFIX + Long.toHexString(random.nextLong());
    Algorithm algorithm = Algorithm.HMAC256(secret);
    var builder = JWT.create().withExpiresAt(now.plusHours(1).toInstant(Constants.ZONE_OFFSET));
    builder.withSubject(String.valueOf(accountId));

    return builder.sign(algorithm);
  }

  public static String[] parse(String token, LocalDateTime now) {
    LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
    Random random = new Random(nowHour.toEpochSecond(Constants.ZONE_OFFSET) * 1000);
    try {
      String secret = Constants.SECRET_PREFIX + Long.toHexString(random.nextLong());
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm).build().verify(token).getSubject().split("\t");
    } catch (Exception e) {
      random.setSeed(nowHour.minusHours(1).toEpochSecond(Constants.ZONE_OFFSET) * 1000);
      String secret = Constants.SECRET_PREFIX + Long.toHexString(random.nextLong());
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm).build().verify(token).getSubject().split("\t");
    }
  }

  /**
   * 認証チェック.
   *
   * @param req リクエスト
   * @param positions 役割配列
   * @return 認証OKの場合はtrue,それ以外はfalseを返します
   */
  public static boolean auth(Request req, LocalDateTime now, Position[] positions) {
    String token = req.headers("token");
    if (token != null) {
      try {
        String[] splits = parse(token, now);
        if (splits.length > 0) {
          req.attribute("accountId", Long.parseLong(splits[0]));
          if (splits.length > 2) {
            req.attribute("organizationId", Long.parseLong(splits[2]));
          }
          if (positions.length > 0) {
            if (splits.length > 1) {
              String role = splits[1];
              logger.info("id:" + splits[0] + ",role:" + role);
              var position = Position.get(role);
              req.attribute("role", position);
              if (position == null) return false;
              return Arrays.binarySearch(positions, position) >= 0;
            }
            logger.warning("id:" + splits[0]);
            return false;
          }
          logger.info("id:" + splits[0]);
          return true;
        }
      } catch (Exception e) {
        logger.log(Level.INFO, "認証エラー", e);
        return false;
      }
    }
    return false;
  }

  static String createSubject(long accountId, Position position, Long organizationId) {
    var builder = new StringBuilder();
    builder
        .append(accountId)
        .append("\t")
        .append(Optional.ofNullable(position).orElse(Position.OWNER));
    if (organizationId != null) {
      builder.append("\t").append(organizationId);
    }
    return builder.toString();
  }
}
