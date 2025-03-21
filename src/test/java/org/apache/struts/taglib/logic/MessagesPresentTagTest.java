package org.apache.struts.taglib.logic;

import jakarta.servlet.jsp.PageContext;
import org.apache.struts.Globals;
import org.apache.struts.TestApp;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class MessagesPresentTagTest {

  @Autowired
  private TestApp app;

  /**
   * <logic:messagesPresent>ERROR!!!</logic:messagesPresent>
   */
  @Test
  void itRendersItsContentIfThereIsErrorMessageInAnyScope() throws Exception {
    var errorMessages = new ActionErrors();
    errorMessages.add("lastName", new ActionMessage("error.required"));

    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
      "/welcome",
      MessagesPresentTag.class,
      (tag, context) -> {
        context.getRequest().setAttribute(Globals.ERROR_KEY, errorMessages);
      },
      (content, processedBody) -> {
        assertTrue(processedBody);
      }
    );

    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
      "/welcome",
      MessagesPresentTag.class,
      (tag, context) -> {
      },
      (content, processedBody) -> {
        assertFalse(processedBody);
      }
    );
  }

  /**
   * <logic:messagesPresent count="errorCount">ERROR!!!</logic:messagesPresent>
   */
  @Test
  void itCanPopulateTheNumberOfMessagesToPageScope() throws Exception {
    var errorMessages = new ActionErrors();
    errorMessages.add("lastName", new ActionMessage("error.required"));
    errorMessages.add("firstName", new ActionMessage("error.required"));
    var pageContext = new ArrayList<PageContext>();

    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
      "/welcome",
      MessagesPresentTag.class,
      (tag, context) -> {
        pageContext.add(context);
        context.getRequest().setAttribute(Globals.ERROR_KEY, errorMessages);
        tag.setCount("errorCount");
      },
      (content, processedBody) -> {
        assertTrue(processedBody);
        var count = pageContext.getFirst().getAttribute("errorCount");
        assertEquals(2, count);
      }
    );

  }
}
