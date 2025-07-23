// (C) 2023 uchicom
package com.uchicom.tracker.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieService {

  public CookieService() {}

  public void addJwt(HttpServletResponse res, String jwt) {
    res.addCookie(createJwtCookie(jwt));
  }

  public void removeJwt(HttpServletRequest req, HttpServletResponse res) {
    if (getJwtCookie(req.getCookies()) == null) {
      return;
    }
    var cookie = createJwtCookie(null);
    cookie.setMaxAge(0);
    res.addCookie(cookie);
  }

  Cookie createJwtCookie(String jwt) {
    return createCookie("jwt", jwt);
  }

  Cookie createLoginIdCookie(String key, String loginId) {
    var cookie = createCookie(key, loginId);
    cookie.setMaxAge(3600);
    return cookie;
  }

  Cookie createCookie(String key, String value) {
    var cookie = new Cookie(key, encode(value));
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    return cookie;
  }

  Cookie getJwtCookie(Cookie[] cookies) {
    return getCookie(cookies, "jwt");
  }

  Cookie getCookie(Cookie[] cookies, String key) {
    if (cookies == null) {
      return null;
    }
    for (var cookie : cookies) {
      if (key.equals(cookie.getName())) {
        return cookie;
      }
    }
    return null;
  }

  String getValue(Cookie[] cookies, String key) {
    var cookie = getCookie(cookies, key);
    if (cookie == null) {
      return null;
    }
    return decode(cookie.getValue());
  }

  String encode(String value) {
    if (value == null) {
      return null;
    }
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  String decode(String value) {
    if (value == null) {
      return null;
    }
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }
}
