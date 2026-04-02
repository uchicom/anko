// (C) 2023 uchicom
package com.uchicom.pj.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dao.AccountDao;
import com.uchicom.pj.dto.request.account.AccountRegisterDto;
import com.uchicom.pj.dto.request.account.LoginDto;
import com.uchicom.pj.entity.Account;
import com.uchicom.pj.entity.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

/**
 * {@link AccountService}のテストケース.
 *
 * @author uchicom
 */
@Tag("service")
public class AccountServiceTest extends AbstractTest {

  @Mock DateTimeService dateTimeService;
  @Mock AuthService authService;
  @Mock CookieService cookieService;
  @Mock RefreshTokenService refreshTokenService;
  @Mock AccountDao accountDao;
  @Mock HttpServletResponse res;

  @Captor ArgumentCaptor<String> loginIdCaptor;
  @Captor ArgumentCaptor<String> passwordCaptor;
  @Captor ArgumentCaptor<Account> accountCaptor;
  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<HttpServletRequest> reqCaptor;
  @Captor ArgumentCaptor<HttpServletResponse> resCaptor;
  @Captor ArgumentCaptor<String> saltCaptor;
  @Captor ArgumentCaptor<String> tokenCaptor;

  @Spy @InjectMocks AccountService service;

  @Test
  public void register() throws Exception {
    // mock
    doReturn(null).when(accountDao).findByLoginId(loginIdCaptor.capture());
    var hash = "hash".getBytes(StandardCharsets.ISO_8859_1);
    doReturn(hash)
        .when(service)
        .createPasswordHash(loginIdCaptor.capture(), passwordCaptor.capture());
    doReturn(1L).when(accountDao).insertAndGetKey(accountCaptor.capture());

    var dto = new AccountRegisterDto();
    dto.id = "loginId";
    dto.pass = "password";
    dto.name = "name";

    // method
    var result = service.register(dto);

    // assert
    assertThat(result.id).isEqualTo(1L);
    assertThat(result.login_id).isEqualTo("loginId");
    assertThat(result.password).isEqualTo(hash);
    assertThat(result.name).isEqualTo("name");
    var loginIds = loginIdCaptor.getAllValues();
    assertThat(loginIds).hasSize(2);
    assertThat(loginIds.get(0)).isEqualTo("loginId");
    assertThat(loginIds.get(1)).isEqualTo("loginId");
    assertThat(passwordCaptor.getValue()).isEqualTo("password");
  }

  @Test
  public void login_notExist() throws Exception {
    // mock
    doReturn(null).when(accountDao).findByLoginId(loginIdCaptor.capture());
    var dto = new LoginDto();
    dto.id = "loginId";
    dto.pass = "password";

    // method
    var result = service.login(dto, res);

    // assert
    assertThat(result).isFalse();

    assertThat(loginIdCaptor.getValue()).isEqualTo("loginId");
  }

  @Test
  public void login_notVerify() throws Exception {
    // mock
    var account = new Account();
    doReturn(account).when(accountDao).findByLoginId(loginIdCaptor.capture());
    doReturn(false).when(service).verifyPassword(accountCaptor.capture(), passwordCaptor.capture());
    var dto = new LoginDto();
    dto.id = "loginId";
    dto.pass = "password";

    // method
    var result = service.login(dto, res);

    // assert
    assertThat(result).isFalse();

    assertThat(loginIdCaptor.getValue()).isEqualTo("loginId");
    assertThat(accountCaptor.getValue()).isEqualTo(account);
    assertThat(passwordCaptor.getValue()).isEqualTo("password");
  }

  @Test
  public void login() throws Exception {
    // mock
    var account = new Account();
    account.id = 1L;
    doReturn(account).when(accountDao).findByLoginId(loginIdCaptor.capture());
    doReturn(true).when(service).verifyPassword(accountCaptor.capture(), passwordCaptor.capture());
    doReturn(LocalDateTime.of(2023, 8, 25, 12, 34, 56)).when(dateTimeService).getLocalDateTime();
    var token = "token";
    doReturn(token).when(authService).publish(accountIdCaptor.capture());
    var dto = new LoginDto();
    dto.id = "loginId";
    dto.pass = "password";

    // method
    var result = service.login(dto, res);

    // assert
    assertThat(result).isTrue();

    assertThat(loginIdCaptor.getValue()).isEqualTo("loginId");
    assertThat(accountCaptor.getValue()).isEqualTo(account);
    assertThat(passwordCaptor.getValue()).isEqualTo("password");
    assertThat(accountIdCaptor.getValue()).isEqualTo(account.id);
  }

  @Test
  public void register_alreadyExist() throws Exception {
    // mock
    doReturn(new Account()).when(accountDao).findByLoginId(loginIdCaptor.capture());
    var dto = new AccountRegisterDto();
    dto.id = "loginId";
    dto.pass = "password";
    dto.name = "name";

    // method
    var result = service.register(dto);

    // assert
    assertThat(result).isNull();
    assertThat(loginIdCaptor.getValue()).isEqualTo("loginId");
  }

  @Test
  public void refresh_invalidToken() throws Exception {
    // mock
    var req = mock(HttpServletRequest.class);
    var res = mock(HttpServletResponse.class);
    doReturn("refreshToken").when(cookieService).getRefreshToken(req);
    doReturn(null).when(refreshTokenService).get("refreshToken");

    // test
    var result = service.refresh(req, res);

    // assert
    assertThat(result).isFalse();
  }

  @Test
  public void refresh_noAccount() throws Exception {
    // mock
    var req = mock(HttpServletRequest.class);
    var res = mock(HttpServletResponse.class);
    doReturn("refreshToken").when(cookieService).getRefreshToken(req);
    var refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.account_id = 1L;
    doReturn(refreshTokenEntity).when(refreshTokenService).get("refreshToken");
    doReturn(null).when(accountDao).findById(1L);

    // test
    var result = service.refresh(req, res);

    // assert
    assertThat(result).isFalse();
  }

  @Test
  public void verifyPassword_false() throws Exception {
    // mock
    var hash = "other".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
    doReturn(hash)
        .when(service)
        .createPasswordHash(loginIdCaptor.capture(), passwordCaptor.capture());
    var account = new Account();
    account.login_id = "loginId";
    account.password = "password".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
    var password = "wrongPassword";

    // method
    var result = service.verifyPassword(account, password);

    // assert
    assertThat(result).isFalse();
    assertThat(loginIdCaptor.getValue()).isEqualTo(account.login_id);
    assertThat(passwordCaptor.getValue()).isEqualTo(password);
  }

  @Test
  public void createSalt() throws Exception {
    // mock
    var loginId = "loginId";

    // method
    var result = service.createSalt(loginId);

    // assert
    assertThat(result).isEqualTo("loginId/abcdefghijklmnop");
  }

  @Test
  public void createPasswordHash() throws Exception {
    // mock
    var salt = "salt";
    doReturn(salt).when(service).createSalt(loginIdCaptor.capture());
    var hash = "hash".getBytes(StandardCharsets.US_ASCII);
    doReturn(hash).when(service).getHash(passwordCaptor.capture(), saltCaptor.capture());
    var loginId = "loginId";
    var password = "password";

    // method
    var result = service.createPasswordHash(loginId, password);

    // assert
    assertThat(result).isEqualTo(hash);
    assertThat(loginIdCaptor.getValue()).isEqualTo(loginId);
    assertThat(passwordCaptor.getValue()).isEqualTo(password);
    assertThat(saltCaptor.getValue()).isEqualTo(salt);
  }

  @Test
  public void verifyPassword() throws Exception {
    // mock
    var hash = "password".getBytes(StandardCharsets.US_ASCII);
    doReturn(hash)
        .when(service)
        .createPasswordHash(loginIdCaptor.capture(), passwordCaptor.capture());
    var account = new Account();
    account.login_id = "loginId";
    account.password = "password".getBytes(StandardCharsets.US_ASCII);
    var password = "password";

    // method
    var result = service.verifyPassword(account, password);

    // assert
    assertThat(result).isTrue();
    assertThat(loginIdCaptor.getValue()).isEqualTo(account.login_id);
    assertThat(passwordCaptor.getValue()).isEqualTo(password);
  }

  @Test
  public void isLogin() throws Exception {
    // mock
    var bool = true;
    doReturn(bool).when(authService).auth(reqCaptor.capture());

    var req = mock(HttpServletRequest.class);
    // test
    var result = service.isLogin(req);

    // assert
    assertThat(result).isEqualTo(bool);
    assertThat(reqCaptor.getValue()).isEqualTo(req);
  }

  @Test
  public void setToken() throws Exception {
    // mock
    var account = new Account();
    account.id = 1L;
    var refreshToken = "refreshToken";
    var res = mock(HttpServletResponse.class);
    doNothing().when(cookieService).addRefreshToken(resCaptor.capture(), tokenCaptor.capture());
    doReturn("jwt").when(authService).publish(accountIdCaptor.capture());
    doNothing().when(cookieService).addJwt(resCaptor.capture(), tokenCaptor.capture());

    // test
    service.setToken(res, account, refreshToken);

    // assert
    assertThat(resCaptor.getAllValues().get(0)).isEqualTo(res);
    assertThat(tokenCaptor.getAllValues().get(0)).isEqualTo(refreshToken);
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(resCaptor.getAllValues().get(1)).isEqualTo(res);
    assertThat(tokenCaptor.getAllValues().get(1)).isEqualTo("jwt");
  }

  @Test
  public void refresh_noRefreshToken() throws Exception {
    // mock
    var req = mock(HttpServletRequest.class);
    var res = mock(HttpServletResponse.class);
    doReturn(null).when(cookieService).getRefreshToken(req);

    // test
    var result = service.refresh(req, res);

    // assert
    assertThat(result).isFalse();
  }

  @Test
  public void refresh_success() throws Exception {
    // mock
    var req = mock(HttpServletRequest.class);
    var res = mock(HttpServletResponse.class);
    doReturn("refreshToken").when(cookieService).getRefreshToken(req);
    var refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.account_id = 1L;
    doReturn(refreshTokenEntity).when(refreshTokenService).get("refreshToken");
    var account = new Account();
    account.id = 1L;
    doReturn(account).when(accountDao).findById(1L);
    doNothing()
        .when(service)
        .setToken(resCaptor.capture(), accountCaptor.capture(), tokenCaptor.capture());

    // test
    var result = service.refresh(req, res);

    // assert
    assertThat(result).isTrue();
    assertThat(resCaptor.getValue()).isEqualTo(res);
    assertThat(accountCaptor.getValue()).isEqualTo(account);
    assertThat(tokenCaptor.getValue()).isEqualTo("refreshToken");
  }

  @Test
  public void logout_withAccountId() throws Exception {
    // mock
    var req = mock(HttpServletRequest.class);
    var res = mock(HttpServletResponse.class);
    doNothing().when(cookieService).removeJwt(reqCaptor.capture(), resCaptor.capture());
    doNothing().when(cookieService).removeRefreshToken(reqCaptor.capture(), resCaptor.capture());
    doReturn(true).when(authService).auth(reqCaptor.capture());
    doReturn(1L).when(req).getAttribute("accountId");
    doNothing().when(refreshTokenService).delete(accountIdCaptor.capture());

    // test
    service.logout(req, res);

    // assert
    var reqs = reqCaptor.getAllValues();
    assertThat(reqs.get(0)).isEqualTo(req);
    assertThat(reqs.get(1)).isEqualTo(req);
    assertThat(reqs.get(2)).isEqualTo(req);
    assertThat(resCaptor.getAllValues().get(0)).isEqualTo(res);
    assertThat(resCaptor.getAllValues().get(1)).isEqualTo(res);
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
  }

  @Test
  public void logout_noAccountId() throws Exception {
    // mock
    var req = mock(HttpServletRequest.class);
    var res = mock(HttpServletResponse.class);
    doNothing().when(cookieService).removeJwt(reqCaptor.capture(), resCaptor.capture());
    doNothing().when(cookieService).removeRefreshToken(reqCaptor.capture(), resCaptor.capture());
    doReturn(false).when(authService).auth(reqCaptor.capture());

    // test
    service.logout(req, res);

    // assert
    assertThat(reqCaptor.getAllValues().get(0)).isEqualTo(req);
    assertThat(reqCaptor.getAllValues().get(1)).isEqualTo(req);
    assertThat(reqCaptor.getAllValues().get(2)).isEqualTo(req);
    assertThat(resCaptor.getAllValues().get(0)).isEqualTo(res);
    assertThat(resCaptor.getAllValues().get(1)).isEqualTo(res);
  }
}
