// (C) 2025 uchicom
package com.uchicom.tracker.factory.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.uchicom.tracker.service.AccountService;
import com.uchicom.tracker.service.AuthService;
import com.uchicom.tracker.service.CookieService;
import com.uchicom.tracker.service.DateTimeService;
import com.uchicom.tracker.service.IssueService;
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

  static IssueService issueService() {
    return new IssueService(DaoFactory.issueDao());
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
