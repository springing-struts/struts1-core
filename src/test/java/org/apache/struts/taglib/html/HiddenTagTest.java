package org.apache.struts.taglib.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;

import org.apache.struts.TestApp;
import org.apache.struts.webapp.exercise.TestBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

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
    app.setupActionForm("testbean", () -> {
      var bean = new TestBean();
      bean.setStringProperty("stringValue");
      return bean;
    });
    app.assertTagContent(
      "/welcome",
      HiddenTag.class,
      (tag, context) -> {
        tag.setProperty("stringProperty");
      },
      (content, bodyProcessed) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"hidden\""));
        assertThat(content, containsString("name=\"stringProperty\""));
        assertThat(content, containsString("value=\"stringValue\""));
      }
    );
  }
}
