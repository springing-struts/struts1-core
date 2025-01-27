package org.apache.struts.taglib.html;

import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class ImgTagTest {

  @Autowired
  private TestApp app;

  /**
   * <pre>
   * <html:img
   *   page="/struts-power.gif"
   *   altKey="index.power"
   * />
   *
   * [MessageResources.properties]
   * index.power=Powered by Struts
   * </pre>
   */
  @Test
  void testItsAltTextCanBeSetThroughMessageResources() throws Exception {
    app.createRequest(GET, "/validator/welcome");
    app.assertTagContent(
      "/welcome",
      ImgTag.class,
      (tag, context) -> {
        tag.setPage("/struts-power.gif");
        tag.setAltKey("index.power");
      },
      (content, processedBody) -> {
        assertThat(content, containsString("<img"));
        assertThat(content, containsString("src=\"/validator/struts-power.gif\""));
        assertThat(content, containsString("alt=\"Powered by Struts\""));
      }
    );
  }
}
