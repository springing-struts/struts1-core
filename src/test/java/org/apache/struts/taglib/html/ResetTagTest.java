package org.apache.struts.taglib.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;

@WebMvcTest
public class ResetTagTest {

  @Autowired
  private TestApp app;

  /**
   * <html:reset>Reset</html:reset>
   */
  @Test
  void testItRendersInputTagWithResetType() throws Exception {
    app.createRequest(HttpMethod.POST, "/exercise/html-link-submit");
    app.assertTagContent(
      "/html-link-submit",
      ResetTag.class,
      (tag, context) -> {},
      (content, evaluatedBody) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"reset\""));
        assertThat(content, containsString("value=\"Reset\""));
      }
    );
  }
}
