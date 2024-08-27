package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.InputTag;
import springing.struts1.taglib.DelegatingHtmlInputElementTagBase;
import springing.struts1.taglib.HtmlElementTagBase;

import java.util.Map;

/**
 * Render a Cancel Button
 * Renders an HTML `<input>` element of type submit. This tag is only valid
 * when nested inside a form tag body. Pressing of this submit button causes
 * the action servlet to bypass calling the associated form bean validate()
 * method. The action is called normally.
 */
public class CancelTag extends HtmlElementTagBase {

  public CancelTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  public void init() {
    value = null;
  }
  private @Nullable String value;

  @Override
  protected String getTagName() {
    return "input";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    return Map.of(
      "type", "submit",
      "name", Constants.CANCEL_PROPERTY,
      "value", (value == null) ? DEFAULT_CANCEL_BUTTON_LABEL : value
    );
  }
  private static final String DEFAULT_CANCEL_BUTTON_LABEL = "Cancel";

  /**
   * Value of the label to be placed on this button. This value will also be
   * submitted as the value of the specified request parameter.
   * [Body of this tag (if any), or "Cancel"]
   */
  public void setValue(String value) {
    this.value = value;
  }
}
