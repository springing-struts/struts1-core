package org.apache.struts.actions;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.chain.contexts.ActionContext;
import org.apache.struts.dispatcher.Dispatcher;
import org.apache.struts.util.MessageResources;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.apache.struts.Globals.CANCEL_KEY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * Action `helper` class that dispatches to a public method in an Action.
 * This class is provided as an alternative mechanism to using DispatchAction
 * and its various flavours and means `Dispatch` behaviour can be easily
 * implemented into any `Action` without having to inherit from a particular
 * super `Action`.
 * To implement `dispatch` behaviour in an `Action` class, create your custom
 * Action as follows, along with the methods you require (and optionally
 * "cancelled" and "unspecified" methods):
 *
 * <pre>
 *   public class MyCustomAction extends Action {
 *
 *     protected ActionDispatcher dispatcher
 *       = new ActionDispatcher(this, ActionDispatcher.MAPPING_FLAVOR);
 *
 *     public ActionForward execute(
 *       ActionMapping mapping,
 *       ActionForm form,
 *       HttpServletRequest request,
 *       HttpServletResponse response
 *     ) throws Exception {
 *       return dispatcher.execute(mapping, form, request, response);
 *     }
 *   }
 * </pre>
 *
 * It provides three flavours of determining the name of the method:
 * **DEFAULT_FLAVOR**
 *   uses the parameter specified in the struts-config.xml to get the method
 *   name from the Request (equivalent to `DispatchAction` except uses
 *   "method" as a default if the `parameter` is not specified in the
 *   struts-config.xml).
 * **DISPATCH_FLAVOR**
 *   uses the parameter specified in the struts-config.xml to get the method
 *   name from the Request (equivalent to `DispatchAction`).
 * **MAPPING_FLAVOR**
 *   uses the parameter specified in the struts-config.xml as the method name
 *   (equivalent to `MappingDispatchAction`).
 */
public class ActionDispatcher implements Dispatcher {
  /**
   * Indicates "default" dispatch flavor.
   */
  public static final int DEFAULT_FLAVOR = 0;
  /**
   * Indicates "mapping" dispatch flavor.
   */
  public static final int MAPPING_FLAVOR = 1;
  /**
   * Indicates flavor compatible with DispatchAction.
   */
  public static final int DISPATCH_FLAVOR = 2;

  public static final String EXECUTE_METHOD_NAME = "execute";

  public ActionDispatcher(Action action, int flavor) {
    this.action = action;
    this.actionClass = action.getClass();
    this.flavor = flavor;
  }

  public ActionDispatcher(Action action) {
    this(action, DEFAULT_FLAVOR);
  }

  private final Action action;
  private final Class<? extends Action> actionClass;
  private final int flavor;

  /**
   * Process the specified HTTP request, and create the corresponding HTTP
   * response (or forward to another web component that will create it).
   * Return an `ActionForward` instance describing where and how control should
   * be forwarded, or `null` if the response has already been completed.
   */
  public ActionForward execute(
    ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    if (isCancelled(request)) {
      var forward = cancelled(mapping, form, request, response);
      if (forward != null) {
        return forward;
      }
    }
    var parameter = getParameter(mapping, form, request, response);
    var methodName = getMethodName(mapping, form, request, response, parameter);
    var recursive = methodName.equals("execute") || methodName.equals("perform");
    if (recursive) throw new ResponseStatusException(NOT_FOUND, String.format(
      "Failed to dispatch the request for the path: [%s] (parameter: [%s], methodName: [%s]).",
      mapping.getPath(), parameter, methodName
    ));
    return dispatchMethod(mapping, form, request, response, methodName);
  }

  /**
   * Returns the method name, given a parameter's value.
   */
  protected String getMethodName(
    ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response,
    String parameter
  ) throws Exception {
    if (flavor == MAPPING_FLAVOR) {
      return parameter;
    }
    return request.getParameter(parameter);
  }

  /**
   * Returns the parameter value as influenced by the selected `flavor`
   * specified for this `ActionDispatcher`.
   */
  protected String getParameter(
    ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    var parameter = mapping.getParameter();
    if (!isEmpty(parameter)) {
      return parameter;
    }
    if (flavor == DEFAULT_FLAVOR) {
      return "method";
    }
    throw new IllegalArgumentException(String.format(
      "The parameter of the mapping [%s] is not defined, which is required to determine the dispatch target.",
      mapping.getPath()
    ));
  }

  /**
   * Dispatches to the target class' cancelled method, if present, otherwise
   * returns null. Classes utilizing `ActionDispatcher` should provide a
   * `cancelled` method if they wish to provide behavior different from
   * returning `null`.
   */
  protected @Nullable ActionForward cancelled(
    ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    return dispatchMethod(mapping, form, request, response, "cancelled");
  }

  /**
   * Dispatches to the target class' `unspecified` method, if present,
   * otherwise throws a ServletException. Classes utilizing `ActionDispatcher`
   * should provide an `unspecified` method if they wish to provide behavior
   * different from throwing a ServletException.
   */
  protected ActionForward unspecified(
    ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    return dispatchMethod(mapping, form, request, response, "unspecified");
  }

  private ActionForward dispatchMethod(
      ActionMapping mapping,
      ActionForm form,
      HttpServletRequest request,
      HttpServletResponse response,
      @Nullable String name
  ) throws Exception {
    if (name == null) {
      return unspecified(mapping, form, request, response);
    }
    var method = getMethod(name);
    if (method == null) throw new ResponseStatusException(NOT_FOUND, String.format(
      "Failed to dispatch the request [%s] because the given dispatch target [%s] is unknown.",
      mapping.getPath(), name
    ));
    try {
      return (ActionForward) method.invoke(action, mapping, form, request, response);
    } catch (InvocationTargetException e) {
      var cause = e.getCause();
      if (cause instanceof Exception runtimeError) {
        throw runtimeError;
      }
      throw new RuntimeException(String.format(
        "An error occurred while invoking action method [%s] for the request [%s].",
        name, mapping.getPath()
      ), cause);
    }
  }

  /**
   * Returns `true` if the current form's cancel button was pressed. This
   * method will check if the `Globals.CANCEL_KEY` request attribute has been
   * set, which normally occurs if the cancel button generated by `CancelTag`
   * was pressed by the user in the current request. If `true`, validation
   * performed by an `ActionForm`'s `validate()` method will have been skipped
   * by the controller servlet.
   */
  private boolean isCancelled(HttpServletRequest request) {
    return request.getAttribute(CANCEL_KEY) != null;
  }

  private @Nullable Method getMethod(String methodName) {
    return ReflectionUtils.findMethod(
      actionClass,
      methodName,
      ActionMapping.class,
      ActionForm.class,
      HttpServletRequest.class,
      HttpServletResponse.class
    );
  }

  @Override
  public Object dispatch(ActionContext context) throws Exception {
    //TODO
    throw new UnsupportedOperationException();
  }
}
