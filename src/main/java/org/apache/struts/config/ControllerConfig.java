package org.apache.struts.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.chain.ComposableRequestProcessor;
import org.springframework.lang.Nullable;
import java.util.List;
import static springing.util.ObjectUtils.classFor;
import static springing.util.ObjectUtils.createInstanceOf;

/**
 * A JavaBean representing the configuration information of a `controller`
 * element in a Struts configuration file.
 */
public class ControllerConfig {

  public RequestProcessor createInstance() {
    return createInstanceOf(getProcessorClass());
  }

  /**
   * Should the `input` property of `ActionConfig` instances associated with
   * this module be treated as the name of a corresponding `ForwardConfig`. A
   * `false` value treats them as a module-relative path (consistent with the
   * hard coded behavior of earlier versions of Struts).
   */
  public boolean getInputForward() {
    return inputForward;
  }
  @JacksonXmlProperty(localName = "inputForward", isAttribute = true)
  private boolean inputForward = false;

  /**
   * The replacement pattern used to determine a context-relative URL from the
   * `page` attribute of Struts tags and configuration properties. The pattern
   * may consist of any combination of the following markers and characters:
   * - `$M`
   *   Replaced by the module prefix for the current module.
   * - `$P`
   *   The `page` attribute value being evaluated.
   * - `$$`
   *   Renders a literal dollar sign ("$") character in the resulting URL.
   * - A dollar sign followed by any other character is reserved for future use,
   *   and both characters are silently swallowed.
   * - All other characters in the pattern are passed through unchanged.
   * If this property is set to `null`, a default pattern of `$M$P` is
   * utilized, which is backwards compatible with the hard coded functionality
   * in prior versions.
   */
  @JacksonXmlProperty(localName = "pagePattern", isAttribute = true)
  private @Nullable String pagePattern;

  /**
   * The fully qualified class name of the RequestProcessor implementation
   * class to be used for this module.
   */
  @JacksonXmlProperty(localName = "processorClass", isAttribute = true)
  public void setProcessorClass(String processorClassName) {
    processorClass = classFor(processorClassName);
  }
  public String getProcessorClass() {
    return processorClass.getName();
  }
  private Class<? extends RequestProcessor> processorClass = DEFAULT_REQUEST_PROCESSOR;

  private static final Class<? extends RequestProcessor>
    DEFAULT_REQUEST_PROCESSOR = ComposableRequestProcessor.class;

  @JacksonXmlProperty(localName = "maxFileSize", isAttribute = true)
  private @Nullable String maxFileSize;

  @JacksonXmlProperty(localName = "set-property")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setProperties(List<ControllerProperty> properties) {
    for (var property : properties) {
      var name = property.property;
      var value = property.value;
      if (name.equals("inputForward")) {
        inputForward = Boolean.parseBoolean(value);
      }
      else if (name.equals("processorClass")) {
        setProcessorClass(value);
      }
    }
  }

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
