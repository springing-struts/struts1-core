package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static springing.util.StringUtils.normalizeForwardPath;

/**
 * A JavaBean representing the configuration information of a `forward`
 * element from a Struts configuration file.
 */
public class ForwardConfig {

  @JsonCreator()
  public ForwardConfig(
    @JacksonXmlProperty(localName = "name", isAttribute = true) @JsonProperty("name") String name,
    @JacksonXmlProperty(localName = "path", isAttribute = true) @JsonProperty("path") String path
  ) {
    this.name = name;
    this.path = path;
  }

  /**
   * The unique identifier of this forward, which is used to reference it in
   * `Action` classes.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The URL to which this `ForwardConfig` entry points, which must start with
   * a slash ("/") character. It is interpreted according to the following
   * rules:
   * - If `contextRelative` property is `true`, the path is considered to be
   *   context-relative within the current web application (even if we are in a
   *   named module). It will be prefixed by the context path to create a
   *   server-relative URL.
   * - If the `contextRelative` property is false, the path is considered to be
   *   the module-relative portion of the URL. It will be used as the
   *   replacement for the `$P` marker in the `forwardPattern` property defined
   *   on the `ControllerConfig` element for our current module. For the
   *   default `forwardPattern` value of `$C$M$P` the resulting server-relative
   *   URL will be the concatenation of the context path, the module prefix,
   *   and the `path` from this `ForwardConfig`.
   */
  public String getPath() {
    return path;
  }
  private final String path;

  /**
   * Returns the URL targeted by this forward, relative to the servlet context.
   */
  public String getUrl() {
    var path =  isAbsolute()
      ? getPath()
      : normalizeForwardPath(getModulePath() + "/" + getPath());
    return ServletActionContext.current().interpolatePathParams(path);
  }

  private boolean isAbsolute() {
    return containsScheme();
  }

  private boolean containsScheme() {
    return URL_SCHEME.matcher(getPath()).matches();
  }
  private static final Pattern URL_SCHEME = Pattern.compile(
    "^[_a-zA-Z0-9]+://.*$"
  );

  private String getModulePath() {
    if (modulePrefix != null) {
      return modulePrefix;
    }
    if (actionConfig != null) {
      return actionConfig.getModuleConfig().getPrefix();
    }
    if (moduleConfig != null) {
      return moduleConfig.getPrefix();
    }
    return ModuleUtils.getCurrent().getPrefix();
  }

  /**
   * Should a redirect be used to transfer control to the specified path?
   */
  public boolean getRedirect() {
    return redirect != null && redirect;
  }
  @JacksonXmlProperty(localName = "redirect", isAttribute = true)
  private @Nullable Boolean redirect;

  /**
   * The prefix of the module to which this `ForwardConfig` entry points, which
   * must start with a slash ("/") character.
   * **Usage note:**
   * If a forward config is used in a hyperlink, and a module is specified, the
   * path must lead to another action and not directly to a page. This is in
   * keeping with rule that in a modular application all links must be to an
   * action rather than a page.
   */
  public String getModule() {
    return modulePrefix == null ? "" : modulePrefix;
  }
  @JacksonXmlProperty(localName = "module", isAttribute = true)
  private @Nullable String modulePrefix;

  /**
   * Set an action-config containing this forward definition.
   * This will not be set if it is a global forward definition.
   */
  public void setActionConfig(ActionConfig actionConfig) {
    this.actionConfig = actionConfig;
  }
  private @Nullable ActionConfig actionConfig;

  /**
   * Set a module-config containing this forward definition.
   * This will not be set if it is an action-mapping local forward definition.
   */
  public void setModuleConfig(ModuleConfig moduleConfig) {
    this.moduleConfig = moduleConfig;
  }
  private @Nullable ModuleConfig moduleConfig;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private List<PropertyConfig> properties = new ArrayList<>();
}
