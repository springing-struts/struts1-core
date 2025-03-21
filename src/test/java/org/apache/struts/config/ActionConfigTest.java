package org.apache.struts.config;

import org.apache.struts.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class ActionConfigTest {

  @Autowired
  private TestApp app;

  /**
   * Display any other page (Logon, MainMenu) - Wildcards must come first!
   * <action
   *    path="/*"
   *    forward="/{1}.jsp">
   * </action>
   */
  @Test
  public void itInterpolatesPlaceholdersInTemplateStringWithPathParameters() {
    var request = app.createRequest(GET, "/mailreader/Logon");
    var forwardPath = app.getActionContext().getActionConfig().getForwardPath(request);
    assertThat(forwardPath).isEqualTo("/Logon.jsp");

    request = app.createRequest(GET, "/mailreader/Logout");
    forwardPath = app.getActionContext().getActionConfig().getForwardPath(request);
    assertThat(forwardPath).isEqualTo("/Logout.jsp");
  }

  /**
   * Abstract" mapping to use as base
   * <action path="//BaseAction"
   *   input="Input"
   *   type="org.apache.struts.apps.mailreader.actions.{1}Action"
   *   name="{1}Form"
   *   scope="request">
   *   <forward
   *     name="Success"
   *     path="/{1}.jsp"/>
   *   <forward
   *     name="Input"
   *     path="/{1}.jsp"/>
   * </action>
   * <action
   *   path="/Delete*"
   *   extends="//BaseAction"
   *   parameter="Delete"
   *   validate="false"/>
   * <action
   *   path="/Edit*"
   *   extends="//BaseAction"
   *   parameter="Edit"
   *   validate="false"/>
   * <action
   *   path="/Save*"
   *   extends="//BaseAction"
   *   parameter="Save"
   *   cancellable="true"
   *   validate="true">
   *   <forward
   *     name="Success"
   *     path="/MainMenu.do"/>
   */
  @Test
  public void testActionConfigInheritanceAndInterpolation() {
    var request = app.createRequest(GET, "/mailreader/EditRegistration.do");
    var actionConfig = app.getActionContext().getActionConfig();
    assertThat(actionConfig.getName()).isEqualTo("{1}Form");
    assertThat(actionConfig.getName(request)).isEqualTo("RegistrationForm");
  }
}
