package org.apache.struts.config;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.ActionMapping;
import org.springframework.lang.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A JavaBean representing the configuration information of an `<action />`
 * element from a Struts module configuration file.
 */
public class ActionConfig {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public ActionMapping createActionMapping() {
    var config = MAPPER.convertValue(this, Map.class);
    for (var prop : properties) {
      config.put(prop.getProperty(), prop.getValue());
    }
    return new ObjectMapper().convertValue(config, container.getActionMappingClass());
  }

  @JsonBackReference
  private ActionMappingsConfig container;

  private StrutsConfig getStrutsConfig() {
    return container.getStrutsConfig();
  }

  /**
   * Return context-relative path of the submitted request, starting with
   * a slash ("/") character, and omitting any filename extension if extension
   * mapping is being used.
   */
  public String getPath() {
    return path;
  }
  public void setPath(String path) {
    this.path = ("/" + path).replaceAll("//+", "/");
  }
  @JacksonXmlProperty(localName = "path", isAttribute = true)
  private String path;


  /**
   * Return name of the form bean, if any, associated with this Action.
   */
  public @Nullable String getName() {
    return name;
  }
  @JacksonXmlProperty(localName = "name", isAttribute = true)
  private @Nullable String name;

  /**
   * Returns a FormBeanConfig associated with this Action.
   */
  @JsonIgnore
  public @Nullable FormBeanConfig getFormBeanConfig() {
    if (name == null) return null;
    for (var formBeanConfig : getStrutsConfig().getFormBeanConfigs()) {
      if (name.equals(formBeanConfig.getName())) {
        return formBeanConfig;
      }
    }
    throw new IllegalStateException(
      "Unknown form bean name [" + name + "] is referred from the action [" + type + "]."
    );
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
    for (var forward : getStrutsConfig().getGlobalForwards()) {
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

  public List<ForwardConfig> getLocalForwards() {
    if (localForwards == null) {
      return new ArrayList<>();
    }
    return localForwards;
  }

  @JacksonXmlProperty(localName = "forward")
  @JacksonXmlElementWrapper(useWrapping = false)
  private @Nullable List<ForwardConfig> localForwards;

  /**
   * Return all forward configurations for this module.  If there are none, a
   * zero-length array is returned.
   */
  public ForwardConfig[] findForwardConfigs() {
    return new ForwardConfig[0];
  }

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

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private List<PropertyConfig> properties;
}
