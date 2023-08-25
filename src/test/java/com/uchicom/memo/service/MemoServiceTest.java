// (C) 2023 uchicom
package com.uchicom.memo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.uchicom.memo.AbstractTest;
import com.uchicom.memo.dao.MemoDao;
import com.uchicom.memo.dto.request.memo.MemoRegisterDto;
import com.uchicom.memo.dto.request.memo.MemoUpdateDto;
import com.uchicom.memo.entity.Memo;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

/**
 * {@link MemoService}のテストケース.
 *
 * @author uchicom
 */
@Tag("service")
public class MemoServiceTest extends AbstractTest {

  @Mock MemoDao memoDao;

  @Captor ArgumentCaptor<String> loginIdCaptor;
  @Captor ArgumentCaptor<String> ipCaptor;
  @Captor ArgumentCaptor<Memo> memoCaptor;
  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<Long> memoIdCaptor;

  @Spy @InjectMocks MemoService service;

  @Test
  public void getList() {
    var list = List.of(new Memo());
    doReturn(list).when(memoDao).findByAccountId(accountIdCaptor.capture());

    // test
    var result = service.getList(1L);

    // assert
    assertThat(result).isEqualTo(list);
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
  }

  @Test
  public void register() {
    doReturn(true).when(memoDao).insert(memoCaptor.capture());
    var dto = new MemoRegisterDto();
    dto.title = "title";
    dto.body = "body";

    // test
    service.register(1L, dto);

    // assert
    var memo = memoCaptor.getValue();
    assertThat(memo.account_id).isEqualTo(1L);
    assertThat(memo.title).isEqualTo(dto.title);
    assertThat(memo.body).isEqualTo(dto.body);
  }

  @Test
  public void update() {
    var memo2 = new Memo();
    memo2.title = "title2";
    memo2.body = "body2";
    var memo3 = new Memo();
    memo3.title = "title3";
    memo3.body = "body3";
    doReturn(null)
        .doReturn(memo2)
        .doReturn(memo3)
        .when(memoDao)
        .findByIdAndAccountId(memoIdCaptor.capture(), accountIdCaptor.capture());
    doReturn(true).when(memoDao).update(memoCaptor.capture());

    var dto1 = new MemoUpdateDto();
    dto1.memoId = 1L;
    dto1.title = "title1";
    dto1.body = "body1";

    var dto2 = new MemoUpdateDto();
    dto2.memoId = 2L;
    dto2.title = "title2";
    dto2.body = "body2";

    var dto3 = new MemoUpdateDto();
    dto3.memoId = 3L;
    dto3.title = "title31";
    dto3.body = "body31";

    // test
    service.update(4L, List.of(dto1, dto2, dto3));

    // assert
    var memoIds = memoIdCaptor.getAllValues();
    assertThat(memoIds).hasSize(3);
    assertThat(memoIds.get(0)).isEqualTo(dto1.memoId);
    assertThat(memoIds.get(1)).isEqualTo(dto2.memoId);
    assertThat(memoIds.get(2)).isEqualTo(dto3.memoId);

    var accountIds = accountIdCaptor.getAllValues();
    assertThat(accountIds).hasSize(3);
    assertThat(accountIds.get(0)).isEqualTo(4L);
    assertThat(accountIds.get(1)).isEqualTo(4L);
    assertThat(accountIds.get(2)).isEqualTo(4L);

    assertThat(memoCaptor.getValue()).isEqualTo(memo3);
    assertThat(memo3.title).isEqualTo("title31");
    assertThat(memo3.body).isEqualTo("body31");
  }
}
