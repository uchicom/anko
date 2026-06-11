// (C) 2026 uchicom
package com.uchicom.pj.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * {@link GraphAssembler}のテストケース.
 *
 * @author uchicom
 */
public class GraphAssemblerTest {

  static class Row {
    long customerId;
    String customerName;
    long orderId;
    String orderName;

    Row(long customerId, String customerName, long orderId, String orderName) {
      this.customerId = customerId;
      this.customerName = customerName;
      this.orderId = orderId;
      this.orderName = orderName;
    }
  }

  static class Order {
    long id;
    String name;

    Order(long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  static class Customer {
    long id;
    String name;
    List<Order> orders = new ArrayList<>();

    Customer(long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  static class CustomerAssembler extends GraphAssembler<Row, Long, Customer> {

    @Override
    protected Long key(Row row) {
      return row.customerId;
    }

    @Override
    protected Customer create(Row row) {
      var customer = new Customer(row.customerId, row.customerName);
      customer.orders.add(new Order(row.orderId, row.orderName));
      return customer;
    }

    @Override
    protected void merge(Customer dto, Row row) {
      dto.orders.add(new Order(row.orderId, row.orderName));
    }
  }

  final CustomerAssembler assembler = new CustomerAssembler();

  /** {@link GraphAssembler#assemble(List)}のテスト：空リストの場合. */
  @Test
  public void assemble_empty() {
    var result = assembler.assemble(List.of());
    assertThat(result).isEmpty();
  }

  /** {@link GraphAssembler#assemble(List)}のテスト：1件のcustomerに複数orderがある場合. */
  @Test
  public void assemble_singleCustomerMultipleOrders() {
    var rows = List.of(new Row(1L, "Alice", 101L, "Book"), new Row(1L, "Alice", 102L, "Pen"));

    var result = assembler.assemble(rows);

    assertThat(result).hasSize(1);
    var customer = result.get(0);
    assertThat(customer.id).isEqualTo(1L);
    assertThat(customer.name).isEqualTo("Alice");
    assertThat(customer.orders).hasSize(2);
    assertThat(customer.orders.get(0).id).isEqualTo(101L);
    assertThat(customer.orders.get(1).id).isEqualTo(102L);
  }

  /** {@link GraphAssembler#assemble(List)}のテスト：複数customerの場合. */
  @Test
  public void assemble_multipleCustomers() {
    var rows =
        List.of(
            new Row(1L, "Alice", 101L, "Book"),
            new Row(2L, "Bob", 201L, "Pen"),
            new Row(1L, "Alice", 102L, "Eraser"));

    var result = assembler.assemble(rows);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).id).isEqualTo(1L);
    assertThat(result.get(0).orders).hasSize(2);
    assertThat(result.get(1).id).isEqualTo(2L);
    assertThat(result.get(1).orders).hasSize(1);
  }

  /** {@link GraphAssembler#assemble(List)}のテスト：各customerに1件のorderがある場合. */
  @Test
  public void assemble_oneOrderEach() {
    var rows = List.of(new Row(1L, "Alice", 101L, "Book"), new Row(2L, "Bob", 201L, "Pen"));

    var result = assembler.assemble(rows);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).name).isEqualTo("Alice");
    assertThat(result.get(1).name).isEqualTo("Bob");
  }
}
