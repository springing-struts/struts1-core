package springing.struts1.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebXmlTest {

  @Test
  void testItParsesWebXmlConfigFile() {
    var webXml = WebXml.loadFrom("/WEB-INF/web.xml");
    assertNotNull(webXml);
  }

  @Test
  void testItLoadsStrutsConfigFilesLocatedAtPathWrittenInInitParams() {
    var webXml = WebXml.loadFrom("/WEB-INF/web.xml");
    var strutsConfigs = webXml.getActionServlet().getStrutsModules();
    assertEquals(7, strutsConfigs.size());
    var configForValidatorModule = strutsConfigs.stream().filter(it -> it.getPrefix().equals("/validator")).findAny().get();
    assertEquals("/validator", configForValidatorModule.getPrefix());
    assertEquals(
      "/WEB-INF/validator/struts-config.xml, /WEB-INF/validator/struts-config-bundles.xml," +
        " /WEB-INF/validator/struts-config-i18nVariables.xml, /WEB-INF/validator/struts-config-type.xml," +
        " /WEB-INF/validator/struts-config-validwhen.xml",
      configForValidatorModule.getConfigFilePaths()
    );
  }
}
