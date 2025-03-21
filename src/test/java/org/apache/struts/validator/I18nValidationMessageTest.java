package org.apache.struts.validator;


import org.apache.struts.TestApp;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.ModuleUtils;
import org.eclipse.tags.shaded.org.apache.xpath.operations.Mod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import springing.struts1.configuration.ServletConfigBean;
import springing.struts1.validator.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;

/**
 * ## validation-i18n.xml
 * <pre>
 * <form name="i18nForm">
 *   <field property="name" depends="required,minlength,maxlength">
 *     <msg name="required"  key="msg.required"      bundle="i18nExample" />
 *     <msg name="minlength" key="msg.minlength"     bundle="i18nExample" />
 *     <msg name="maxlength" key="msg.maxlength"     bundle="i18nExample" />
 *     <arg                  key="label.name"        bundle="i18nExample" />
 *     <arg name="minlength" key="name.minlength"    bundle="i18nVariables" />
 *     <arg name="maxlength" key="name.maxlength"    bundle="i18nVariables" />
 *     <var resource="true" bundle="i18nVariables">
 *       <var-name>minlength</var-name>
 *       <var-value>name.minlength</var-value>
 *     </var>
 *     <var resource="true" bundle="i18nVariables">
 *       <var-name>maxlength</var-name>
 *       <var-value>name.maxlength</var-value>
 *     </var>
 *   </field>
 *   <field property="zip" depends="required,minlength,maxlength,validwhen">
 *     <msg name="required"  key="msg.required"      bundle="i18nExample" />
 *     <msg name="minlength" key="msg.zip.minlength" bundle="i18nExample" />
 *     <msg name="maxlength" key="msg.zip.maxlength" bundle="i18nExample" />
 *     <msg name="validwhen" key="msg.zip.validwhen" bundle="i18nExample" />
 *     <arg                  key="label.zip"         bundle="i18nExample" />
 *     <arg                  key="zip.minlength"     bundle="i18nVariables" />
 *     <arg name="validwhen" key="zip.maxlength"     bundle="i18nVariables" />
 *     <var resource="true" bundle="i18nVariables">
 *       <var-name>minlength</var-name>
 *       <var-value>zip.minlength</var-value>
 *     </var>
 *     <var resource="true" bundle="i18nVariables">
 *       <var-name>maxlength</var-name>
 *       <var-value>zip.maxlength</var-value>
 *     </var>
 *     <var resource="true" bundle="i18nVariables">
 *       <var-name>test</var-name>
 *       <var-value>zip.validwhen</var-value>
 *     </var>
 *   </field>
 * </pre>
 */
@WebMvcTest
public class I18nValidationMessageTest {

  @Autowired
  private TestApp app;

  @Test
  void switchesMessageContentBasedOnTheCurrentLocale() throws Exception {
    app.createRequest(GET, "/validator/validateI18nExample");
    ModuleUtils.getCurrent().loadPlugins(
      new ActionServlet(new ServletConfigBean("action", ActionServlet.class.getName()))
    );
    var form = ModuleUtils.getCurrent().getValidatorResources().getForm("i18nForm");
    assertNotNull(form);
    var nameField = form.getFieldByName("name");
    var minLengthOfName = nameField.getRequiredVarValue("minlength");
    assertEquals("5", minLengthOfName);
  }
}
