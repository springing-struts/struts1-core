package org.apache.struts.action;

import org.apache.struts.TestApp;
import org.apache.struts.util.ModuleUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
public class ActionMappingTest {

  @Autowired
  private ModuleUtils moduleUtils;

  @Autowired
  private TestApp app;

  /**
   * <pre>
   *
   * <action
   *   path="/html-setters-submit"
   *   type="org.apache.struts.webapp.exercise.HtmlSettersAction"
   *   name="testbean"
   *   scope="session"
   *   validate="false">
   *   <forward name="input" path="/html-setters.do"/>
   * </action>
   *
   * public ActionForward execute(
   *   ActionMapping mapping,
   *   ActionForm form,
   *   HttpServletRequest request,
   *   HttpServletResponse response) throws Exception {
   *
   *   if (isCancelled(request)) {
   *     return mapping.findForward("redirect-default");
   *   }
   *   return mapping.findForward("input");
   * }
   * </pre>
   */
  @Test
  void itCanFindForwardsFromLocalForwardsAndGlobalForwards() {
    var request = app.createRequest(GET, "/exercise/html-setters-submit");
    var moduleConfig = moduleUtils.getModuleConfig(request);
    var actionConfig = moduleConfig.findActionConfig("/html-setters-submit");
    assertNotNull(actionConfig);
    var actionMapping = (ActionMapping) actionConfig;
    var locallyDefinedForward = actionMapping.findForward("redirect-default");
    assertNotNull(locallyDefinedForward);
    assertEquals("redirect-default", locallyDefinedForward.getName());
    var globalForward = actionConfig.findForwardConfig("input");
    assertNotNull(globalForward);
    assertEquals("input", globalForward.getName());
  }

  /**
   * <pre>
   *
   * <action
   *   path="/html-link-submit"
   *   type="org.apache.struts.webapp.exercise.HtmlSettersAction"
   *   name="testbean" scope="session"
   *   validate="false">
   *   <forward name="input" path="/html-link.do"/>
   * </action>
   *
   * public ActionForward execute(
   *   ActionMapping mapping,
   *   ActionForm form,
   *   HttpServletRequest request,
   *   HttpServletResponse response
   * ) throws Exception {
   *   if (isCancelled(request)) {
   *     return (mapping.findForward("redirect-default"));
   *   } else {
   *     return (mapping.findForward("input"));
   *   }
   * }
   * </pre>
   */
  @Test
  void testActionCanReceiveActionFormAssignedInActionMapping() throws Exception {
    var request =  app.createRequest(GET, "/exercise/html-link-submit");
    var moduleConfig = moduleUtils.getModuleConfig(request);
    var actionConfig = moduleConfig.findActionConfig("/html-link-submit");
    assertNotNull(actionConfig);
    assertEquals("/html-link-submit", actionConfig.getPath());
    var actionMapping = (ActionMapping) actionConfig;
    app.getActionContext().setActionMapping(actionMapping);
    var response = app.getResponse();
    app.getRequestProcessor().process(request, response);
    var mockResponse = (MockHttpServletResponse) response.unwrap();
    assertEquals("/exercise/html-link.do", mockResponse.getForwardedUrl());
  }
}
