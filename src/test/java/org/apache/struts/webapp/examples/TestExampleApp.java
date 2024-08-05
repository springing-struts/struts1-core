package org.apache.struts.webapp.examples;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import springing.struts1.controller.StrutsActionController;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StrutsActionController.class)
public class TestExampleApp {

  @Autowired
  private MockMvc mvc;

  @Test
  void testItForwardsToWelcomePage() throws Exception {
    mvc
      .perform(MockMvcRequestBuilders.get("/welcome.do").accept("text/html"))
      .andDo(print())
      .andExpect(
        status().isOk()
      )
      .andExpect(
        forwardedUrl("/welcome.jsp")
      );
  }

  @Test
  void testItDispatchesToActionControllerByDefault() throws Exception {
    mvc
      .perform(MockMvcRequestBuilders.get("/welcome").accept("text/html"))
      .andDo(print())
      .andExpect(
        status().isOk()
      )
      .andExpect(
        forwardedUrl("/welcome.jsp")
      );
  }
}
