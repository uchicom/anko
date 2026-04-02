// (C) 2026 uchicom
package com.uchicom.pj.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** {@link SecurityUtil}のテストケース. */
public class SecurityUtilTest {

  /** {@link SecurityUtil#getHash(String, String)}でハッシュが生成されるテスト. */
  @Test
  public void getHash() throws Exception {
    // test
    var result = SecurityUtil.getHash("password", "salt");

    // assert
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
  }

  /** {@link SecurityUtil#getHash(String, String)}で同じ入力から同じハッシュが生成されるテスト. */
  @Test
  public void getHash_deterministic() throws Exception {
    // test
    var result1 = SecurityUtil.getHash("password", "salt");
    var result2 = SecurityUtil.getHash("password", "salt");

    // assert
    assertThat(result1).isEqualTo(result2);
  }

  /** {@link SecurityUtil#getHash(String, String)}でsaltが異なると異なるハッシュが生成されるテスト. */
  @Test
  public void getHash_differentSalt() throws Exception {
    // test
    var result1 = SecurityUtil.getHash("password", "salt1");
    var result2 = SecurityUtil.getHash("password", "salt2");

    // assert
    assertThat(result1).isNotEqualTo(result2);
  }
}
