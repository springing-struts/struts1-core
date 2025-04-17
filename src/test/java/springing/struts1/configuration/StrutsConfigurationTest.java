package springing.struts1.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.webapp.examples.CustomActionMapping;
import org.apache.struts.webapp.examples.CustomFormBean;
import org.junit.jupiter.api.Test;

public class StrutsConfigurationTest {

  @Test
  void testItParsesSimpleStrutsConfigFile() {
    var config = ModuleConfigBean.loadFrom("/WEB-INF/struts-config.xml", "");
    assertNotNull(config);

    var actions = config.findActionConfigs();
    assertEquals(1, actions.length);
    var actionConfig = actions[0];
    var actionMapping = (ActionMapping) actionConfig;
    assertEquals(CustomActionMapping.class, actionMapping.getClass());
    var customMapping = (CustomActionMapping) actionMapping;
    assertEquals("/welcome", customMapping.getPath());
    assertEquals("/welcome.jsp", customMapping.getForwardPath());
    assertEquals("EXAMPLE", customMapping.getExample());

    var formBeanConfigs = config.getFormBeanConfigs();
    assertEquals(1, formBeanConfigs.size());
    var formBeanConfig = formBeanConfigs.getFirst();
    assertEquals(CustomFormBean.class, formBeanConfig.getClass());
    var customFormBeanConfig = (CustomFormBean) formBeanConfig;
    assertEquals("EXAMPLE", customFormBeanConfig.getExample());
  }

  @Test
  void testItParsesStrutsConfigContainingFormBeanDefinitions() {
    var config = ModuleConfigBean.loadFrom(
      "/WEB-INF/exercise/struts-config.xml",
      "exercise"
    );
    assertNotNull(config);
  }

  @Test
  void testItParsesStrutsConfigContainingUploadRelatedSettings() {
    var config = ModuleConfigBean.loadFrom(
      "/WEB-INF/upload/struts-config.xml",
      "upload"
    );
    assertNotNull(config);
  }

  @Test
  void testItParsesStrutsConfigContainingValidationSettings() {
    var baseConfig = ModuleConfigBean.loadFrom(
      "/WEB-INF/validator/struts-config.xml",
      "validator"
    );
    assertNotNull(baseConfig);

    var bundleConfig = ModuleConfigBean.loadFrom(
      "/WEB-INF/validator/struts-config-bundles.xml",
      "validator"
    );
    assertNotNull(bundleConfig);
  }
}
