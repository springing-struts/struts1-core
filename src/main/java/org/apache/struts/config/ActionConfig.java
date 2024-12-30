package org.apache.struts.config;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.*;
import org.apache.struts.taglib.html.Constants;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springing.struts1.configuration.StrutsConfiguration;
import springing.struts1.controller.StrutsRequestContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.requireNonNullElseGet;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static springing.struts1.validator.ValidationUtils.bindRequest;
import static springing.util.ObjectUtils.classFor;
import static springing.util.StringUtils.normalizeForwardPath;

/**
 * A JavaBean representing the configuration information of an `<action />`
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
    return moduleConfig;
  }
  void setModuleConfig(ModuleConfigBean config) {
    moduleConfig = config;
  }
  private ModuleConfigBean moduleConfig;

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
    return normalizeForwardPath("/" + getModuleConfig().getPrefix() + "/" + path);
  }

  /**
   * Return name of the form bean, if any, associated with this Action.
   */
  public @Nullable String getName() {
    return name;
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
   * Returns a FormBeanConfig associated with this Action.
   */
  @JsonIgnore
  public @Nullable FormBeanConfig getFormBeanConfig() {
    if (name == null) return null;
    for (var formBeanConfig : getModuleConfig().getFormBeanConfigs()) {
      if (name.equals(formBeanConfig.getName())) {
        return formBeanConfig;
      }
    }
    throw new IllegalStateException(format(
      "Unknown form bean name [%s] is referred from the action [%s]." +
      " Please check the configuration files at [%s].",
      name, path, getModuleConfig().getConfigFilePaths()
    ));
  }

  /**
   * Return general purpose configuration parameter that can be used to pass
   * extra information to the Action instance selected by this Action.
   * Struts does not itself use this value in any way.
   */
  public @Nullable String getParameter() {
    return parameter;
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
  @JacksonXmlProperty(localName = "forward-path", isAttribute = true)
  private @Nullable String forwardPath;

  /**
   * Returns context-relative path of the web application resource that will
   * process this request.
   */
  public @Nullable String getInclude() {
    return include;
  }
  @JacksonXmlProperty(localName = "include", isAttribute = true)
  private @Nullable String include;

  public List<ActionForward> getLocalForwards() {
    return localForwards;
  }
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
    return type;
  }
  @JacksonXmlProperty(localName = "type", isAttribute = true)
  private @Nullable String type;

  public boolean hasAction() {
    return hasText(type);
  }

  public Class<? extends Action> getActionClass() {
    if (!hasAction()) throw new IllegalStateException(format(
      "Failed to determine action class for action mapping [%s].",
      path
    ));
    if (actionClass == null) {
     actionClass = classFor(type);
    }
    return actionClass;
  }
  private @Nullable Class<? extends Action> actionClass;

  public void registerAction(GenericApplicationContext context) {
    if (!hasAction()) {
      return;
    }
    var actionClass = getActionClass();
    var actions = context.getBeansOfType(actionClass);
    if (!actions.isEmpty()) {
      return;
    }
    context.registerBean(actionClass);
  }

  public void registerMapping(RequestMappingHandlerMapping mappings, RequestProcessor requestProcessor) {
    var actionUrl = getActionUrl();
    var mapping = RequestMappingInfo
      .paths(actionUrl, actionUrl + ".do")
      .methods(GET, POST)
      .produces(DEFAULT_RESPONSE_CONTENT_TYPES)
      .build();
    if (!(this instanceof ActionMapping actionMapping)) throw new IllegalStateException(format(
      "The actionConfig [%s] should be an instance of [%s].",
      getName(), ActionMapping.class.getName()
    ));
    mappings.registerMapping(mapping, (StrutsRequestProcessing) (request, response) -> {
      StrutsRequestContext.setActionMapping(actionMapping);
      requestProcessor.process(
        HttpServletRequest.toJavaxNamespace(request),
        HttpServletResponse.toJavaxNamespace(response)
      );
    }, PROCESS_METHOD);
  }

  private static final String[] DEFAULT_RESPONSE_CONTENT_TYPES = new String[] {
    "text/html", "application/xhtml+xml", "application/xml", "text/plain"
  };

  @FunctionalInterface
  private interface StrutsRequestProcessing {
    void process(
      jakarta.servlet.http.HttpServletRequest request,
      jakarta.servlet.http.HttpServletResponse response
    ) throws ServletException, IOException;
  }
  public static final Method PROCESS_METHOD;
  static {
    try {
      PROCESS_METHOD = StrutsRequestProcessing.class.getDeclaredMethod(
        "process",
        jakarta.servlet.http.HttpServletRequest.class,
        jakarta.servlet.http.HttpServletResponse.class
      );
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get the scope ("request" or "session") within which our form bean is
   * accessed, if any.
   */
  public String getScope() {
    if (scope == null) {
      return "request";
    }
    var isValidScope = "request".equals(scope) || "session".equals(scope);
    if (isValidScope) {
      return scope;
    }
    throw new IllegalArgumentException(
      "Invalid scope name: "  + scope
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
    return input;
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
    var formBeanConfig = getFormBeanConfig();
    if (formBeanConfig == null) {
      return getCurrentForm(request);
    }
    var formBeanName = formBeanConfig.getName();
    var formBeanScope = getScope();
    var formBean = requireNonNullElseGet(
      findForm(request, formBeanName, formBeanScope),
      formBeanConfig::createActionForm
    );
    try {
      bindRequest(request, formBean);
    } catch (BindException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, format(
        "Failed to bind the parameters of the request for [%s] to the form bean [%s].",
        getActionUrl(), getName()
      ), e);
    }
    saveForm(request, formBeanName, formBeanScope, formBean);
    return formBean;
  }

  private @Nullable ActionForm findForm(HttpServletRequest request, String name, String scope) {
    if (scope.equals("session")) {
      return (ActionForm) request.getSession().getAttribute(name);
    }
    return (ActionForm) request.getAttribute(name);
  }

  private void saveForm(HttpServletRequest request, String name, String scope, ActionForm bean) {
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
