package org.apache.struts.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.BaseAction;
import org.springframework.lang.Nullable;

/**
 * An abstract `Action` that dispatches to a public method that is named by
 * the request parameter whose name is specified by the `parameter` property
 * of the corresponding ActionMapping.
 * This Action is useful for developers who prefer to combine many similar
 * actions into a single Action class, in order to simplify their application
 * design.
 * To configure the use of this action in your `struts-config.xml` file, create
 * an entry like this:
 * <pre>{@code
 *   <action
 *     path="/saveSubscription"
 *     type="org.apache.struts.actions.DispatchAction"
 *     name="subscriptionForm"
 *     scope="request"
 *     input="/subscription.jsp"
 *     parameter="method"
 *   />
 * }</pre>
 * which will use the value of the request parameter named "method" to pick
 * the appropriate "execute" method, which must have the same signature (other
 * than method name) of the standard `Action.execute` method. For example, you
 * might have the following three methods in the same action:
 * <pre>{@code
 * public ActionForward delete(
 *   ActionMapping mapping,
 *   ActionForm form,
 *   HttpServletRequest request,
 *   HttpServletResponse response
 * ) throws Exception
 *
 * public ActionForward insert(
 *   ActionMapping mapping,
 *   ActionForm form,
 *   HttpServletRequest request,
 *   HttpServletResponse response
 * ) throws Exception
 *
 * public ActionForward update(
 *   ActionMapping mapping,
 *   ActionForm form,
 *   HttpServletRequest request,
 *   HttpServletResponse response
 * ) throws Exception
 * }</pre>
 * and call one of the methods with a URL like this:
 * <pre>
 *   http://localhost:8080/myapp/saveSubscription.do?method=update
 * </pre>
 * **NOTE**
 *   All the other mapping characteristics of this action must be shared by
 *   the various handlers. This places some constraints over what types of
 *   handlers may reasonably be packaged into the same `DispatchAction`
 *   subclass.
 * **NOTE**
 *   If the value of the request parameter is empty, a method named
 *   `unspecified` is called. The default action is to throw an exception.
 *   If the request was cancelled (a `html:cancel` button was pressed), the
 *   custom handler `cancelled` will be used instead. You can also override
 *   the `getMethodName` method to override the action's default handler
 *   selection.
 */
public abstract class DispatchAction extends BaseAction {

  public DispatchAction() {
    dispatcher = new ActionDispatcher(this);
  }

  protected ActionDispatcher dispatcher;

  @Override
  public ActionForward execute(
    ActionMapping mapping,
    @Nullable ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    return dispatcher.execute(mapping, form, request, response);
  }
}
