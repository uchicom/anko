// (C) 2023 uchicom
package com.uchicom.memo.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.uchicom.memo.AbstractTest;
import com.uchicom.memo.dto.request.memo.MemoRegisterDto;
import com.uchicom.memo.dto.request.memo.MemoUpdateDto;
import com.uchicom.memo.dto.response.ListDto;
import com.uchicom.memo.dto.response.MessageDto;
import com.uchicom.memo.entity.Memo;
import com.uchicom.memo.service.AccountService;
import com.uchicom.memo.service.MemoService;
import java.util.List;
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
public class MemoApiTest extends AbstractTest {
  @Mock Request req;
  @Mock Response res;

  @Mock AccountService accountService;
  @Mock MemoService memoService;

  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<MemoRegisterDto> memoRegisterDtoCaptor;
  @Captor ArgumentCaptor<List<MemoUpdateDto>> memoUpdateDtoListCaptor;

  @Spy @InjectMocks MemoApi api;

  @BeforeEach
  public void setUp() {
    createApiMock(api);
  }

  @Test
  public void list() throws Exception {
    // mock
    doReturn(1L).when(accountService).getAccountId(req);
    var list = List.of(new Memo());
    doReturn(list).when(memoService).getList(accountIdCaptor.capture());

    // test
    var result = api.list(req, res);

    // assert
    if (result instanceof ListDto<?> listDto) {
      assertThat(listDto.list).isEqualTo(list);
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
  }

  @Test
  public void register() throws Exception {
    // mock
    doReturn(1L).when(accountService).getAccountId(req);
    var dto = new MemoRegisterDto();
    doNothing()
        .when(memoService)
        .register(accountIdCaptor.capture(), memoRegisterDtoCaptor.capture());

    // test
    var result = api.register(dto, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("メモを登録しました。");
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(memoRegisterDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void update() throws Exception {
    // mock
    doReturn(1L).when(accountService).getAccountId(req);
    doNothing()
        .when(memoService)
        .update(accountIdCaptor.capture(), memoUpdateDtoListCaptor.capture());

    var list = List.of(new MemoUpdateDto());
    // test
    var result = api.update(list, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("メモを更新しました。");
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(memoUpdateDtoListCaptor.getValue()).isEqualTo(list);
  }
}
