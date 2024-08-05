package org.apache.struts.config;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static springing.util.StringUtils.normalizeForwardPath;

/**
 * A JavaBean representing the configuration information of an `<action />`
 * element from a Struts module configuration file.
 */
public class ActionConfig {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public ActionMapping createActionMapping() {
    try {
      var clazz = container.getActionMappingClass();
      var constructor = clazz.getDeclaredConstructor();
      constructor.setAccessible(true);
      ActionConfig actionMapping = constructor.newInstance();
      actionMapping.path = path;
      actionMapping.name = name;
      actionMapping.actionId = actionId;
      actionMapping.parameter = parameter;
      actionMapping.forwardPath = forwardPath;
      actionMapping.attribute = attribute;
      actionMapping.scope = scope;
      actionMapping.cancellable = cancellable;
      actionMapping.input = input;
      actionMapping.type = type;
      actionMapping.validate = validate;
      actionMapping.localForwards = localForwards;
      actionMapping.localExceptionHandlers = localExceptionHandlers;
      actionMapping.container = container;
      actionMapping.moduleConfig = moduleConfig;
      for (var p : properties) {
        var property = BeanUtils.getPropertyDescriptor(clazz, p.getProperty());
        if (property == null) throw new IllegalArgumentException(String.format(
          "Unknown property %s of the action mapping class [%s].", p.getProperty(), type
        ));
        var setter = property.getWriteMethod();
        setter.invoke(actionMapping, p.getValue());
      }
      return (ActionMapping) actionMapping;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @JsonBackReference
  private ActionMappingsConfig container;

  public ModuleConfig getModuleConfig() {
    return moduleConfig;
  }
  void setModuleConfig(ModuleConfig config) {
    moduleConfig = config;
  }
  private ModuleConfig moduleConfig;

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
    throw new IllegalStateException(String.format(
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

  @JsonIgnore
  public @Nullable String getForwardUrl() {
    if (forwardPath == null) return null;
    return normalizeForwardPath("/" + getModuleConfig().getPrefix() + "/" + forwardPath);
  }

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

  /**
   * Get the scope ("request" or "session") within which our form bean is
   * accessed, if any.
   */
  public String getScope() {
    if (scope == null) return "request";
    var isValidScope = "request".equals(scope) || "session".equals(scope);
    if (isValidScope)  return scope;
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
  private List<PropertyConfig> properties = new ArrayList<>();
}
