package org.apache.commons.validator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;

/**
 * A default argument or an argument for a specific validator definition
 * (ex: required) can be stored to pass into a message as parameters. This can
 * be used in a pluggable validator for constructing locale sensitive messages
 * by using java.text.MessageFormat or an equivalent class. The resource field
 * can be used to determine if the value stored in the argument is a value to
 * be retrieved from a locale sensitive message retrieval system like
 * `java.util.PropertyResourceBundle`. The resource field defaults to 'true'.
 * Instances of this class are configured with an `arg` xml element.
 */
public class Arg {

  public Arg(
    @JacksonXmlProperty(isAttribute = true, localName = "name") @Nullable String name,
    @JacksonXmlProperty(isAttribute = true, localName = "key") String key,
    @JacksonXmlProperty(isAttribute = true, localName = "resource") @Nullable Boolean resource
  ) {
    this.name = (name == null) ? "" : name;
    this.key = key;
    this.resource = resource == null || resource;
  }

  @JsonBackReference
  private Field field;

  /**
   * The name dependency that this argument goes with (optional).
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The key or value of the argument.
   */
  public String getKey() {
    return key;
  }
  private final String key;

  /**
   * This argument's position in the message.
   */
  @JacksonXmlProperty(isAttribute = true, localName = "position")
  public void setPosition(int position) {
    this.position = position;
  }
  public int getPosition() {
    return position;
  }
  private int position = 0;

  /**
   * Whether the key is a message resource (optional).
   * Defaults to true. If it is 'true', the value will try to be resolved as a
   * message resource.
   */
  public boolean isResource() {
    return resource;
  }
  private final boolean resource;

  /**
   * The resource bundle name that this Arg's key should be resolved in
   * (optional).
   */
  public String getBundle() {
    return bundle;
  }
  @JacksonXmlProperty(isAttribute = true, localName = "bundle")
  private String bundle = "";

  public @Nullable String getText() {
    if (!resource) {
      return field.interpolate(key);
    }
    return ModuleUtils
      .getCurrent()
      .getMessageResources(getBundle())
      .getMessage(key);
  }
}
