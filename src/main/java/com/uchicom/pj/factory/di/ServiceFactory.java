// (C) 2025 uchicom
package com.uchicom.pj.factory.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.uchicom.pj.service.AccountService;
import com.uchicom.pj.service.AuthService;
import com.uchicom.pj.service.CookieService;
import com.uchicom.pj.service.CustomerService;
import com.uchicom.pj.service.DateTimeService;
import com.uchicom.pj.service.ProjectService;
import com.uchicom.pj.service.TaskService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ServiceFactory {

  static AccountService accountService() {
    return new AccountService(authService(), DaoFactory.accountDao());
  }

  static AuthService authService() {
    return new AuthService(dateTimeService(), cookieService(), DIFactory.logger());
  }

  static CookieService cookieService() {
    return new CookieService();
  }

  static DateTimeService dateTimeService() {
    return new DateTimeService();
  }

  static CustomerService customerService() {
    return new CustomerService(DaoFactory.customerDao());
  }

  static ProjectService projectService() {
    return new ProjectService(DaoFactory.projectDao());
  }

  static TaskService taskService() {
    return new TaskService(DaoFactory.taskDao());
  }

  static ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
    javaTimeModule.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
    javaTimeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
    javaTimeModule.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
    javaTimeModule.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
    javaTimeModule.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
    mapper.registerModule(javaTimeModule);
    return mapper;
  }
}
