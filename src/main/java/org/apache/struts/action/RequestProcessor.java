package org.apache.struts.action;

import static java.util.Objects.requireNonNull;
import static springing.util.StringUtils.normalizeForwardPath;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.Globals;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

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
  public void init(ActionServlet servlet, ModuleConfig moduleConfig)
    throws ServletException {
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

  protected ModuleConfigBean getModuleConfig() {
    return requireNonNull(
      (ModuleConfigBean) moduleConfig,
      "This request processor has not been initialized yet."
    );
  }

  protected @Nullable ModuleConfig moduleConfig;

  @Autowired
  public void setActionContext(ServletActionContext actionContext) {
    this.actionContext = actionContext;
  }

  private @Nullable ServletActionContext actionContext;

  public ServletActionContext getActionContext() {
    return requireNonNull(
      actionContext,
      "This request processor has not been initialized yet."
    );
  }

  @Autowired
  public void setTagUtils(TagUtils tagUtils) {
    this.tagUtils = tagUtils;
  }

  public TagUtils getTagUtils() {
    return requireNonNull(tagUtils);
  }

  private @Nullable TagUtils tagUtils;

  /**
   * `Process` an `HttpServletRequest` and create the corresponding
   * `HttpServletResponse` or dispatch to another resource.
   */
  public void process(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    String path = processPath(request, response);
    var mapping = processMapping(request, response, path);
    var form = processActionForm(request, response, mapping);
    processPopulate(request, response, form, mapping);
    var forwarded = !processForward(request, response, mapping);
    if (forwarded) {
      return;
    }
    var included = !processInclude(request, response, mapping);
    if (included) {
      return;
    }
    ActionForward forward;
    try {
      var rejectedAsInvalid = !processValidate(
        request,
        response,
        form,
        mapping
      );
      if (rejectedAsInvalid) {
        return;
      }
      var action = processActionCreate(request, response, mapping);
      forward = processActionPerform(request, response, action, form, mapping);
    } catch (Exception e) {
      forward = processException(request, response, e, form, mapping);
    }
    if (forward != null) {
      processForwardConfig(request, response, forward);
    }
  }

  /**
   * Ask the specified `Action` instance to handle this request. Return the
   * `ActionForward` instance (if any) returned by the called `Action` for
   * further processing.
   */
  protected @Nullable ActionForward processActionPerform(
    HttpServletRequest request,
    HttpServletResponse response,
    Action action,
    @Nullable ActionForm form,
    ActionMapping mapping
  ) throws IOException, ServletException {
    try {
      return action.execute(mapping, form, request, response);
    } catch (Exception e) {
      return processException(request, response, e, form, mapping);
    }
  }

  /**
   * Ask our exception handler to handle the exception. Return the
   * `ActionForward` instance (if any) returned by the called
   * `ExceptionHandler`.
   */
  protected @Nullable ActionForward processException(
    HttpServletRequest request,
    HttpServletResponse response,
    Exception exception,
    @Nullable ActionForm form,
    ActionMapping mapping
  ) throws IOException, ServletException {
    request.setAttribute(Globals.EXCEPTION_KEY, exception);
    switch (exception) {
      case ServletException servletException -> throw servletException;
      case IOException ioException -> throw ioException;
      case RuntimeException runtimeException -> throw runtimeException;
      default -> throw new ServletException(exception);
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
  ) throws IOException {
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
    return getActionContext().getActionMapping();
  }

  /**
   * Return an `Action` instance that will be used to process the current
   * request, creating a new one if necessary.
   */
  protected Action processActionCreate(
    HttpServletRequest request,
    HttpServletResponse response,
    ActionMapping mapping
  ) throws IOException {
    return getApplicationContext()
      .getBean(mapping.getActionClass(getActionContext()));
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
    return mapping.prepareForm(request.getRequestURI(), request);
  }

  /**
   * Populate the properties of the specified `ActionForm` instance from the
   * request parameters included with this request. In addition, request
   * attribute `Globals.CANCEL_KEY` will be set if the request was submitted
   * with a button created by `CancelTag`.
   */
  protected void processPopulate(
    HttpServletRequest request,
    HttpServletResponse response,
    @Nullable ActionForm form,
    ActionMapping mapping
  ) throws ServletException {
    if (request.getParameter(Constants.CANCEL_PROPERTY) != null) {
      request.setAttribute(Globals.CANCEL_KEY, Boolean.TRUE);
    }
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
    var forwardPath = mapping.getForwardPath(request);
    if (forwardPath == null) {
      return true;
    }
    internalModuleRelativeForward(forwardPath, request, response);
    return false;
  }

  /**
   * Process an include requested by this mapping (if any). Return `true` if
   * standard processing should continue, or `false` if we have already handled
   * this request.
   */
  protected boolean processInclude(
    HttpServletRequest request,
    HttpServletResponse response,
    ActionMapping mapping
  ) throws IOException, ServletException {
    var includePath = mapping.getInclude();
    if (includePath == null) {
      return true;
    }
    internalModuleRelativeInclude(includePath, request, response);
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
    getActionContext().forwardRequest(uri, true);
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
    getActionContext().forwardRequest(uri, false);
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
  ) throws IOException, ServletException, InvalidCancelException {
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
      mapping.getPath()
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
    String forwardPath,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    doForward(toContextRelPath(forwardPath), request, response);
  }

  /**
   * Do a module relative include to specified uri using request dispatcher.
   * Uri is relative to the current module. The real uri is computed by
   * prefixing the module name. This method is used internally and is not part
   * of the public API. It is advised to not use it in subclasses.
   */
  protected void internalModuleRelativeInclude(
    String includePath,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    doInclude(toContextRelPath(includePath), request, response);
  }

  private String toContextRelPath(String moduleRelPath) {
    return normalizeForwardPath(
      "/" + getModuleConfig().getPrefix() + "/" + moduleRelPath
    );
  }

  /**
   * Return the `ServletContext` for the web application in which we are
   * running.
   */
  protected ServletContext getServletContext() {
    return getActionServlet().getServletContext();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
    throws BeansException {
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
