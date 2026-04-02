// (C) 2026 uchicom
package com.uchicom.pj.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
  private static final String algo = "SHA3-512";

  public static byte[] getHash(String org, String salt) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance(algo);
    messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));
    return messageDigest.digest(org.getBytes(StandardCharsets.UTF_8));
  }
}
