package org.apache.struts.tiles.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 *  The "put" element describes an attribute of a definition. It allows to
 *  specify the tiles attribute name and its value. The tiles value can be
 *  specified as an xml attribute, or in the body of the `put` tag.
 */
public class TilesAttribute {
  public TilesAttribute(
    @JacksonXmlProperty(isAttribute = true, localName = "name") String name,
    @JacksonXmlProperty(isAttribute = true, localName = "value") String value
  ) {
    this.name = name;
    this.value = value;
  }

  /**
   * The unique identifier for this attribute.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The value associated to this tiles attribute. The value should be
   * specified with this tag attribute, or in the body of the tag.
   */
  public String getValue() {
    return value;
  }
  private final String value;
}
