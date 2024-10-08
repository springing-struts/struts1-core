package org.apache.struts.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A JavaBean representing the configuration information of a `<controller>`
 * element in a Struts configuration file.
 */
public class ControllerConfig {

  @JacksonXmlProperty(localName = "maxFileSize", isAttribute = true)
  private @Nullable String maxFileSize;

  /**
   * Should the `input` property of `ActionConfig` instances associated with
   * this module be treated as the name of a corresponding `ForwardConfig`.
   * A `false` value treats them as a module-relative path (consistent with the
   * hard coded behavior of earlier versions of Struts.
   */
  @JacksonXmlProperty(localName = "inputForward", isAttribute = true)
  private @Nullable Boolean inputForward;

  @JacksonXmlProperty(localName = "set-property")
  @JacksonXmlElementWrapper(useWrapping = false)
  private final List<ControllerProperty> properties = new ArrayList<>();

  public static class ControllerProperty {
    public ControllerProperty(
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
