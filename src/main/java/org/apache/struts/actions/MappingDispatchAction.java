package org.apache.struts.actions;
/**
 * An abstract `Action` that dispatches to a public method that is named by
 * the `parameter` attribute of the corresponding ActionMapping. This is
 * useful for developers who prefer to combine many related actions into a
 * single Action class.
 * To configure the use of this action in your `struts-config.xml` file,
 * create an entry like this:
 * <pre>
 *   <action
 *     path="/saveSubscription"
 *     type="org.example.SubscriptionAction"
 *     name="subscriptionForm"
 *     scope="request"
 *     input="/subscription.jsp"
 *     parameter="method"
 *   />
 * </pre>
 * where 'method' is the name of a method in your subclass of
 * `MappingDispatchAction` that has the same signature (other than method
 * name) of the standard `Action.execute` method. For example, you might
 * combine the methods for managing a subscription into a  single
 * `MappingDispatchAction` class using the following methods:
 * <pre>
 *   public ActionForward create(
 *     ActionMapping mapping,
 *     ActionForm form,
 *     HttpServletRequest request,
 *     HttpServletResponse response
 *   ) throws Exception
 *
 *   public ActionForward edit(
 *     ActionMapping mapping,
 *     ActionForm form,
 *     HttpServletRequest request,
 *     HttpServletResponse response
 *   ) throws Exception
 *
 *   public ActionForward save(
 *     ActionMapping mapping,
 *     ActionForm form,
 *     HttpServletRequest request,
 *     HttpServletResponse response
 *   ) throws Exception
 *
 *   public ActionForward delete(
 *     ActionMapping mapping,
 *     ActionForm form,
 *     HttpServletRequest request,
 *     HttpServletResponse response
 *   ) throws Exception
 *
 *   public ActionForward list(
 *     ActionMapping mapping,
 *     ActionForm form,
 *     HttpServletRequest request,
 *     HttpServletResponse response
 *   ) throws Exception
 * </pre>
 * for which you would create corresponding <action> configurations that
 * reference this class:
 * <pre>
 *   <action
 *     path="/createSubscription"
 *     type="org.example.SubscriptionAction"
 *     parameter="create">
 *     <forward name="success" path="/editSubscription.jsp" />
 *   </action>
 *   <action
 *     path="/editSubscription"
 *     type="org.example.SubscriptionAction"
 *     parameter="edit">
 *     <forward name="success" path="/editSubscription.jsp"/>
 *   </action>
 *   <action
 *     path="/saveSubscription"
 *     type="org.example.SubscriptionAction"
 *     parameter="save"
 *     name="subscriptionForm"
 *     validate="true"
 *     input="/editSubscription.jsp"
 *     scope="request">
 *     <forward name="success" path="/savedSubscription.jsp" />
 *   </action>
 *   <action
 *     path="/deleteSubscription"
 *     type="org.example.SubscriptionAction"
 *     name="subscriptionForm"
 *     scope="request"
 *     input="/subscription.jsp"
 *     parameter="delete"&gt;
 *     <forward name="success" path="/deletedSubscription.jsp" />
 *   </action>
 *   <action
 *     path="/listSubscriptions"
 *     type="org.example.SubscriptionAction"
 *     parameter="list">
 *     <forward name="success" path="/subscriptionList.jsp" />
 *   </action>
 * </pre>
 * **NOTE**
 * Unlike DispatchAction, mapping characteristics may differ between the
 * various handlers, so you can combine actions in the
 * same class that, for example, differ in their use of forms or validation.
 * Also, a request parameter, which would be visible to the application user,
 * is not required to enable selection of the handler method. </p>
 *
 * @version $Rev$ $Date$
 * @since Struts 1.2
 */
public class MappingDispatchAction extends DispatchAction {
}
