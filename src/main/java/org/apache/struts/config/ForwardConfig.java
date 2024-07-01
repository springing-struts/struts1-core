package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
/**
 * A JavaBean representing the configuration information of a `<forward>`
 * element from a Struts configuration file.
 */
public class ForwardConfig {

  public ForwardConfig(
    @JacksonXmlProperty(localName = "name", isAttribute = true) @Nullable String name,
    @JacksonXmlProperty(localName = "path", isAttribute = true) String path,
    @JacksonXmlProperty(localName = "redirect", isAttribute = true) @Nullable Boolean redirect
  ) {
    this.name = name;
    this.path = path;
    this.redirect = redirect != null && redirect;
  }

  /**
   * The unique identifier of this forward, which is used to reference it in
   * `Action` classes.
   */
  public @Nullable String getName() {
    return name;
  }
  private final @Nullable String name;

  /**
   * The URL to which this `ForwardConfig` entry points, which* must start with
   * a slash ("/") character. It is interpreted according to the following
   * rules:
   * - If `contextRelative<` property is `true`, the path is considered to be
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
   * Should a redirect be used to transfer control to the specified path?
   */
  public boolean getRedirect() {
    return redirect;
  }
  private final boolean redirect;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private List<PropertyConfig> properties = new ArrayList<>();
}
