package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.InputTag;
import springing.struts1.taglib.DelegatingHtmlInputElementTagBase;

/**
 * Render a Cancel Button
 * Renders an HTML `<input>` element of type submit. This tag is only valid
 * when nested inside a form tag body. Pressing of this submit button causes
 * the action servlet to bypass calling the associated form bean validate()
 * method. The action is called normally.
 */
public class CancelTag extends DelegatingHtmlInputElementTagBase<InputTag> {

  public CancelTag() {
    super(new InputTag() {
      @Override
      protected String getType() {
        return "submit";
      }
    });
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  public void init() {
    setProperty(Constants.CANCEL_PROPERTY);
    value = null;
  }

  private @Nullable String value;

  /**
   * Value of the label to be placed on this button. This value will also be
   * submitted as the value of the specified request parameter.
   * [Body of this tag (if any), or "Cancel"]
   */
  @Override
  public void setValue(String value ) {
    this.value = value;
  }

  @Override
  protected String processValue(@Nullable Object v) {
    return (value == null) ? DEFAULT_CANCEL_BUTTON_LABEL : value;
  }
  private static final String DEFAULT_CANCEL_BUTTON_LABEL = "Cancel";
}
