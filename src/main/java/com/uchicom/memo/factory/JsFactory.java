// (C) 2024 uchicom
package com.uchicom.memo.factory;

import com.uchicom.memo.util.ValidateUtil;

public class JsFactory {

  public JsFactory() {}

  public String createValidationPage() {
    var builder = new StringBuilder(1024);
    builder.append(
        """
        const validationHash='$hash';
        if (validationHash != localStorage.getItem('validationHash')) {
          localStorage.setItem('validationHash', validationHash);
          localStorage.setItem('validation', JSON.stringify($validation));
        }
        """);
    var json = ValidateUtil.createJsonForAllRequest();
    return builder
        .toString()
        .replace("$hash", ValidateUtil.createValidateHashCode(json))
        .replace("$validation", json);
  }
}
