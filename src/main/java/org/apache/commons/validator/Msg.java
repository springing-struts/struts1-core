package org.apache.commons.validator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

/**
 * An alternative message can be associated with a Field and a pluggable
 * validator instead of using the default message stored in the ValidatorAction
 * (aka pluggable validator). Instances of this class are configured with a
 * `msg` xml element.
 */
public class Msg {
  public Msg(
    @JacksonXmlProperty(isAttribute = true, localName = "key") String key
  ) {
    this.key = key;
  }
  private final String key;

  /**
   * The resource bundle name that this Msg's key should be resolved in
   * (optional).
   */
  @JacksonXmlProperty(isAttribute = true, localName = "bundle")
  private @Nullable String bundle;

  /**
   * The name dependency that this argument goes with (optional).
   */
  @JacksonXmlProperty(isAttribute = true, localName = "name")
  private @Nullable String name;

  /**
   * Whether the key is a message resource (optional).
   */
  @JacksonXmlProperty(isAttribute = true, localName = "resource")
  private boolean resource = false;
}
