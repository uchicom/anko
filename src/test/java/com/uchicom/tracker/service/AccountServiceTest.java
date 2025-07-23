// (C) 2023 uchicom
package com.uchicom.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.uchicom.tracker.AbstractTest;
import com.uchicom.tracker.dao.AccountDao;
import com.uchicom.tracker.dto.request.account.AccountRegisterDto;
import com.uchicom.tracker.dto.request.account.LoginDto;
import com.uchicom.tracker.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
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
  @Mock AccountDao accountDao;

  @Captor ArgumentCaptor<String> loginIdCaptor;
  @Captor ArgumentCaptor<String> passwordCaptor;
  @Captor ArgumentCaptor<Account> accountCaptor;
  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<HttpServletRequest> reqCaptor;

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
    var result = service.login(dto);

    // assert
    assertThat(result).isNull();

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
    var result = service.login(dto);

    // assert
    assertThat(result).isNull();

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
    var result = service.login(dto);

    // assert
    assertThat(result).isEqualTo(token);

    assertThat(loginIdCaptor.getValue()).isEqualTo("loginId");
    assertThat(accountCaptor.getValue()).isEqualTo(account);
    assertThat(passwordCaptor.getValue()).isEqualTo("password");
    assertThat(accountIdCaptor.getValue()).isEqualTo(account.id);
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
}
