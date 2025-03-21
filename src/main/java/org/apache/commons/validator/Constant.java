package org.apache.commons.validator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Constant {
  public Constant(
    @JacksonXmlProperty(isAttribute = true, localName = "constant-name") String name,
    @JacksonXmlProperty(isAttribute = true, localName = "constant-value") String value)
  {
    this.name = name;
    this.value = value;
  };

  public String getName() {
    return name;
  }
  private final String name;

  public String getValue() {
    return value;
  }
  private final String value;
}