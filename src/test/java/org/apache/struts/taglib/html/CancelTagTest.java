package org.apache.struts.taglib.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;

import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest
public class CancelTagTest {

  @Autowired
  private TestApp app;

  /**
   * <html:cancel>Cancel</html:cancel>
   */
  @Test
  void testItRendersInputElementOfTypeSubmit() throws Exception {
    var request = app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      CancelTag.class,
      (tag, context) -> {},
      (content, processedBody) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"submit\""));
        assertThat(
          content,
          containsString("name=\"org.apache.struts.taglib.html.CANCEL\"")
        );
        assertThat(content, containsString("value=\"Cancel\""));
      }
    );
  }

  /**
   * <html:cancel value="Reject!!">Cancel</html:cancel>
   */
  @Test
  void testItCanSendAnotherValue() throws Exception {
    app.createRequest(GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      CancelTag.class,
      (tag, context) -> {
        tag.setValue("Reject!!");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"submit\""));
        assertThat(content, containsString("value=\"Reject!!\""));
      }
    );
  }
}
