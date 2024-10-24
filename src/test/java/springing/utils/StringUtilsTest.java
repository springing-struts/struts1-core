package springing.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static springing.util.StringUtils.getExtensionOf;
import static springing.util.StringUtils.normalizeForwardPath;

public class StringUtilsTest {

  @Test
  void testItCanNormalizeRequestPath() {
    assertEquals("/", normalizeForwardPath(null));
    assertEquals("/", normalizeForwardPath(""));
    assertEquals("/", normalizeForwardPath("/"));
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
}
