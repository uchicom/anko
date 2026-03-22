// (C) 2025 uchicom
package com.uchicom.pj.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dao.CustomerDao;
import com.uchicom.pj.dto.request.customer.CustomerRegisterDto;
import com.uchicom.pj.dto.request.customer.CustomerUpdateDto;
import com.uchicom.pj.entity.Customer;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

/**
 * {@link CustomerService}のテストケース.
 *
 * @author uchicom
 */
@Tag("service")
public class CustomerServiceTest extends AbstractTest {

  @Mock CustomerDao customerDao;

  @Captor ArgumentCaptor<Customer> customerCaptor;
  @Captor ArgumentCaptor<Long> customerIdCaptor;

  @Spy @InjectMocks CustomerService service;

  @Test
  public void getList() {
    var list = List.of(new Customer());
    doReturn(list).when(customerDao).findAll();

    // test
    var result = service.getList();

    // assert
    assertThat(result).isEqualTo(list);
  }

  @Test
  public void register() {
    doReturn(true).when(customerDao).insert(customerCaptor.capture());
    var dto = new CustomerRegisterDto();
    dto.companyName = "company";
    dto.picName = "pic";
    dto.emailAddress = "test@example.com";
    dto.telephonNumber = "09012345678";
    dto.faxNumber = "0312345678";
    dto.address = "address";
    dto.building = "building";

    // test
    service.register(dto);

    // assert
    var customer = customerCaptor.getValue();
    assertThat(customer.company_name).isEqualTo(dto.companyName);
    assertThat(customer.pic_name).isEqualTo(dto.picName);
    assertThat(customer.email_address).isEqualTo(dto.emailAddress);
    assertThat(customer.telephon_number).isEqualTo(dto.telephonNumber);
    assertThat(customer.fax_number).isEqualTo(dto.faxNumber);
    assertThat(customer.address).isEqualTo(dto.address);
    assertThat(customer.building).isEqualTo(dto.building);
  }

  @Test
  public void update() {
    var customer2 = new Customer();
    customer2.company_name = "company2";
    var customer3 = new Customer();
    customer3.company_name = "company3";
    doReturn(null)
        .doReturn(customer2)
        .doReturn(customer3)
        .when(customerDao)
        .findById(customerIdCaptor.capture());
    doReturn(true).when(customerDao).update(customerCaptor.capture());

    var dto1 = new CustomerUpdateDto();
    dto1.customerId = 1L;
    dto1.companyName = "company1";

    var dto2 = new CustomerUpdateDto();
    dto2.customerId = 2L;
    dto2.companyName = "company2";

    var dto3 = new CustomerUpdateDto();
    dto3.customerId = 3L;
    dto3.companyName = "company31";
    dto3.picName = "pic31";

    // test
    service.update(List.of(dto1, dto2, dto3));

    // assert
    var customerIds = customerIdCaptor.getAllValues();
    assertThat(customerIds).hasSize(3);
    assertThat(customerIds.get(0)).isEqualTo(dto1.customerId);
    assertThat(customerIds.get(1)).isEqualTo(dto2.customerId);
    assertThat(customerIds.get(2)).isEqualTo(dto3.customerId);

    assertThat(customerCaptor.getValue()).isEqualTo(customer3);
    assertThat(customer3.company_name).isEqualTo("company31");
    assertThat(customer3.pic_name).isEqualTo("pic31");
  }
}
