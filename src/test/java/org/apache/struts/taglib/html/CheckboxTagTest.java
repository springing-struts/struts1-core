package org.apache.struts.taglib.html;

import org.apache.struts.TestApp;
import org.apache.struts.webapp.exercise.TestBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

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
public class CheckboxTagTest {

  @Autowired
  private TestApp app;

  /**
   * <pre>
   *   <html:checkbox property="booleanProperty" />
   * </pre>
   */
  @Test
  void testItRendersFormBeansPropertyValueAsInputElementWithCheckboxType() throws Exception {
    app.createRequest(HttpMethod.POST, "/exercise/html-link-submit");
    app.setupActionForm("testbean", () -> {
      var testBean = new TestBean();
      testBean.setBooleanProperty(true);
      return testBean;
    });
    app.assertTagContent(
      "/html-link-submit",
      CheckboxTag.class,
      (tag, context) -> {
        tag.setProperty("booleanProperty");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"checkbox\""));
        assertThat(content, containsString("name=\"booleanProperty\""));
        assertThat(content, containsString("value=\"on\""));
        assertThat(content, containsString("checked"));
      }
    );
    app.createRequest(HttpMethod.POST, "/exercise/html-link-submit");
    app.setupActionForm("testbean", () -> {
      var testBean = new TestBean();
      testBean.setBooleanProperty(false);
      return testBean;
    });
    app.assertTagContent(
        "/html-link-submit",
        CheckboxTag.class,
      (tag, context) -> {
        tag.setProperty("booleanProperty");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<input"));
        assertThat(content, containsString("type=\"checkbox\""));
        assertThat(content, containsString("name=\"booleanProperty\""));
        assertThat(content, containsString("value=\"on\""));
        assertThat(content, not(containsString("checked")));
      }
    );
  }
}
