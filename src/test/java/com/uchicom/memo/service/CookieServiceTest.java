// (C) 2023 uchicom
package com.uchicom.memo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.uchicom.memo.AbstractTest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;

/**
 * {@link Service}
 *
 * @author shigeki.uchiyama
 */
@Tag("service")
public class CookieServiceTest extends AbstractTest {

  @Spy @InjectMocks CookieService service;

  @Captor ArgumentCaptor<Cookie> cookieCaptor;
  @Captor ArgumentCaptor<String> jwtCaptor;
  @Captor ArgumentCaptor<String> keyCaptor;
  @Captor ArgumentCaptor<String> valueCaptor;
  @Captor ArgumentCaptor<String> loginIdCaptor;
  @Captor ArgumentCaptor<Cookie[]> cookiesCaptor;
  @Captor ArgumentCaptor<LocalDateTime> dateTimeCaptor;
  @Captor ArgumentCaptor<HttpServletResponse> resCaptor;
  @Captor ArgumentCaptor<Integer> maxAgeCaptor;

  @Test
  public void addJwt() throws Exception {
    // mock
    var res = mock(HttpServletResponse.class);
    doNothing().when(res).addCookie(cookieCaptor.capture());
    var cookie = mock(Cookie.class);
    doReturn(cookie).when(service).createJwtCookie(jwtCaptor.capture());

    String jwt = "token";
    // test
    service.addJwt(res, jwt);

    // assert
    assertThat(cookieCaptor.getValue()).isEqualTo(cookie);
    assertThat(jwtCaptor.getValue()).isEqualTo(jwt);
  }

  @Test
  public void removeJwt_null() throws Exception {
    // mock
    doReturn(null).when(service).getJwtCookie(cookiesCaptor.capture());

    var req = mock(HttpServletRequest.class);
    var cookies = new Cookie[0];
    doReturn(cookies).when(req).getCookies();
    var res = mock(HttpServletResponse.class);
    // test
    service.removeJwt(req, res);

    // assert
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
  }

  @Test
  public void removeJwt() throws Exception {
    // mock
    var cookie = mock(Cookie.class);
    doReturn(cookie).when(service).getJwtCookie(cookiesCaptor.capture());
    var cookie2 = mock(Cookie.class);
    doReturn(cookie2).when(service).createJwtCookie(jwtCaptor.capture());

    var req = mock(HttpServletRequest.class);
    var cookies = new Cookie[0];
    doReturn(cookies).when(req).getCookies();
    var res = mock(HttpServletResponse.class);
    doNothing().when(res).addCookie(cookieCaptor.capture());
    // test
    service.removeJwt(req, res);

    // assert
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
    assertThat(jwtCaptor.getValue()).isNull();
    assertThat(cookieCaptor.getValue().getMaxAge()).isEqualTo(0);
  }

  @Test
  public void createJwtCookie() throws Exception {
    // mock
    var cookie = mock(Cookie.class);
    doReturn(cookie).when(service).createCookie(keyCaptor.capture(), jwtCaptor.capture());
    var jwt = "token";
    // test
    var result = service.createJwtCookie(jwt);

    // assert
    assertThat(result).isEqualTo(cookie);
    assertThat(keyCaptor.getValue()).isEqualTo("jwt");
    assertThat(jwtCaptor.getValue()).isEqualTo(jwt);
  }

  @Test
  public void createLoginIdCookie() throws Exception {
    // mock
    var cookie = mock(Cookie.class);
    doNothing().when(cookie).setMaxAge(maxAgeCaptor.capture());
    doReturn(cookie).when(service).createCookie(keyCaptor.capture(), jwtCaptor.capture());
    var key = "key";
    var jwt = "token";
    // test
    var result = service.createLoginIdCookie(key, jwt);

    // assert
    assertThat(result).isEqualTo(cookie);
    assertThat(maxAgeCaptor.getValue()).isEqualTo(3600);
    assertThat(keyCaptor.getValue()).isEqualTo("key");
    assertThat(jwtCaptor.getValue()).isEqualTo(jwt);
  }

  @Test
  public void createCookie() throws Exception {
    // mock
    var encoded = "encoded";
    doReturn(encoded).when(service).encode(valueCaptor.capture());
    var key = "key";
    var jwt = "token";
    // test
    var cookie = service.createCookie(key, jwt);

    // assert
    assertThat(valueCaptor.getValue()).isEqualTo(jwt);
    assertThat(cookie.getName()).isEqualTo(key);
    assertThat(cookie.getValue()).isEqualTo(encoded);
    assertThat(cookie.getPath()).isEqualTo("/");
    assertThat(cookie.getSecure()).isTrue();
    assertThat(cookie.isHttpOnly()).isTrue();
  }

  @Test
  public void getJwtCookie() throws Exception {
    // mock
    var cookie = new Cookie("jwt", "token");
    doReturn(cookie).when(service).getCookie(cookiesCaptor.capture(), keyCaptor.capture());
    var cookies = new Cookie[0];

    // test
    var result = service.getJwtCookie(cookies);

    // assert
    assertThat(result).isEqualTo(cookie);
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
    assertThat(keyCaptor.getValue()).isEqualTo("jwt");
  }

  @Test
  public void getCookie() throws Exception {
    // mock
    var cookie = new Cookie("key", "token");
    var cookies = new Cookie[] {cookie};
    var key = "key";

    // test
    var result = service.getCookie(cookies, key);

    // assert
    assertThat(result).isEqualTo(cookie);
  }

  @Test
  public void getCookie_null() throws Exception {
    // mock
    var cookies = new Cookie[0];
    var key = "key";

    // test
    var result = service.getCookie(cookies, key);

    // assert
    assertThat(result).isNull();
  }

  @Test
  public void getCookie_cookie_null() throws Exception {
    // mock
    var key = "key";

    // test
    var result = service.getCookie(null, key);

    // assert
    assertThat(result).isNull();
  }

  @Test
  public void getValue() throws Exception {
    // mock
    var cookie = new Cookie("mpcsLoginId", "token");
    doReturn(cookie).when(service).getCookie(cookiesCaptor.capture(), keyCaptor.capture());
    var decoded = "decoded";
    doReturn(decoded).when(service).decode(valueCaptor.capture());

    var cookies = new Cookie[0];
    var key = "key";

    // test
    var result = service.getValue(cookies, key);

    // assert
    assertThat(result).isEqualTo(decoded);
    assertThat(valueCaptor.getValue()).isEqualTo(cookie.getValue());
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
    assertThat(keyCaptor.getValue()).isEqualTo(key);
  }

  @Test
  public void getValue_null() throws Exception {
    // mock
    doReturn(null).when(service).getCookie(cookiesCaptor.capture(), keyCaptor.capture());
    var cookies = new Cookie[0];
    var key = "key";

    // test
    var result = service.getValue(cookies, key);

    // assert
    assertThat(result).isNull();
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
    assertThat(keyCaptor.getValue()).isEqualTo(key);
  }

  @Test
  public void encode_null() throws Exception {
    // mock

    // test
    var result = service.encode(null);

    // assert
    assertThat(result).isNull();
  }

  @Test
  public void encode() throws Exception {
    // mock

    // test
    var result = service.encode("a");

    // assert
    assertThat(result).isNotNull();
  }

  @Test
  public void decode_null() throws Exception {
    // mock

    // test
    var result = service.decode(null);

    // assert
    assertThat(result).isNull();
  }

  @Test
  public void decode() throws Exception {
    // mock

    // test
    var result = service.decode("a");

    // assert
    assertThat(result).isNotNull();
  }
}
