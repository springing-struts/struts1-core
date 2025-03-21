package org.apache.struts.webapp.exercise;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

@WebMvcTest
public class TestExerciseApp {

  @Autowired
  private MockMvc mvc;

  @Test
  void testSubmitLinkApi() throws Exception {
    mvc
     .perform(get("/exercise/html-link-submit.do"))
     .andDo(print())
     .andExpect(forwardedUrl("/exercise/html-link.do"));
  }
}
