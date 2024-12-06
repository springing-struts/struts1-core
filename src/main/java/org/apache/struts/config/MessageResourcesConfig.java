package org.apache.struts.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;
import springing.struts1.message.ResourceBundleMessageResources;

import java.util.List;

public class MessageResourcesConfig {

  public MessageResourcesConfig(
    @JacksonXmlProperty(localName = "parameter", isAttribute = true)
    String parameter,
    @JacksonXmlProperty(localName = "key", isAttribute = true)
    @Nullable String key
  ) {
    this.parameter = parameter;
    this.key = key == null ? "" : key;
  }

  public boolean isDefault() {
    return key.isBlank();
  }

  /**
   * The servlet context attributes key under which this MessageResources
   * instance is stored.
   */
  public String getKey() {
    return key;
  }
  private final String key;

  /**
   * The configuration parameter used to initialize this MessageResources.
   */
  public String getConfig() {
    return parameter;
  }
  private final String parameter;

  /**
   * Indicates that a `null` is returned instead of an error message string if
   * an unknown Locale or key is requested.
   */
  public boolean getReturnNull() {
    return returnsNull != null && returnsNull;
  }
  @JacksonXmlProperty(localName = "null", isAttribute = true)
  private @Nullable Boolean returnsNull;

  /**
   * Returns or creates a Struts `MessageResource` based on this configuration.
   */
  public ResourceBundleMessageResources toMessageResources() {
    return ResourceBundleMessageResources.load(getConfig());
  }

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private @Nullable List<PropertyConfig> properties;
}
