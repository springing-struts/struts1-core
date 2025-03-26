package org.apache.struts.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

/**
 * A JavaBean representing the configuration information of an `exception`
 * element from a Struts configuration file.
 */
public class ExceptionConfig {

  public ExceptionConfig(
    @JacksonXmlProperty(localName = "key", isAttribute = true) String key,
    @JacksonXmlProperty(localName = "type", isAttribute = true) String type
  ) {
    this.key = key;
    this.type = type;
  }

  /**
   * The message resources key specifying the error message associated with
   * this exception.
   */
  public String getKey() {
    return key;
  }

  private final String key;

  /**
   * The fully qualified Java class name of the exception that is to be handled
   * by this handler.
   */
  public String getType() {
    return type;
  }

  private final String type;

  /**
   * The module-relative path of the resource to forward to if this exception
   * occurs during an `Action`.
   */
  public @Nullable String getPath() {
    return path;
  }

  @JacksonXmlProperty(localName = "path", isAttribute = true)
  private @Nullable String path;
}
