package org.apache.struts.actions;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * An abstract `Action` that dispatches to the subclass mapped `execute()`
 * method. This is useful in cases where an HTML form has multiple submit
 * buttons with the same name. The button name is specified by the `parameter`
 * property of the corresponding ActionMapping. To configure the use of this
 * action in your `struts-config.xml` file, create an entry like this:
 * <pre>
 * <action
 *   path="/test"
 *   type="org.example.MyAction"
 *   name="MyForm"
 *   scope="request"
 *   input="/test.jsp"
 *   parameter="method"
 * />
 * </pre>
 * which will use the value of the request parameter named "method" to locate
 * the corresponding key in ApplicationResources. For example, you might have
 * the following ApplicationResources.properties:
 * <pre>
 * button.add=Add Record
 * button.delete=Delete Record
 * </pre>
 * And your JSP would have the following format for submit buttons:
 * <pre>
 * <html:form action="/test">
 *   <html:submit property="method">
 *     <bean:message key="button.add" />
 *   </html:submit>
 *   <html:submit property="method">
 *     <bean:message key="button.delete"/>
 *   </html:submit>
 * </html:form>
 * </pre>
 * Your subclass must implement both getKeyMethodMap and the methods defined
 * in the map. An example of such implementations are:
 * <pre>
 * protected Map getKeyMethodMap() {
 *   Map map = new HashMap();
 *   map.put("button.add", "add");
 *   map.put("button.delete", "delete");
 *   return map;
 * }
 *
 * public ActionForward add(
 *   ActionMapping mapping,
 *   ActionForm form,
 *   HttpServletRequest request,
 *   HttpServletResponse response
 * ) throws IOException, ServletException {
 *   // do add
 *   return mapping.findForward("success");
 * }
 *
 * public ActionForward delete(
 *   ActionMapping mapping,
 *   ActionForm form,
 *   HttpServletRequest request,
 *   HttpServletResponse response
 * ) throws IOException, ServletException {
 *   // do delete
 *   return mapping.findForward("success");
 * }
 * </pre>
 * **Notes**
 * If duplicate values exist for the keys returned by getKeys, only the first
 * one found will be returned. If no corresponding key is found then an
 * exception will be thrown. You can override the method `unspecified` to
 * provide a custom handler. If the request to submit was cancelled
 * (a `<html:cancel>` button was pressed), the custom handler `cancelled` will
 * be used instead.
 */
public abstract class LookupDispatchAction extends DispatchAction {
  public LookupDispatchAction() {
    dispatcher = new ActionDispatcher(this) {
      @Override
      protected String getMethodName(
        ActionMapping mapping,
        @Nullable ActionForm form,
        HttpServletRequest request,
        HttpServletResponse response,
        String parameter
      ) throws Exception {
        return LookupDispatchAction.this.getMethodName(mapping, form, request, response);
      }
    };
  }

  protected String getMethodName(
    ActionMapping mapping,
    @Nullable ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    var key = dispatcher.getParameter(mapping, form, request, response);
    var submitButtonLabel = request.getParameter(key);
    if (submitButtonLabel == null || submitButtonLabel.isBlank()) throw new ResponseStatusException(
      NOT_FOUND,
      String.format("Filed to dispatch request because the request parameter [%s] was blank.", key)
    );
    var messageResources = MessageResources.getMessageResources();
    for (var entry : getKeyMethodMap().entrySet()) {
      var buttonLabelKey = entry.getKey();
      var methodName = entry.getValue();
      var buttonLabel = messageResources.getMessage(buttonLabelKey);
      if (submitButtonLabel.equals(buttonLabel)) {
        return methodName;
      }
    }
    throw new ResponseStatusException(NOT_FOUND, String.format(
      "There is no dispatch target for the button with label [%s] in the mapping [%s].",
      submitButtonLabel, mapping.getPath()
    ));
  }

  /**
   * Provides the mapping from resource key to method name.
   */
  protected abstract Map<String, String> getKeyMethodMap();
}
