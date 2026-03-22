// (C) 2025 uchicom
package com.uchicom.pj.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dto.request.customer.CustomerRegisterDto;
import com.uchicom.pj.dto.request.customer.CustomerUpdateDto;
import com.uchicom.pj.dto.response.ListDto;
import com.uchicom.pj.dto.response.MessageDto;
import com.uchicom.pj.entity.Customer;
import com.uchicom.pj.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("api")
public class CustomerApiTest extends AbstractTest {
  @Mock HttpServletRequest req;
  @Mock HttpServletResponse res;

  @Mock CustomerService customerService;

  @Captor ArgumentCaptor<CustomerRegisterDto> customerRegisterDtoCaptor;
  @Captor ArgumentCaptor<List<CustomerUpdateDto>> customerUpdateDtoListCaptor;

  @Spy @InjectMocks CustomerApi api;

  @BeforeEach
  public void setUp() {
    createApiMock(api);
  }

  @Test
  public void list() throws Exception {
    // mock
    var list = List.of(new Customer());
    doReturn(list).when(customerService).getList();

    // test
    var result = api.list(req, res);

    // assert
    if (result instanceof ListDto<?> listDto) {
      assertThat(listDto.list).isEqualTo(list);
    } else {
      fail();
    }
  }

  @Test
  public void register() throws Exception {
    // mock
    var dto = new CustomerRegisterDto();
    doNothing().when(customerService).register(customerRegisterDtoCaptor.capture());

    // test
    var result = api.register(dto, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("顧客を登録しました。");
    } else {
      fail();
    }
    assertThat(customerRegisterDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void update() throws Exception {
    // mock
    doNothing().when(customerService).update(customerUpdateDtoListCaptor.capture());

    var list = List.of(new CustomerUpdateDto());
    // test
    var result = api.update(list, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("顧客を更新しました。");
    } else {
      fail();
    }
    assertThat(customerUpdateDtoListCaptor.getValue()).isEqualTo(list);
  }
}
