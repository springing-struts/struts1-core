package org.apache.commons.validator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;

/**
 * A variable that can be associated with a Field for passing in information
 * to a pluggable validator. Instances of this class are configured with a
 * `var` xml element.
 */
public class Var {
  public Var(
    @JacksonXmlProperty(localName = "var-name") String name,
    @JacksonXmlProperty(localName = "var-value") String value,
    @JacksonXmlProperty(localName = "resource", isAttribute = true) @Nullable Boolean resource
  ) {
    this.name = name;
    this.value = value;
    this.resource = resource == null ? false : true;
  }

  @JsonBackReference
  private Field field;

  /**
   * The name of the variable.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The value of the variable.
   */
  public String getValue() {
    return value;
  }
  private final String value;

  public String getVarValue() {
    if (!resource) {
      return field.interpolate(value);
    }
    var basename = (bundle != null) ? bundle : ModuleUtils.getCurrent().getMessageResourcesBasename();
    var messageResource = MessageResources.getMessageResources(basename);
    return messageResource.getMessage(value);
  }
  /**
   * Whether the value is a resource key or literal value.
   */
  private boolean resource = false;

  /**
   * The resource bundle name.
   */
  @JacksonXmlProperty(isAttribute = true, localName = "bundle")
  private @Nullable String bundle;
}
