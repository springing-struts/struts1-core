package springing.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static springing.util.StringUtils.*;

public class StringUtilsTest {

  @Test
  void testItCanNormalizeRequestPath() {
    assertEquals("", normalizeForwardPath(null));
    assertEquals("", normalizeForwardPath(""));
    assertEquals("", normalizeForwardPath("/"));
    assertEquals("/module", normalizeForwardPath("module"));
    assertEquals("/module", normalizeForwardPath("/module"));
    assertEquals("/module", normalizeForwardPath("/module/"));
  }

  @Test
  void testItCanRetrieveExtensionPartOfForwardPath() {
    assertEquals("do", getExtensionOf("/welcome.do"));
    assertEquals("jsp", getExtensionOf("/module.a/welcome.jsp?key=value"));
    assertEquals("", getExtensionOf("/welcome"));
    assertEquals("", getExtensionOf("/welcome?style=dark"));
  }

  @Test
  void testItCanCamelizeTheGivenTokens() {
    assertEquals("assertEquals", lowerCamelize("AssertEquals"));
    assertEquals("assertEquals", lowerCamelize("assert-equals"));
    assertEquals("testAssertEquals", lowerCamelize("TEST", "assertEquals"));
  }

  @Test
  void testItCanTokenizeGivenIdentifier() {
    assertLinesMatch(List.of("Assert", "Equals"), tokenizeIdentifier("AssertEquals"));
    assertLinesMatch(List.of("assert", "equals"), tokenizeIdentifier("assert-equals"));
    assertLinesMatch(List.of("test", "assert", "equals"), tokenizeIdentifier("test_assert  equals"));
  }
}
