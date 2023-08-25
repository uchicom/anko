// (C) 2023 uchicom
package com.uchicom.memo.util;

import com.uchicom.memo.annotation.Form;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.validator.constraints.Length;

public class ValidateUtil {

  public static String createValidateHashCode(String json) {
    return Long.toHexString(json.hashCode()).toUpperCase(Locale.JAPAN);
  }

  public static String createJsonForAllRequest() {
    var classSet = ClassUtil.listClasses("com.uchicom.memo.dto.request");
    classSet.addAll(ClassUtil.listClasses("com.uchicom.memo.dto.request.account"));
    classSet.addAll(ClassUtil.listClasses("com.uchicom.memo.dto.request.memo"));
    return classSet.stream()
        .map(ValidateUtil::createJson)
        .filter(Objects::nonNull)
        .collect(Collectors.joining(",", "{", "}"));
  }

  static String createJson(Class<Object> clazz) {
    var form = clazz.getAnnotation(Form.class);
    if (form == null) {
      return null;
    }
    return "\"" + form.value() + "\":" + createJson(clazz.getFields());
  }

  static String createJson(Field[] fields) {
    return Stream.of(fields)
        .map(ValidateUtil::createJson)
        .filter(Objects::nonNull)
        .collect(Collectors.joining(",", "{", "}"));
  }

  static String createJson(Field field) {
    // required
    var requiredFlg = false;
    String requiredMessage = null;
    var patternFlg = false;
    String regexp = null;
    String patternMessage = null;
    var maxLengthFlg = false;
    Integer maxLength = null;
    var notNull = field.getAnnotation(NotNull.class);
    var notEmpty = field.getAnnotation(NotEmpty.class);
    // pattern
    var notBlank = field.getAnnotation(NotBlank.class);
    // var email = field.getAnnotation(Email.class);
    var pattern = field.getAnnotation(Pattern.class);
    var length = field.getAnnotation(Length.class);
    var assertTrue = field.getAnnotation(AssertTrue.class);
    if (Boolean.class == field.getType()) {
      if (assertTrue != null) {
        requiredFlg = true;
        requiredMessage = assertTrue.message();
      }
    } else {
      if (notNull != null) {
        requiredFlg = true;
        requiredMessage = notNull.message();
      }
      if (notEmpty != null) {
        requiredFlg = true;
        requiredMessage = notEmpty.message();
      }
      if (notBlank != null) {
        requiredFlg = true;
        patternFlg = true;
        requiredMessage = notBlank.message();
        regexp = ".*\\\\S+.*";
        patternMessage = notBlank.message();
      }
      if (pattern != null) {
        patternFlg = true;
        regexp = pattern.regexp();
        patternMessage = pattern.message();
      }
      if (length != null) {
        maxLengthFlg = true;
        maxLength = length.max();
      }
      if (!requiredFlg && !patternFlg && !maxLengthFlg) {
        return null;
      }
    }
    var builder = new StringBuilder();
    builder.append("\"");
    builder.append(field.getName());
    builder.append("\":{");
    if (requiredFlg) {
      builder.append("\"required\":{\"message\":\"" + requiredMessage + "\"}");
    }
    if (patternFlg) {
      if (requiredFlg) {
        builder.append(",");
      }
      builder.append(
          "\"pattern\":{\"regexp\":\"" + regexp + "\",\"message\":\"" + patternMessage + "\"}");
    }
    if (maxLengthFlg) {
      if (requiredFlg || patternFlg) {
        builder.append(",");
      }
      builder.append("\"length\":{\"max\":\"" + maxLength + "\"}");
    }
    builder.append("}");
    return builder.toString();
  }
}
