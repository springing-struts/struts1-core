package org.apache.struts.taglib.html;

import org.apache.struts.TestApp;
import org.apache.struts.webapp.exercise.TestBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class HiddenTagTest {

  @Autowired
  private TestApp app;

  /**
   * <html:hidden property="action" />
   */
  @Test
  void itRendersAnInputTagWithHiddenType() throws Exception {
    app.createRequest(GET, "/exercise/welcome");
    app.setupActionForm("testbean", TestBean::new);
    app.assertTagContent(
      "/welcome",
      HiddenTag.class,
      (tag, context) -> {
        tag.setProperty("action");
      },
      (content, bodyProcessed) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"hidden\""));
        assertThat(content, containsString("name=\"action\""));
        assertThat(content, containsString("value=\"\""));
      }
    );
  }
}
