package org.apache.struts.action;

import org.apache.struts.Globals;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.taglib.html.Constants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import springing.struts1.controller.StrutsRequestContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static java.util.Objects.requireNonNull;
import static springing.struts1.validator.ValidationUtils.bindRequest;

/**
 * `RequestProcessor` contains the processing logic that the `ActionServlet`
 * performs as it receives each servlet request from the container. You can
 * customize the request processing behavior by subclassing this class and
 * overriding the method(s) whose behavior you are interested in changing.
 */
public class RequestProcessor implements ApplicationContextAware {

  /**
   * Initialize this request processor instance.
   */
  @Autowired
  public void init(ActionServlet servlet, ModuleConfig moduleConfig) throws ServletException {
    this.servlet = servlet;
    this.moduleConfig = moduleConfig;
  }

  private ActionServlet getActionServlet() {
    return requireNonNull(
      servlet,
      "This request processor has not been initialized yet."
    );
  }

  protected @Nullable ActionServlet servlet;

  protected @Nullable ModuleConfig moduleConfig;

  /**
   * `Process` an `HttpServletRequest` and create the corresponding
   * `HttpServletResponse` or dispatch to another resource.
   */
  public void process(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    String path = processPath(request, response);
    var mapping = processMapping(request, response, path);
    var forwarded = !processForward(request, response, mapping);
    if (forwarded) {
      return;
    }
    var form = processActionForm(request, response, mapping);
    var rejectedAsInvalid = !processValidate(request, response, form, mapping);
    if (rejectedAsInvalid) {
      return;
    }
    try {
      var action = processActionCreate(request, response, mapping);
      var forward = action.execute(mapping, form, request, response);
      processForwardConfig(request, response, forward);
    } catch (Exception e) {
      //TODO
      throw new RuntimeException(e);
    }
  }

  /**
   * Identify and return the path component (from the request URI) that we will
   * use to select an `ActionMapping` with which to dispatch. If no such path
   * can be identified, create an error response and return `null`.
   */
  protected String processPath(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    return getActionMapping().getPath();
  }

  /**
   * Select the mapping used to process the selection path for this request. If
   * no mapping can be identified, create an error response and return `null`.
   */
  protected ActionMapping processMapping(
    HttpServletRequest request,
    HttpServletResponse response,
    String path
  ) {
    var mapping = getActionMapping();
    request.setAttribute(Globals.MAPPING_KEY, mapping);
    return mapping;
  }

  private ActionMapping getActionMapping() {
    return StrutsRequestContext.getActionMapping();
  }

  /**
   * Return an `Action` instance that will be used to process the current
   * request, creating a new one if necessary.
   */
  protected Action processActionCreate(
    HttpServletRequest request,
    HttpServletResponse response,
    ActionMapping mapping
  ) {
    return getApplicationContext().getBean(mapping.getActionClass());
  }

  /**
   * Retrieve and return the `ActionForm` associated with this mapping,
   * creating and retaining one if necessary. If there is no `ActionForm`
   * associated with this mapping, return `null`.
   */
  protected @Nullable ActionForm processActionForm(
    HttpServletRequest request,
    HttpServletResponse response,
    ActionMapping mapping
  ) {
    if (request.getParameter(Constants.CANCEL_PROPERTY) != null) {
      request.setAttribute(Globals.CANCEL_KEY, Boolean.TRUE);
    }
    var formBeanConfig = mapping.getFormBeanConfig();
    if (formBeanConfig == null) {
      var formBeanKey = (String) request.getAttribute(Constants.BEAN_KEY);
      if (formBeanKey == null || formBeanKey.isEmpty()) {
        return null;
      }
      return (ActionForm) request.getAttribute(formBeanKey);
    }
    var formBean = formBeanConfig.createActionForm();
    try {
      bindRequest(request, formBean);
    } catch (BindException e) {
      throw new IllegalArgumentException(String.format(
        "Failed to bind the parameters of the request [%s] to the form bean [%s].",
        mapping.getActionId(), mapping.getName()
      ), e);
    }
    return formBean;
  }

  /**
   * Forward or redirect to the specified destination, by the specified
   * mechanism. This method uses a `ForwardConfig` object instead an
   * `ActionForward`. Returns `true` to continue normal processing,
   * `false` if a response has been created.
   */
  protected boolean processForward(
    HttpServletRequest request,
    HttpServletResponse response,
    ActionMapping mapping
  ) throws IOException, ServletException {
    var forwardUrl = mapping.getForwardUrl();
    if (forwardUrl == null) {
      return true;
    }
    doForward(forwardUrl, request, response);
    return false;
  }

  /**
   * Do an include of specified URI using a `RequestDispatcher`. This method is
   * used by all internal method needing to do an include.
   */
  protected void doInclude(
    String uri,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    doForwardOrInclude(uri, request, response, false);
  }

  /**
   * Do a forward to specified URI using a `RequestDispatcher`. This method is
   * used by all internal method needing to do a forward.
   */
  protected void doForward(
    String uri,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    doForwardOrInclude(uri, request, response, true);
  }

  private void doForwardOrInclude(
    String uri,
    HttpServletRequest request,
    HttpServletResponse response,
    boolean isForward
  ) throws ServletException, IOException {
    var dispatcher = request.getRequestDispatcher(uri);
    if (dispatcher == null) throw new ServletException(String.format(
      "Failed to retrieve a RequestDispatcher for url [%s].", uri
    ));
    try {
      if (isForward) {
        dispatcher.forward(request.unwrap(), response.unwrap());
      }
      else {
        dispatcher.include(request.unwrap(), response.unwrap());
      }
    } catch (jakarta.servlet.ServletException e) {
      throw new ServletException(e);
    }
  }

  /**
   * If this request was not cancelled, and the request's `ActionMapping` has
   * not disabled validation, call the `validate` method of the specified
   * `ActionForm`, and forward to the input path if there were any errors.
   * Return `true` if we should continue processing, or `false` if we have
   * already forwarded control back to the input form.
   */
  protected boolean processValidate(
    HttpServletRequest request,
    HttpServletResponse response,
    @Nullable ActionForm form,
    ActionMapping mapping
  ) throws IOException, ServletException {
    if (form == null || !mapping.getValidate()) {
      return true;
    }
    var errors = form.validate(mapping, request);
    if (errors == null || errors.isEmpty()) {
      return true;
    }
    var input = mapping.getInputForward();
    if (input == null) throw new IllegalStateException(
      "The input property is required when form validation is enabled: " +
      mapping.getName()
    );
    request.setAttribute(Globals.ERROR_KEY, errors);
    processForwardConfig(request, response, input);
    return false;
  }

  /**
   * Forward or redirect to the specified destination, by the specified
   * mechanism. This method uses a `ForwardConfig` object instead an
   * `ActionForward`.
   */
  protected void processForwardConfig(
    HttpServletRequest request,
    HttpServletResponse response,
    ForwardConfig forward
  ) throws IOException, ServletException {
    var url = forward.getUrl();
    if (forward.getRedirect()) {
      response.sendRedirect(url);
      return;
    }
    doForward(url, request, response);
  }

  /**
   * Do a module relative forward to specified URI using request dispatcher.
   * URI is relative to the current module. The real URI is computed by
   * prefixing the module name. This method is used internally and is not part
   * of the public API. It is advised to not use it in subclasses.
   */
  protected void internalModuleRelativeForward(
    String uri,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  /**
   * Do a module relative include to specified uri using request dispatcher.
   * Uri is relative to the current module. The real uri is computed by
   * prefixing the module name. This method is used internally and is not part
   * of the public API. It is advised to not use it in subclasses.
   */
  protected void internalModuleRelativeInclude(
    String uri,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  /**
   * Return the `ServletContext` for the web application in which we are
   * running.
   */
  protected ServletContext getServletContext() {
    return getActionServlet().getServletContext();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
  protected ApplicationContext getApplicationContext() {
    return requireNonNull(
      applicationContext,
      "This request processor has not been initialized yet."
    );
  }
  private @Nullable ApplicationContext applicationContext;
}
