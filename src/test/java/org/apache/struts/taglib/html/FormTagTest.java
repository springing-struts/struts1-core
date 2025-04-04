package org.apache.struts.taglib.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;

/**
 * <pre>
 * <action
 *   path="/html-link-submit"
 *   type="org.apache.struts.webapp.exercise.HtmlSettersAction"
 *   name="testbean"
 *   scope="session"
 *   validate="false">
 *   <forward name="input" path="/html-link.do"/>
 * </action>
 *
 * <html:form action="/html-link-submit"></html:form>
 *
 * </pre>
 */
@WebMvcTest
public class FormTagTest {

  @Autowired
  private TestApp app;

  @Test
  void testItRendersHtmlFormTagOnPage() throws Exception {
    app.createRequest(HttpMethod.GET, "/exercise/html-link");
    app.assertTagContent(
      "/html-link",
      FormTag.class,
      (tag, context) -> {
        tag.setAction("/html-link-submit");
        tag.setMethod("post");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<form"));
        assertThat(
          content,
          containsString("action=\"/exercise/html-link-submit\"")
        );
        assertThat(content, containsString("method=\"post\""));
      }
    );
  }

  @Test
  void testItSetsActionFormNameAssociatedWithAction() throws Exception {
    var request = app.createRequest(
      HttpMethod.GET,
      "/exercise/html-link-submit"
    );
    app.assertTagContent(
      "/html-link-submit",
      FormTag.class,
      (tag, context) -> {
        tag.setAction("/html-link-submit");
      },
      (content, processedBody) -> {
        var actionFormName = request.getAttribute(Constants.BEAN_KEY);
        assertEquals("testbean", actionFormName);
      }
    );
  }
}
