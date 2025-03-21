package org.apache.struts.utils;

import org.apache.struts.util.ResponseUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResponseUtilsTest {
  @Test
  void testItCanEscapeHtmlSpecialCharacterFromText() {
    assertEquals(
      "&lt;javascript src=&quot;../../src/test.js&quot;&gt;",
      ResponseUtils.filter("<javascript src=\"../../src/test.js\">")
    );
    assertEquals(
      "", ResponseUtils.filter("")
    );
    assertNull(
      ResponseUtils.filter(null)
    );
  }
}
