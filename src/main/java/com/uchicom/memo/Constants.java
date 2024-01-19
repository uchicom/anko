// (C) 2023 uchicom
package com.uchicom.memo;

import com.uchicom.memo.enumeration.Config;
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

  /** ログ出力ディレクトリ. */
  public static final String LOG_DIR = "./logs";

  /** ログ出力フォーマット. */
  public static final String LOG_FORMAT =
      "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n";
}
