package org.apache.struts.action;

import java.io.Serializable;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;

/**
 * An encapsulation of an individual message returned by the `validate`
 * method of an `ActionForm`, consisting of a message key (to be used to look
 * up message text in an appropriate message resources database) plus up to
 * four placeholder objects that can be used for parametric replacement in
 * the message text.
 */
public class ActionMessage implements Serializable {

  /**
   * Construct an action message with the specified replacement values.
   */
  public ActionMessage(String key, Object... values) {
    this.key = key;
    this.values = values;
    this.resource = true;
    this.bundle = null;
  }

  /**
   *  Construct an action message with no replacement values.
   */
  public ActionMessage(String key, boolean resource) {
    this.key = key;
    this.values = new Object[] {};
    this.resource = resource;
    this.bundle = null;
  }

  /**
   * Get the message key for this message.
   */
  public String getKey() {
    return key;
  }

  private final String key;

  /**
   * Get the replacement values for this message.
   */
  public Object[] getValues() {
    return values;
  }

  private final Object[] values;

  /**
   * Indicate whether the key is taken to be as a bundle key [true] or literal
   * value [false].
   */
  public boolean isResource() {
    return resource;
  }

  private final boolean resource;

  private @Nullable String bundle;

  public void setBundle(@Nullable String bundle) {
    this.bundle = bundle;
  }

  /**
   * Returns the content of this message.
   */
  public String getText(@Nullable String bundle) {
    var resources = ModuleUtils.getCurrent()
      .getMessageResources((bundle != null) ? bundle : this.bundle);
    return getText(resources);
  }

  public String getText(MessageResources resources) {
    if (!isResource()) {
      return getKey();
    }
    return resources.requireMessage(getKey(), getValues());
  }
}
