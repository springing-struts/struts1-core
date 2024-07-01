package org.apache.struts.actions;

import org.apache.struts.action.Action;

/**
 * An Action helper class that dispatches to one of the public methods that
 * are named in the `parameter` attribute of the corresponding `ActionMapping`
 * and matches a submission parameter. This is useful for developers who
 * prefer to use many submit buttons, images, or submit links on a single form
 * and whose related actions exist in a single Action class.
 * The method(s) in the associated `Action` must have the same signature
 * (other than method name) of the standard `Action.execute()` method.
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
 * where `parameter` contains three possible methods and one default method if
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
 * To implement this `dispatch` behaviour in an `Action`, class create your
 * custom Action as follows, along with the methods you require (and optionally
 * "cancelled" and "unspecified" methods):
 * <pre>
 * public class MyCustomAction extends Action {
 *   protected ActionDispatcher dispatcher = new EventActionDispatcher(this);
 *   public ActionForward execute(
 *     ActionMapping mapping,
 *     ActionForm form,
 *     HttpServletRequest request,
 *     HttpServletResponse response
 *  ) throws Exception {
 *    return dispatcher.execute(mapping, form, request, response);
 *  }
 * }
 * </pre>
 */
public class EventActionDispatcher extends ActionDispatcher {
  public EventActionDispatcher(Action action) {
    super(action, ActionDispatcher.MAPPING_FLAVOR);
  }
}
