package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class PluginConfig {
  public PluginConfig(
    @JacksonXmlProperty(localName = "className", isAttribute = true) String className
  ) {
    this.className = className;
  }
  private final String className;

  @JsonBackReference
  private StrutsConfig config;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private List<PluginProperty> properties;

  public static class PluginProperty {
    public PluginProperty(
      @JacksonXmlProperty(localName = "property", isAttribute = true) String property,
      @JacksonXmlProperty(localName = "value", isAttribute = true) String value
    ) {
      this.property = property;
      this.value = value;
    }
    private final String property;
    private final String value;
  }
}
