package com.uchicom.anko.enumeration;

import java.util.Arrays;

public enum Position {
  OWNER,
  MEMBER,
  ADMIN;

  public static Position get(String role) {
    return Arrays.stream(values())
        .filter(roleCode -> roleCode.name().equals(role))
        .findFirst()
        .orElse(null);
  }

  public boolean is(String role) {
    return this == get(role);
  }

  public boolean isAdmin() {
    return this == ADMIN;
  }
}

