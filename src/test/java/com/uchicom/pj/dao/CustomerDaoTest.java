// (C) 2025 uchicom
package com.uchicom.pj.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.pj.entity.Customer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link CustomerDao}のテストケース.
 *
 * @author uchicom
 */
@Tag("dao")
public class CustomerDaoTest extends AbstractDaoTest<Customer, CustomerDao> {

  public CustomerDaoTest() {
    super(Customer::new, helper -> new CustomerDao(helper));
  }

  /** {@link CustomerDao#findAll()}のテスト. */
  @Test
  public void findAll() throws Exception {
    test(
        () -> {
          dao.findAll();
          assertThat(getSQL()).isEqualTo("SELECT * FROM pj.customer ORDER BY id");
        });
  }
}
