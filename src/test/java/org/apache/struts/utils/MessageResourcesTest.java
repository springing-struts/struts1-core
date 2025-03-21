package org.apache.struts.utils;

import org.apache.struts.TestApp;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class MessageResourcesTest {

  @Autowired
  private TestApp app;

  @Test
  void testLoadMessageResources() {
    app.createRequest(GET, "/validator/validateI18nExample");
    var messageResources = ModuleUtils.getCurrent().getMessageResources();
    var message = messageResources.getMessage("errors.required", "Password");
    assertEquals("Password is required.", message);
    var localizedMessage = messageResources.getMessage(
      Locale.JAPANESE, "errors.required", "パスワード"
    );
    assertEquals("パスワード を入力してください。", localizedMessage);
  }
}
