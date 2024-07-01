package springing.struts1.configuration;

import org.apache.struts.webapp.examples.CustomActionMapping;
import org.apache.struts.webapp.examples.CustomFormBean;
import org.junit.jupiter.api.Test;
import org.apache.struts.config.StrutsConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Struts1ConfigurationTest {

  @Test
  public void testParsingStrutsConfigFiles() {
    var config = StrutsConfig.parseConfigFileAt("/WEB-INF/struts-config.xml");
    assertNotNull(config);

    var actions = config.getActionConfigs();
    assertEquals(1, actions.size());
    var actionConfig = actions.getFirst();
    var actionMapping = actionConfig.createActionMapping();
    assertEquals(CustomActionMapping.class, actionMapping.getClass());
    var customMapping = (CustomActionMapping) actionMapping;
    assertEquals("/welcome", customMapping.getPath());
    assertEquals("/welcome.jsp", customMapping.getForwardPath());
    assertEquals("EXAMPLE", customMapping.getExample());

    var formBeanConfigs = config.getFormBeanConfigs();
    assertEquals(1, formBeanConfigs.size());
    var formBeanConfig = formBeanConfigs.getFirst();
    var formBean = formBeanConfig.createActionFormBean();
    assertEquals(CustomFormBean.class, formBean.getClass());
  }
}
