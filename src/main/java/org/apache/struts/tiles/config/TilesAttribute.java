package org.apache.struts.tiles.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

/**
 *  The "put" element describes an attribute of a definition. It allows to
 *  specify the tiles attribute name and its value. The tiles value can be
 *  specified as an xml attribute, or in the body of the `put` tag.
 */
public class TilesAttribute {
  public TilesAttribute(
    @JacksonXmlProperty(isAttribute = true, localName = "name") @JsonAlias("content") String name,
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

  /**
   * The type of the value. Can be: string, page, template or definition.
   * By default, no type is associated to a value. If a type is associated, it
   * will be used as a hint to process the value when the attribute will be
   * used in the inserted tiles.
   */
  public @Nullable String getType() {
    return type;
  }

  @JacksonXmlProperty(isAttribute = true, localName = "type")
  private @Nullable String type;

  /**
   * Same as type="string". For compatibility with the template tag library.
   */
  public void setDirect(boolean direct) {
    if (direct) {
      this.type = "string";
    }
  }
}
