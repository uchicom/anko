// (C) 2025 uchicom
package com.uchicom.pj.api;

import com.uchicom.pj.annotation.Auth;
import com.uchicom.pj.annotation.Path;
import com.uchicom.pj.dto.request.customer.CustomerRegisterDto;
import com.uchicom.pj.dto.request.customer.CustomerUpdateDto;
import com.uchicom.pj.dto.response.ListDto;
import com.uchicom.pj.dto.response.MessageDto;
import com.uchicom.pj.entity.Customer;
import com.uchicom.pj.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Path("/customer")
public class CustomerApi extends AbstractApi {

  private final CustomerService customerService;

  public CustomerApi(CustomerService customerService) {
    this.customerService = customerService;
  }

  @Auth
  @Path("/list")
  public Object list(HttpServletRequest req, HttpServletResponse res) {
    return refer(
        () -> {
          return new ListDto<Customer>(customerService.getList());
        });
  }

  @Auth
  @Path("/register")
  public Object register(CustomerRegisterDto dto, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          customerService.register(dto);
          return new MessageDto("顧客を登録しました。");
        });
  }

  @Auth
  @Path("/update")
  public Object update(
      List<CustomerUpdateDto> dtoList, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          customerService.update(dtoList);
          return new MessageDto("顧客を更新しました。");
        });
  }
}
