package org.apache.struts.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.BaseAction;

/**
 * An `Action` that forwards to the context-relative URI specified by the
 * `parameter` property of our associated `ActionMapping`. This can be used to
 * integrate Struts with other business logic components that are implemented
 * as servlets (or JSP pages), but still take advantage of the Struts
 * controller servlet's functionality (such as processing of form beans).
 * To configure the use of this Action in your `struts-config.xml` file, create
 * an entry like this:
 * <pre>
 *   <action
 *     path="/saveSubscription"
 *     type="org.apache.struts.actions.ForwardAction"
 *     name="subscriptionForm"
 *     scope="request"
 *     input="/subscription.jsp"
 *     parameter="/path/to/processing/servlet"/>
 *  </pre>
 *  which will forward control to the context-relative URI specified by the
 *  `parameter` attribute.
 */

public class ForwardAction extends BaseAction {

  @Override
  public ActionForward execute(
    ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    var path = mapping.getParameter();
    return new ActionForward(path);
  }
}
