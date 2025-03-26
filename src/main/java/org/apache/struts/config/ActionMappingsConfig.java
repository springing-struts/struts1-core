package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.ArrayList;
import java.util.List;
import org.apache.struts.action.ActionMapping;
import org.springframework.lang.Nullable;

/**
 * The "action-mappings" element describes a set of ActionMapping objects that
 * are available to process requests matching the url-pattern our ActionServlet
 * registered with the container. The individual ActionMappings are configured
 * through nested `action` elements.
 */
public class ActionMappingsConfig {

  public ActionMappingsConfig(
    @JacksonXmlProperty(
      localName = "type",
      isAttribute = true
    ) @Nullable String type
  ) throws ClassNotFoundException {
    this.type = type;
    actionMappingClass = (type == null)
      ? ActionMapping.class
      : (Class<? extends ActionMapping>) Class.forName(type);
  }

  /**
   * Fully qualified Java class to use when instantiating ActionMapping objects.
   * If specified, the object must be a subclass of the default class type.
   */
  public @Nullable String getType() {
    return type;
  }

  private final @Nullable String type;

  public Class<? extends ActionMapping> getActionMappingClass() {
    return actionMappingClass;
  }

  private final Class<? extends ActionMapping> actionMappingClass;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "action")
  @JsonManagedReference
  public void setEntries(List<ActionConfig> entries) {
    this.entries = entries.reversed();
  }

  private List<ActionConfig> entries = new ArrayList<>();

  public List<ActionConfig> getEntries() {
    return entries;
  }
}
