// (C) 2023 uchicom
package com.uchicom.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.uchicom.tracker.AbstractTest;
import com.uchicom.tracker.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
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
  @Captor ArgumentCaptor<Instant> instantCaptor;
  @Captor ArgumentCaptor<String> subjectCaptor;
  @Captor ArgumentCaptor<Algorithm> algorithmCaptor;

  @Test
  public void publish() {
    // mock
    var builder = mock(JWTCreator.Builder.class);
    doReturn(builder).when(service).getJwtCreatorBuilder();
    when(builder.withExpiresAt(instantCaptor.capture())).thenReturn(builder);
    when(builder.withSubject(subjectCaptor.capture())).thenReturn(builder);
    var signed = "signed";
    when(builder.sign(algorithmCaptor.capture())).thenReturn(signed);
    var datetime = LocalDateTime.of(2025, 9, 20, 12, 34, 56);
    when(dateTimeService.getLocalDateTime()).thenReturn(datetime);
    var accountId = 1L;

    // test
    var result = service.publish(accountId);

    // assert
    assertThat(result).isEqualTo(signed);
    assertThat(instantCaptor.getValue())
        .isEqualTo(datetime.plusHours(1).toInstant(Constants.ZONE_OFFSET));
    assertThat(subjectCaptor.getValue()).isEqualTo("1");
  }

  @Test
  public void subject() {
    // mock
    var jwtVerifier = mock(JWTVerifier.class);
    doReturn(jwtVerifier).when(service).getJwtVerifier();
    var decodedJwt = mock(DecodedJWT.class);
    when(jwtVerifier.verify(tokenCaptor.capture())).thenReturn(decodedJwt);
    var subject = "subject";
    when(decodedJwt.getSubject()).thenReturn(subject);
    var token = "token";

    // test
    var result = service.subject(token);

    // assert
    assertThat(result).isEqualTo(subject);
    assertThat(tokenCaptor.getValue()).isEqualTo(token);
  }

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
