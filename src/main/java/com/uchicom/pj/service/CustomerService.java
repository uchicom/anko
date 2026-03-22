// (C) 2025 uchicom
package com.uchicom.pj.service;

import com.uchicom.pj.dao.CustomerDao;
import com.uchicom.pj.dto.request.customer.CustomerRegisterDto;
import com.uchicom.pj.dto.request.customer.CustomerUpdateDto;
import com.uchicom.pj.entity.Customer;
import java.util.List;

public class CustomerService {
  private final CustomerDao customerDao;

  public CustomerService(CustomerDao customerDao) {
    this.customerDao = customerDao;
  }

  public List<Customer> getList() {
    return customerDao.findAll();
  }

  public void register(CustomerRegisterDto dto) {
    var customer = new Customer();
    customer.company_name = dto.companyName;
    customer.pic_name = dto.picName;
    customer.email_address = dto.emailAddress;
    customer.telephon_number = dto.telephonNumber;
    customer.fax_number = dto.faxNumber;
    customer.address = dto.address;
    customer.building = dto.building;
    customerDao.insert(customer);
  }

  public void update(List<CustomerUpdateDto> dtoList) {
    for (var dto : dtoList) {
      var customer = customerDao.findById(dto.customerId);
      if (customer == null) continue;
      customer.company_name = dto.companyName;
      customer.pic_name = dto.picName;
      customer.email_address = dto.emailAddress;
      customer.telephon_number = dto.telephonNumber;
      customer.fax_number = dto.faxNumber;
      customer.address = dto.address;
      customer.building = dto.building;
      customerDao.update(customer);
    }
  }
}
