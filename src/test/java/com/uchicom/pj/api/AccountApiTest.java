// (C) 2023 uchicom
package com.uchicom.pj.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dto.request.account.AccountRegisterDto;
import com.uchicom.pj.dto.request.account.LoginDto;
import com.uchicom.pj.dto.response.ErrorDto;
import com.uchicom.pj.dto.response.MessageDto;
import com.uchicom.pj.dto.response.account.ResultDto;
import com.uchicom.pj.entity.Account;
import com.uchicom.pj.enumeration.ApiResult;
import com.uchicom.pj.service.AccountService;
import com.uchicom.pj.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("api")
public class AccountApiTest extends AbstractTest {
  @Mock HttpServletRequest req;
  @Mock HttpServletResponse res;

  @Mock AccountService accountService;
  @Mock CookieService cookieService;

  @Captor ArgumentCaptor<AccountRegisterDto> accountRegisterDtoCaptor;
  @Captor ArgumentCaptor<LoginDto> loginDtoCaptor;
  @Captor ArgumentCaptor<HttpServletRequest> reqCaptor;
  @Captor ArgumentCaptor<HttpServletResponse> resCaptor;

  @Spy @InjectMocks AccountApi api;

  @BeforeEach
  public void setUp() {
    createApiMock(api);
  }

  @Test
  public void register_registered() throws Exception {
    // mock
    var dto = new AccountRegisterDto();
    doReturn(null).when(accountService).register(accountRegisterDtoCaptor.capture());

    // test
    var result = api.register(dto, req, res);

    // assert
    if (result instanceof ErrorDto errorDto) {
      assertThat(errorDto.errorMessage).isEqualTo("既に登録済みのログインIDです。");
    } else {
      fail();
    }
    assertThat(accountRegisterDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void register() throws Exception {
    // mock
    var dto = new AccountRegisterDto();
    doReturn(new Account()).when(accountService).register(accountRegisterDtoCaptor.capture());

    // test
    var result = api.register(dto, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("アカウント登録しました。");
    } else {
      fail();
    }
    assertThat(accountRegisterDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void login_fail() throws Exception {
    // mock
    doReturn(false).when(accountService).login(loginDtoCaptor.capture(), resCaptor.capture());

    var dto = new LoginDto();

    // test
    var result = api.login(dto, req, res);

    // assert
    if (result instanceof ErrorDto errorDto) {
      assertThat(errorDto.errorMessage).isEqualTo("認証エラー");
    } else {
      fail();
    }
    assertThat(loginDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void login() throws Exception {
    // mock
    doReturn(true).when(accountService).login(loginDtoCaptor.capture(), resCaptor.capture());

    var dto = new LoginDto();

    // test
    var result = api.login(dto, req, res);

    // assert
    if (result instanceof ResultDto resultDto) {
      assertThat(resultDto.result).isEqualTo(ApiResult.OK);
    } else {
      fail();
    }
    assertThat(loginDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void refresh_NG() throws Exception {

    doReturn(false).when(accountService).refresh(reqCaptor.capture(), resCaptor.capture());

    // test method
    var result = api.refresh(req, res);

    // assert
    if (result instanceof ResultDto resultDto) {
      assertThat(resultDto.result).isEqualTo(ApiResult.NG);
    } else {
      fail();
    }
    assertThat(reqCaptor.getValue()).isEqualTo(req);
    assertThat(resCaptor.getValue()).isEqualTo(res);
  }

  @Test
  public void refresh_OK() throws Exception {

    doReturn(true).when(accountService).refresh(reqCaptor.capture(), resCaptor.capture());

    // test method
    var result = api.refresh(req, res);

    // assert
    if (result instanceof ResultDto resultDto) {
      assertThat(resultDto.result).isEqualTo(ApiResult.OK);
    } else {
      fail();
    }
    assertThat(reqCaptor.getValue()).isEqualTo(req);
    assertThat(resCaptor.getValue()).isEqualTo(res);
  }

  @Test
  public void logout() throws Exception {

    doNothing().when(accountService).logout(reqCaptor.capture(), resCaptor.capture());

    // test method
    var result = api.logout(req, res);

    // assert
    if (result instanceof ResultDto resultDto) {
      assertThat(resultDto.result).isEqualTo(ApiResult.OK);
    } else {
      fail();
    }
    assertThat(reqCaptor.getValue()).isEqualTo(req);
    assertThat(resCaptor.getValue()).isEqualTo(res);
  }
}
