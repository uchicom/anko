// (C) 2023 uchicom
package com.uchicom.memo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.uchicom.memo.AbstractTest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

public class AuthServiceTest extends AbstractTest {

  @Mock DateTimeService dateTimeService;
  @Mock CookieService cookieService;
  @Mock Logger logger;
  @Mock HttpServletRequest req;

  @Spy @InjectMocks AuthService service;

  @Captor ArgumentCaptor<String> tokenCaptor;
  @Captor ArgumentCaptor<String> keyCaptor;
  @Captor ArgumentCaptor<Cookie[]> cookiesCaptor;
  @Captor ArgumentCaptor<String> nameCaptor;
  @Captor ArgumentCaptor<Object> objectCaptor;

  @Test
  public void auth_cookie_null() {
    // mock
    var cookies = new Cookie[0];
    doReturn(cookies).when(req).getCookies();
    doReturn(null).when(cookieService).getValue(cookiesCaptor.capture(), keyCaptor.capture());
    // test method
    var result = service.auth(req);
    // assert
    assertThat(result).isFalse();
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
    assertThat(keyCaptor.getValue()).isEqualTo("jwt");
    assertThat(tokenCaptor.getAllValues()).isEmpty();
  }

  @Test
  public void auth_blank() {
    // mock
    var cookies = new Cookie[0];
    doReturn(cookies).when(req).getCookies();
    doNothing().when(req).setAttribute(nameCaptor.capture(), objectCaptor.capture());
    var token = "1";
    doReturn(token).when(cookieService).getValue(cookiesCaptor.capture(), keyCaptor.capture());
    doReturn("").when(service).subject(tokenCaptor.capture());
    // test method
    var result = service.auth(req);
    // assert
    assertThat(result).isFalse();
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
    assertThat(keyCaptor.getValue()).isEqualTo("jwt");
    assertThat(tokenCaptor.getValue()).isEqualTo(token);
    assertThat(nameCaptor.getAllValues()).isEmpty();
    assertThat(objectCaptor.getAllValues()).isEmpty();
  }

  @Test
  public void auth() {
    // mock
    var cookies = new Cookie[0];
    doReturn(cookies).when(req).getCookies();
    doNothing().when(req).setAttribute(nameCaptor.capture(), objectCaptor.capture());
    var token = "1";
    doReturn(token).when(cookieService).getValue(cookiesCaptor.capture(), keyCaptor.capture());
    doReturn("2").when(service).subject(tokenCaptor.capture());
    // test method
    var result = service.auth(req);
    // assert
    assertThat(result).isTrue();
    assertThat(cookiesCaptor.getValue()).isEqualTo(cookies);
    assertThat(keyCaptor.getValue()).isEqualTo("jwt");
    assertThat(tokenCaptor.getValue()).isEqualTo(token);
    assertThat(nameCaptor.getValue()).isEqualTo("accountId");
    assertThat(objectCaptor.getValue()).isEqualTo(2L);
  }
}
