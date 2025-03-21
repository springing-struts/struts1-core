package org.apache.struts.utils;

import jakarta.servlet.ServletContext;
import org.apache.struts.util.ModuleUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest
public class ModuleUtilsTest {

  @Autowired
  private ModuleUtils moduleUtils;

  @Autowired
  private ServletContext context;

  @Test
  void testItCanTellNameOfModuleCorrespondingToCurrentRequest() {
    var request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setServletPath("/exercise/html-link.html");
    assertEquals(
      "exercise",
      moduleUtils.getModuleName(
        HttpServletRequest.toJavaxNamespace(request),
        javax.servlet.ServletContext.toJavaxNamespace(context)
      )
    );
  }
}
