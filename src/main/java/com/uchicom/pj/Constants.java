// (C) 2023 uchicom
package com.uchicom.pj;

import com.uchicom.pj.enumeration.Config;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class Constants {

  /** ログイントークン */
  public static final String SECRET = Context.get(Config.SECRET);

  /** ゾーンID */
  public static final ZoneId ZONE_ID = ZoneId.of(Context.get(Config.ZONE_ID));

  /** ゾーンオフセット */
  public static final ZoneOffset ZONE_OFFSET =
      ZoneOffset.ofHours(Context.getInteger(Config.ZONE_OFFSET));

  /** パスフレーズハッシュのSALT付加情報. */
  public static final String SALT_SUFFIX = Context.get(Config.SALT_SUFFIX);

  /** ログ出力ディレクトリ. */
  public static final String LOG_DIR = "./logs";

  /** ログ出力フォーマット. */
  public static final String LOG_FORMAT =
      "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n";

  public static final Boolean HTTP_ONLY = Context.getBoolean(Config.HTTP_ONLY);

  public static final Boolean COOKIE_SECURE = Context.getBoolean(Config.COOKIE_SECURE);

  /** リフレッシュトークンソルト */
  public static final String REFRESH_TOKEN_SALT = Context.get(Config.REFRESH_TOKEN_SALT);

  /** リフレッシュトークン有効期間(日) */
  public static final int REFRESH_TOKEN_MAX_AGE_DAYS =
      Context.getInteger(Config.REFRESH_TOKEN_MAX_AGE_DAYS);

  /** アクセストークンCookieキー */
  public static final String ACCESS_TOKEN_KEY = Context.get(Config.ACCESS_TOKEN_KEY);

  /** リフレッシュトークンCookieキー */
  public static final String REFRESH_TOKEN_KEY = Context.get(Config.REFRESH_TOKEN_KEY);

  public static final int TOKEN_EXPIRE_MINUTES = Context.getInteger(Config.TOKEN_EXPIRE_MINUTES);

  /** アクセストークンリフレッシュ間隔(ミリ秒) */
  public static final int REFRESH_INTERVAL_MINUTES =
      Context.getInteger(Config.REFRESH_INTERVAL_MINUTES);
}
