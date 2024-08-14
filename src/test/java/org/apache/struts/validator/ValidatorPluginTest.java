package org.apache.struts.validator;

import org.apache.struts.TestApp;
import org.apache.struts.util.ModuleUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;

/**
 * ## struts-config.xml
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
 * ## validation.xml
 *   </plug-in>
 *       <form name="registrationForm">
 *       <field property="firstName" depends="required,mask,minlength">
 *         <arg key="registrationForm.firstname.displayname" position="0"/>
 *         <arg name="minlength" key="${var:minlength}" resource="false" position="1"/>
 *         <var>
 *           <var-name>mask</var-name>
 *           <var-value>^\w+$</var-value>
 *         </var>
 *         <var>
 *           <var-name>minlength</var-name>
 *           <var-value>5</var-value>
 *         </var>
 *       </field>
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
    var firstNameField = form.getFieldByName("firstName");
    assertEquals("firstName", firstNameField.getProperty());
    assertThat(firstNameField.getDependencyList(), hasItems("required", "mask", "minlength"));
    assertThat(
      firstNameField.getArgs("minlength").stream().map(it -> it.getText()).toList(),
      hasItems("First Name", "5")
    );
  }
}
