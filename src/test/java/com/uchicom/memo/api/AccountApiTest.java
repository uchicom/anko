// (C) 2023 uchicom
package com.uchicom.memo.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;

import com.uchicom.memo.AbstractTest;
import com.uchicom.memo.dto.request.account.AccountRegisterDto;
import com.uchicom.memo.dto.request.account.LoginDto;
import com.uchicom.memo.dto.response.ErrorDto;
import com.uchicom.memo.dto.response.MessageDto;
import com.uchicom.memo.dto.response.account.TokenDto;
import com.uchicom.memo.entity.Account;
import com.uchicom.memo.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import spark.Request;
import spark.Response;

@Tag("api")
public class AccountApiTest extends AbstractTest {
  @Mock Request req;
  @Mock Response res;

  @Mock AccountService accountService;

  @Captor ArgumentCaptor<AccountRegisterDto> accountRegisterDtoCaptor;
  @Captor ArgumentCaptor<LoginDto> loginDtoCaptor;

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
    doReturn(null).when(accountService).login(loginDtoCaptor.capture());

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
    doReturn("test").when(accountService).login(loginDtoCaptor.capture());

    var dto = new LoginDto();

    // test
    var result = api.login(dto, req, res);

    // assert
    if (result instanceof TokenDto tokenDto) {
      assertThat(tokenDto.token).isEqualTo("test");
    } else {
      fail();
    }
    assertThat(loginDtoCaptor.getValue()).isEqualTo(dto);
  }
}
