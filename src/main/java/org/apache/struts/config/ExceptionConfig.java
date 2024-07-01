package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ExceptionConfig {
  public ExceptionConfig(
    @JacksonXmlProperty(localName = "key", isAttribute = true) String key,
    @JacksonXmlProperty(localName = "type", isAttribute = true) String type
  ) {
    this.key = key;
    this.type = type;
  }

  private final String key;
  private final String type;

  @JsonBackReference
  private StrutsConfig config;

}
