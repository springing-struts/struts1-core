package org.apache.struts.config;

import static java.lang.String.format;
import static java.util.Objects.*;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static springing.struts1.validator.ValidationUtils.bindRequest;
import static springing.util.ObjectUtils.classFor;
import static springing.util.StringUtils.normalizeForwardPath;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.taglib.html.Constants;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.BindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springing.struts1.controller.JspForwardingHandler;
import springing.struts1.controller.StrutsRequestHandler;

/**
 * A JavaBean representing the configuration information of an `action`
 * element from a Struts module configuration file.
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.CLASS,
  include = JsonTypeInfo.As.PROPERTY,
  property = "class"
)
public class ActionConfig {

  @JsonBackReference
  private ActionMappingsConfig container;

  public ModuleConfigBean getModuleConfig() {
    return requireNonNull(moduleConfig);
  }

  void setModuleConfig(ModuleConfigBean config) {
    moduleConfig = config;
  }

  private @Nullable ModuleConfigBean moduleConfig;

  /**
   * Return context-relative path of the submitted request, starting with
   * a slash ("/") character, and omitting any filename extension if extension
   * mapping is being used.
   */
  public String getPath() {
    return normalizeForwardPath(path);
  }

  public void setPath(String path) {
    this.path = path;
  }

  @JacksonXmlProperty(localName = "path", isAttribute = true)
  private String path;

  /**
   * Return the context-relative URL.
   */
  @JsonIgnore
  public String getActionUrl() {
    return getModuleConfig().getActionUrl(path);
  }

  /**
   * Return name of the form bean, if any, associated with this Action.
   */
  public @Nullable String getName() {
    return getInheritedProperty(String.class, it -> it.name);
  }

  public @Nullable String getName(HttpServletRequest request) {
    return interpolatePathParams(getName(), request);
  }

  @JacksonXmlProperty(localName = "name", isAttribute = true)
  private @Nullable String name;

  /**
   * The internal identification of this action mapping. Identifications are
   * not inheritable and must be unique within a module.
   */
  public @Nullable String getActionId() {
    return actionId;
  }

  @JacksonXmlProperty(localName = "actionId", isAttribute = true)
  private @Nullable String actionId;

  /**
   * The path or action id of the action mapping that this object should
   * inherit properties from.
   */
  @JacksonXmlProperty(localName = "extends", isAttribute = true)
  private @Nullable String inherit;

  /**
   * Returns a FormBeanConfig associated with this Action.
   */
  @JsonIgnore
  public @Nullable FormBeanConfig getFormBeanConfig(
    HttpServletRequest request
  ) {
    var formName = getName(request);
    if (formName == null) {
      return null;
    }
    for (var formBeanConfig : getModuleConfig().getFormBeanConfigs()) {
      if (formName.equals(formBeanConfig.getName())) {
        return formBeanConfig;
      }
    }
    throw new IllegalStateException(
      format(
        "Unknown form bean name [%s] is referred from the action [%s]." +
        " Please check the configuration files at [%s].",
        formName,
        getActionUrl(),
        getModuleConfig().getConfigFilePaths()
      )
    );
  }

  /**
   * Return general purpose configuration parameter that can be used to pass
   * extra information to the Action instance selected by this Action.
   * Struts does not itself use this value in any way.
   */
  public @Nullable String getParameter() {
    return getInheritedProperty(String.class, it -> it.parameter);
  }

  @JacksonXmlProperty(localName = "parameter", isAttribute = true)
  private @Nullable String parameter;

  /**
   * Return the forward configuration for the specified key, if any; otherwise
   * return `null`.
   */
  public @Nullable ForwardConfig findForwardConfig(String forwardName) {
    for (var forward : getLocalForwards()) {
      if (forwardName.equals(forward.getName())) {
        return forward;
      }
    }
    for (var forward : getModuleConfig().getGlobalForwards()) {
      if (forwardName.equals(forward.getName())) {
        return forward;
      }
    }
    return null;
  }

  /**
   * Returns context-relative path of the web application resource that will
   * process this request.
   */
  public @Nullable String getForwardPath() {
    return forwardPath;
  }

  public @Nullable String getForwardPath(HttpServletRequest request) {
    return interpolatePathParams(forwardPath, request);
  }

  @JacksonXmlProperty(localName = "forward-path", isAttribute = true)
  private @Nullable String forwardPath;

  /**
   * Returns context-relative path of the web application resource that will
   * process this request.
   */
  public @Nullable String getInclude() {
    return include;
  }

  public @Nullable String getInclude(HttpServletRequest request) {
    return interpolatePathParams(include, request);
  }

  @JacksonXmlProperty(localName = "include", isAttribute = true)
  private @Nullable String include;

  /**
   * Return all forward configurations for this module. If there are none, a
   * zero-length array is returned.
   */
  public ForwardConfig[] findForwardConfigs() {
    return localForwards.toArray(new ForwardConfig[0]);
  }

  @JacksonXmlProperty(localName = "forward")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setLocalForwards(List<ActionForward> localForwards) {
    localForwards.forEach(it -> it.setActionConfig(this));
    this.localForwards = localForwards;
  }

  private List<ActionForward> localForwards = new ArrayList<>();

  public List<ActionForward> getLocalForwards() {
    var parentConfig = getParentConfig();
    if (parentConfig == null) {
      return localForwards;
    }
    var forwards = new ArrayList<ActionForward>();
    forwards.addAll(localForwards);
    forwards.addAll(parentConfig.getLocalForwards());
    return forwards;
  }

  /**
   * Return the exception configurations for this action. If there are none,
   * a zero-length array is returned.
   */
  public ExceptionConfig[] findExceptionConfigs() {
    return localExceptionHandlers.toArray(new ExceptionConfig[0]);
  }

  @JacksonXmlProperty(localName = "exception")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<ExceptionConfig> localExceptionHandlers = new ArrayList<>();

  /**
   * Fully qualified Java class name of the `Action` class to be used to
   * process requests for this mapping if the `forward` and `include`
   * properties are not set. Exactly one of `forward`, `include`, or `type`
   * must be specified.
   */
  public @Nullable String getType() {
    return getInheritedProperty(String.class, it -> it.type);
  }

  public @Nullable String getType(HttpServletRequest request) {
    return interpolatePathParams(getType(), request);
  }

  @JacksonXmlProperty(localName = "type", isAttribute = true)
  private @Nullable String type;

  private <T> @Nullable T getInheritedProperty(
    Class<T> propType,
    Function<ActionConfig, T> getProp
  ) {
    var selfValue = getProp.apply(this);
    if (selfValue != null) {
      return selfValue;
    }
    var parentConfig = getParentConfig();
    if (parentConfig == null) {
      return null;
    }
    return parentConfig.getInheritedProperty(propType, getProp);
  }

  private @Nullable ActionConfig getParentConfig() {
    if (inherit == null) {
      return null;
    }
    for (var mapping : container.getEntries()) {
      if (inherit.equals(mapping.path)) {
        return mapping;
      }
    }
    throw new IllegalArgumentException(
      format(
        "Failed to determine the parent action config [%s] of the action config [%s].",
        inherit,
        path
      )
    );
  }

  private boolean containsPlaceholder(@Nullable String prop) {
    if (prop == null) {
      return false;
    }
    var m = PATH_PARAMETER_REFERENCES.matcher(prop);
    return m.find();
  }

  public @Nullable String interpolatePathParams(
    @Nullable String str,
    HttpServletRequest request
  ) {
    if (str == null) {
      return null;
    }
    if (!str.contains("{")) {
      return str;
    }
    var i = new AtomicInteger(0);
    var template = Pattern.compile("\\*(?!\\*)")
      .matcher(path)
      .replaceAll(m -> {
        i.addAndGet(1);
        return "{" + i + "}";
      });
    var prefix = moduleConfig.getPrefix();
    var relPath = normalizeForwardPath(
      request.getRequestURI().substring(prefix.length())
    );
    var pathParams = new AntPathMatcher()
      .extractUriTemplateVariables(template, relPath);
    var interpolated = PATH_PARAMETER_REFERENCES.matcher(str).replaceAll(m -> {
      var paramName = m.group(1);
      return pathParams.get(paramName).replaceAll("\\.[0-9a-zA-Z]+$", "");
    });
    return interpolated;
  }

  private static final Pattern PATH_PARAMETER_REFERENCES = Pattern.compile(
    "(?!\\\\)\\{([0-9]+)}"
  );

  public Class<? extends Action> getActionClass() {
    if (resolvedActionClass != null) {
      return resolvedActionClass;
    }
    resolvedActionClass = doGetActionClass(getType());
    return resolvedActionClass;
  }

  public Class<? extends Action> getActionClass(
    ServletActionContext actionContext
  ) {
    return doGetActionClass(
      interpolatePathParams(getType(), actionContext.getRequest())
    );
  }

  private Class<? extends Action> doGetActionClass(@Nullable String type) {
    if (type == null) throw new IllegalStateException(
      format("Failed to determine action class for action mapping [%s].", path)
    );
    return classFor(type);
  }

  private @Nullable Class<? extends Action> resolvedActionClass;

  private void registerAction(GenericApplicationContext context) {
    var type = getType();
    if (type == null) {
      return;
    }
    if (containsPlaceholder(type)) {
      return;
    }
    doRegisterAction(context, getActionClass());
  }

  public void registerAction(
    GenericApplicationContext context,
    ServletActionContext actionContext
  ) {
    var type = getType();
    if (type == null) {
      return;
    }
    if (!containsPlaceholder(type)) {
      return;
    }
    doRegisterAction(context, getActionClass(actionContext));
  }

  private void doRegisterAction(
    GenericApplicationContext context,
    Class<? extends Action> actionClass
  ) {
    var actions = context.getBeansOfType(actionClass);
    if (!actions.isEmpty()) {
      return;
    }
    context.registerBean(actionClass);
  }

  public void registerRequestMapping(
    RequestMappingHandlerMapping mappings,
    ServletActionContext actionContext,
    RequestProcessor requestProcessor,
    JspForwardingHandler jspResourceHandler,
    GenericApplicationContext applicationContext
  ) {
    registerAction(applicationContext);
    var actionUrl = getActionUrl().replaceAll("\\*", "{param:[-_a-zA-Z0-9]+}");
    var mapping = RequestMappingInfo.paths(actionUrl, actionUrl + ".do")
      .methods(GET, POST)
      .produces(DEFAULT_RESPONSE_CONTENT_TYPES)
      .build();

    mappings.registerMapping(
      mapping,
      new StrutsRequestHandler(
        this,
        requireNonNull(moduleConfig),
        actionContext,
        requestProcessor,
        jspResourceHandler,
        applicationContext
      ),
      StrutsRequestHandler.HANDLE_REQUEST
    );
  }

  private static final String[] DEFAULT_RESPONSE_CONTENT_TYPES = new String[] {
    "text/html",
    "application/xhtml+xml",
    "application/xml",
    "text/plain",
  };

  /**
   * Get the scope ("request" or "session") within which our form bean is
   * accessed, if any.
   */
  public String getScope() {
    return requireNonNull(
      getInheritedProperty(String.class, it -> {
        if (scope == null) {
          return "request";
        }
        var isValidScope = "request".equals(scope) || "session".equals(scope);
        if (!isValidScope) throw new IllegalArgumentException(
          "Invalid scope name: " + scope
        );
        return scope;
      })
    );
  }

  @JacksonXmlProperty(localName = "scope", isAttribute = true)
  private @Nullable String scope;

  /**
   * Should the `validate()` method of the form bean associated with this
   * action be called?
   */
  public boolean getValidate() {
    return validate != null && validate;
  }

  @JacksonXmlProperty(localName = "validate", isAttribute = true)
  private @Nullable Boolean validate;

  /**
   * Context-relative path of the input form to which control should be
   * returned if a validation error is encountered. Required if "name" is
   * specified and the input bean returns validation errors.
   */
  public @Nullable String getInput() {
    return getInheritedProperty(String.class, it -> it.input);
  }

  @JacksonXmlProperty(localName = "input", isAttribute = true)
  private @Nullable String input;

  /**
   * Returns the request-scope or session-scope attribute name under which our
   * form bean is accessed, if it is different from the form bean's specified
   * `name`.
   */
  public @Nullable String getAttribute() {
    return attribute == null ? name : attribute;
  }

  @JacksonXmlProperty(localName = "attribute", isAttribute = true)
  private @Nullable String attribute;

  /**
   * Can this Action be cancelled? [false] By default, when an Action is
   * cancelled, validation is bypassed and the Action should not execute the
   * business operation. If a request tries to cancel an Action when
   * cancellable is not set, a "InvalidCancelException" is thrown.
   */
  @JacksonXmlProperty(localName = "cancellable", isAttribute = true)
  public boolean isCancellable() {
    return cancellable != null && cancellable;
  }

  private @Nullable Boolean cancellable;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private void setProperties(List<PropertyConfig> properties) {
    for (var property : properties) {
      property.applyTo(this);
    }
  }

  /**
   * Prepares an `ActionForm` by retrieving or creating a new one based on
   * the current HTTP request.
   * @throws ResponseStatusException if binding the request parameters to
   *   the form bean fails, with a {@code BAD_REQUEST} status.
   */
  public @Nullable ActionForm prepareForm(HttpServletRequest request) {
    var formBeanConfig = getFormBeanConfig(request);
    if (formBeanConfig == null) {
      return getCurrentForm(request);
    }
    var formBeanName = formBeanConfig.getName();
    var formBeanScope = getScope();
    var formBean = requireNonNullElseGet(
      findForm(request, formBeanName, formBeanScope),
      formBeanConfig::createActionForm
    );
    saveForm(request, formBeanName, formBeanScope, formBean);
    try {
      bindRequest(request, formBean);
    } catch (BindException e) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        format(
          "Failed to bind the parameters of the request for [%s] to the form bean [%s].",
          getActionUrl(),
          getName(request)
        ),
        e
      );
    }
    return formBean;
  }

  private @Nullable ActionForm findForm(
    HttpServletRequest request,
    String name,
    String scope
  ) {
    if (scope.equals("session")) {
      return (ActionForm) request.getSession().getAttribute(name);
    }
    return (ActionForm) request.getAttribute(name);
  }

  private void saveForm(
    HttpServletRequest request,
    String name,
    String scope,
    ActionForm bean
  ) {
    request.setAttribute(Constants.BEAN_KEY, name);
    if (scope.equals("session")) {
      request.getSession().setAttribute(name, bean);
      return;
    }
    request.setAttribute(name, bean);
  }

  private @Nullable ActionForm getCurrentForm(HttpServletRequest request) {
    var formBeanKey = (String) request.getAttribute(Constants.BEAN_KEY);
    if (!hasText(formBeanKey)) {
      return null;
    }
    var bean = (ActionForm) request.getSession().getAttribute(formBeanKey);
    if (bean != null) {
      return bean;
    }
    return (ActionForm) request.getAttribute(formBeanKey);
  }
}
