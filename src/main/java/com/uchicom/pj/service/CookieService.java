// (C) 2023 uchicom
package com.uchicom.pj.service;

import com.uchicom.pj.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieService {

  public CookieService() {}

  public void addRefreshToken(HttpServletResponse res, String refreshToken) {
    res.addCookie(createRefreshTokenCookie(refreshToken));
  }

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

  public void removeRefreshToken(HttpServletRequest req, HttpServletResponse res) {
    var cookie = createRefreshTokenCookie(null);
    cookie.setMaxAge(0);
    res.addCookie(cookie);
  }

  Cookie createJwtCookie(String jwt) {
    return createCookie(Constants.ACCESS_TOKEN_KEY, jwt);
  }

  Cookie createRefreshTokenCookie(String refreshToken) {
    return createCookie(
        Constants.REFRESH_TOKEN_KEY,
        refreshToken,
        "/pj/api/account/refresh",
        Constants.REFRESH_TOKEN_MAX_AGE_DAYS * 24 * 3600);
  }

  Cookie createLoginIdCookie(String key, String loginId) {
    var cookie = createCookie(key, loginId);
    cookie.setMaxAge(3600);
    return cookie;
  }

  Cookie createCookie(String key, String value) {
    return createCookie(key, value, "/", null);
  }

  Cookie createCookie(String key, String value, String path, Integer maxAge) {
    var cookie = new Cookie(key, encode(value));
    cookie.setHttpOnly(Constants.HTTP_ONLY);
    cookie.setSecure(Constants.COOKIE_SECURE);
    cookie.setPath(path);
    if (maxAge != null) {
      cookie.setMaxAge(maxAge);
    }
    return cookie;
  }

  String getRefreshToken(HttpServletRequest req) {
    return getValue(req.getCookies(), Constants.REFRESH_TOKEN_KEY);
  }

  Cookie getRefreshTokenCookie(Cookie[] cookies) {
    return getCookie(cookies, Constants.REFRESH_TOKEN_KEY);
  }

  Cookie getJwtCookie(Cookie[] cookies) {
    return getCookie(cookies, Constants.ACCESS_TOKEN_KEY);
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
