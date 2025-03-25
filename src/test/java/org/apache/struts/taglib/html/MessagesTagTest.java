package org.apache.struts.taglib.html;

import static org.springframework.http.HttpMethod.GET;

import org.apache.struts.Globals;
import org.apache.struts.TestApp;
import org.apache.struts.action.ActionErrors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest
public class MessagesTagTest {

  @Autowired
  private TestApp app;

  /**
   * <pre>
   * <html:messages id="error">
   *   <li><bean:write name="error"/></li>
   * </html:messages>
   * </pre>
   */
  @Test
  void itRendersItsContentForEachErrorMessageInThePage() throws Exception {
    var errors = new ActionErrors();
    app.createRequest(GET, "/exercise/welcome");
    app.assertTagContent(
      "/welcome",
      MessagesTag.class,
      (tag, context) -> {
        context.setAttribute(Globals.ERROR_KEY, errors);
        tag.setId("error");
      },
      (content, bodyProcessed) -> {}
    );
  }
}
