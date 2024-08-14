package org.apache.struts.taglib.html;

import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class SubmitTagTest {

  @Autowired
  private TestApp app;

  /**
   * <html:submit property="submit">
   * </html:submit>
   */
  @Test
  void itRendersAInputTagWithTypeSubmit() throws Exception {
    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
      "/welcome",
      SubmitTag.class,
      (tag, context) -> {
        tag.setProperty("submit");
      },
      (content, bodyProcessed) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"submit\""));
        assertThat(content, containsString("name=\"submit\""));
        assertThat(content, containsString("value=\"Submit\""));
      }
    );

    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
        "/welcome",
        SubmitTag.class,
        (tag, context) -> {
        },
        (content, bodyProcessed) -> {
          assertThat(content, containsString("<input"));
          assertThat(content, containsString("type=\"submit\""));
          assertThat(content, not(containsString("name=")));
        }
    );
  }
}
