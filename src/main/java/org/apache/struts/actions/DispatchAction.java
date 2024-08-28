package org.apache.struts.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.BaseAction;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * An abstract `Action` that dispatches to a public method that is named by
 * the request parameter whose name is specified by the `parameter` property
 * of the corresponding ActionMapping.
 * This Action is useful for developers who prefer to combine many similar
 * actions into a single Action class, in order to simplify their application
 * design.
 * To configure the use of this action in your `struts-config.xml` file, create
 * an entry like this:
 * <pre>
 *   <action
 *     path="/saveSubscription"
 *     type="org.apache.struts.actions.DispatchAction"
 *     name="subscriptionForm"
 *     scope="request"
 *     input="/subscription.jsp"
 *     parameter="method"
 *   />
 * </pre>
 * which will use the value of the request parameter named "method" to pick
 * the appropriate "execute" method, which must have the same signature (other
 * than method name) of the standard `Action.execute` method. For example, you
 * might have the following three methods in the same action:
 * <pre>
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
 * </pre>
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
  @Override
  public ActionForward execute(
    ActionMapping mapping,
    @Nullable ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    var dispatchTarget = getMethodName(mapping, form, request, response);
    var method = findActionMethod(dispatchTarget);
    if (method == null || method.getName().equals("execute")) throw new ResponseStatusException(
      NOT_FOUND,
      String.format(
       "Failed to dispatch this request to action [%s] as the dispatch target [%s] is invalid.",
       mapping.getPath(), dispatchTarget
      )
    );
    try {
      return (ActionForward) method.invoke(this, mapping, form, request, response);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(String.format(
        "Failed to dispatch the action [%s] to the method [%s].",
        mapping.getPath(), dispatchTarget
      ), e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException cause) {
        throw cause;
      }
      throw new RuntimeException(String.format(
        "An error occurred while dispatching the action [%s] to the method [%s].",
        mapping.getPath(), dispatchTarget
      ), e);
    }
  }

  private @Nullable Method findActionMethod(String name) {
    var method = BeanUtils.findDeclaredMethod(
      getClass(),
      name,
      ActionMapping.class,
      ActionForm.class,
      HttpServletRequest.class,
      HttpServletResponse.class
    );
    if (method != null) {
      method.setAccessible(true);
    }
    return method;
  }

  /**
   * Returns the method name, given a parameter's value.
   */
  protected String getMethodName(
      ActionMapping mapping,
      @Nullable ActionForm form,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    var dispatchKey = getActionMappingParameter(mapping);
    var dispatchTarget = request.getParameter(dispatchKey);
    if (dispatchTarget == null || dispatchTarget.isBlank()) throw new ResponseStatusException(
      NOT_FOUND,
      String.format(
        "Failed to dispatch this request to action [%s] as the dispatch key [%s] was blank.",
        mapping.getPath(), dispatchKey
      )
    );
    return dispatchTarget;
  }

  protected String getActionMappingParameter(ActionMapping mapping) {
    var param = mapping.getParameter();
    if (param == null || param.isBlank()) throw new IllegalStateException(String.format(
      "The parameter attribute of mapping [%s] required to determine dispatch method is missing.",
      mapping.getPath()
    ));
    return param;
  }
}
