package org.apache.struts.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.apache.struts.action.ActionServlet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest
public class ModuleConfigTest {

  @Autowired
  private ActionServlet actionServlet;

  ModuleConfig getRootModule() {
    return actionServlet
      .getStrutsModules()
      .stream()
      .filter(it -> it.isDefaultModule())
      .findAny()
      .get();
  }

  @Test
  void testItAllowsToAccessModuleInformation() {
    var rootModule = getRootModule();
    assertNotNull(rootModule);
    assertEquals("", rootModule.getPrefix());
    assertEquals(
      "org.apache.struts.webapp.examples.CustomFormBean",
      rootModule.getActionFormBeanClass()
    );
  }
}
