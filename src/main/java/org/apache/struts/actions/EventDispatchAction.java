package org.apache.struts.actions;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An `Action` that dispatches to one of the public methods that are named in
 * the `parameter` attribute of the corresponding `ActionMapping` and matches
 * a submission parameter. This is useful for developers who prefer to use many
 * submit buttons, images, or submit links on a single form and whose related
 * actions exist in a single Action class.
 * The method(s) must have the same signature (other than method name) of the
 * standard `Action.execute()` method.
 * To configure the use of this action in your `struts-config.xml` file, create
 * an entry like this:
 * <pre>
 * <action
 *   path="/saveSubscription"
 *   type="org.example.SubscriptionAction"
 *   name="subscriptionForm"
 *   scope="request"
 *   input="/subscription.jsp"
 *   parameter="save,back,recalc=recalculate,default=save"
 * />
 * </pre>
 * here `parameter` contains three possible methods and one default method if
 * nothing matches (such as the user pressing the enter key).
 * For utility purposes, you can use the `key=value` notation to alias methods
 * so that they are exposed as different form element names, in the event of a
 * naming conflict or otherwise. In this example, the `recalc` button (via a
 * request parameter) will invoke the `recalculate` method. The security-minded
 * person may find this feature valuable to obfuscate and not expose the
 * methods.
 * The `default` key is purely optional. If this is not specified and no
 * parameters match the list of method keys, `null` is returned which means the
 * `unspecified` method will be invoked.
 * The order of the parameters are guaranteed to be iterated in the order
 * specified. If multiple buttons were accidentally submitted, the first match
 * in the list will be dispatched.
 */
public class EventDispatchAction extends DispatchAction {

  @Override
  protected String getMethodName(
    ActionMapping mapping,
    @Nullable ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    var parameter = getActionMappingParameter(mapping);
    var requestParams = request.getParameterMap();
    String defaultMethodName = null;
    for (var entry : parameter.split("\\s*,\\s*")) {
      var keyAndName = entry.split("=");
      var key = keyAndName[0];
      var methodName = keyAndName.length > 1 ? keyAndName[1] : key;
      if (key.equals("default")) {
        defaultMethodName = methodName;
        continue;
      }
      if (requestParams.containsKey(key)) {
        return methodName;
      }
    }
    return defaultMethodName != null ? defaultMethodName : "unspecified";
  }
}
