package org.apache.struts.utils;

import org.apache.struts.util.MessageResources;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest
public class MessageResourcesTest {

  @Test
  void testLoadMessageResources() {
    var messageResources = MessageResources.getMessageResources(
      "org.apache.struts.webapp.validator.MessageResources"
    );
    var message = messageResources.getMessage("errors.required", "Password");
    assertEquals("Password is required.", message);
    var localizedMessage = messageResources.getMessage(
      Locale.JAPANESE, "errors.required", "パスワード"
    );
    assertEquals("パスワード を入力してください。", localizedMessage);
  }
}
