package org.apache.struts.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest
public class ModuleConfigTest {

  @Autowired
  private List<ModuleConfig> moduleConfigs;

  ModuleConfig getRootModule() {
    return moduleConfigs.stream().filter(
      it -> it.getPrefix().equals("/")
    ).findAny().get();
  }

  @Test
  void testItAllowsToAccessModuleInformation() {
    var rootModule = getRootModule();
    assertNotNull(rootModule);
    assertEquals("/", rootModule.getPrefix());
    assertEquals(
      "org.apache.struts.webapp.examples.CustomFormBean",
      rootModule.getActionFormBeanClass()
    );
  }
}
