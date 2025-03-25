package org.apache.struts.taglib.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.apache.struts.TestApp;
import org.apache.struts.webapp.exercise.TestBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;

/**
 * <pre>
 *   <form-bean
 *     name="testbean"
 *     type="org.apache.struts.webapp.exercise.TestBean"
 *   />
 *   <action
 *     path="/html-link-submit"
 *     type="org.apache.struts.webapp.exercise.HtmlSettersAction"
 *     name="testbean"
 *     scope="session"
 *     validate="false">
 *     <forward name="input" path="/html-link.do"/>
 *   </action>
 * </pre>
 */
@WebMvcTest
public class TextTagTest {

  @Autowired
  private TestApp app;

  /**
   * <html:text property="stringProperty" size="16" />
   */
  @Test
  void testItRendersInputTagWithTextType() throws Exception {
    app.createRequest(HttpMethod.POST, "/exercise/html-link-submit");
    app.setupActionForm("testbean", () -> {
      var testBean = new TestBean();
      testBean.setStringProperty("STRUTS!!");
      return testBean;
    });
    app.assertTagContent(
      "/html-link-submit",
      TextTag.class,
      (tag, context) -> {
        tag.setProperty("stringProperty");
        tag.setSize("16");
      },
      (content, bodyProcessed) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"text\""));
        assertThat(content, containsString("size=\"16\""));
        assertThat(content, containsString("name=\"stringProperty\""));
        assertThat(content, containsString("value=\"STRUTS!!\""));
      }
    );
  }

  /**
   * <html:text property="stringProperty[1]" size="16" />
   */
  @Test
  void testItSupportsArrayTypedProperties() throws Exception {
    app.createRequest(HttpMethod.POST, "/exercise/html-link-submit");
    app.setupActionForm("testbean", () -> {
      var testBean = new TestBean();
      testBean.setStringArray(new String[] { "SPRING!!", "STRUTS!!" });
      return testBean;
    });
    app.assertTagContent(
      "/html-link-submit",
      TextTag.class,
      (tag, context) -> {
        tag.setProperty("stringArray[1]");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"text\""));
        assertThat(content, containsString("name=\"stringArray[1]\""));
        assertThat(content, containsString("value=\"STRUTS!!\""));
      }
    );
  }
}
