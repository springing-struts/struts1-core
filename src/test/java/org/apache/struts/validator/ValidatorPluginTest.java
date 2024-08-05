package org.apache.struts.validator;

import org.apache.struts.TestApp;
import org.apache.struts.util.ModuleUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;

/**
 *   <plug-in
 *     className="org.apache.struts.validator.ValidatorPlugIn">
 *     <set-property
 *       property="pathnames"
 *       value="/org/apache/struts/validator/validator-rules-compressed.xml,
 *         /WEB-INF/validator/validation.xml,
 *         /WEB-INF/validator/validation-bundles.xml,
 *         /WEB-INF/validator/validation-i18nVariables.xml,
 *         /WEB-INF/validator/validation-type.xml,
 *         /WEB-INF/validator/validation-validwhen.xml"
 *     />
 *     <set-property
 *       property="stopOnFirstError"
 *       value="true"
 *     />
 *   </plug-in>
 */
@WebMvcTest
public class ValidatorPluginTest {

  @Autowired
  private TestApp app;

  @Test
  void testItCanBeLoadedFromStrutsConfig() {
    var request = app.createRequest(GET, "/validator/registration");
    var module = ModuleUtils.getCurrent();
    var validatorPlugIn = module.getPlugInByType(ValidatorPlugIn.class);
    assertNotNull(validatorPlugIn);
    var validatorResources = module.getValidatorResources();
    var form = validatorResources.getForm(request.getLocale(), "registrationForm");
    assertNotNull(form);
    assertEquals("registrationForm", form.getName());
  }
}
