package org.apache.struts.taglib.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;

import org.apache.struts.Globals;
import org.apache.struts.TestApp;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest
public class NotPresentTagTest {

  @Autowired
  private TestApp app;

  /**
   * <pre>
   * <logic:notPresent name="<%= Globals.MESSAGES_KEY %>" >
   *   CONTENT
   * </logic:notPresent>
   * </pre>
   */
  @Test
  void itEvaluatesItContentOnlyWhenGivenVariableNameEvaluatedNonNullValue()
    throws Exception {
    var propName = Globals.MESSAGE_KEY;
    var messages = new ActionMessages();

    messages.add("title", new ActionMessage("index.title"));
    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
      "/welcome",
      NotPresentTag.class,
      (tag, context) -> {
        tag.setName(propName);
      },
      (content, processedBody) -> {
        assertTrue(processedBody);
      }
    );
    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
      "/welcome",
      NotPresentTag.class,
      (tag, context) -> {
        context.getRequest().setAttribute(propName, messages);
        tag.setName(propName);
      },
      (content, processedBody) -> {
        assertFalse(processedBody);
      }
    );
  }
}
