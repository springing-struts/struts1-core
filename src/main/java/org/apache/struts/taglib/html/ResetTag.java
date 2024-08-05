package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.InputTag;
import springing.struts1.taglib.DelegatingHtmlInputElementTagBase;

/**
 * Renders an HTML <input> element of type reset.
 */
public class ResetTag extends DelegatingHtmlInputElementTagBase<InputTag> {

  public ResetTag() {
    super(new InputTag() {
      @Override
      protected String getType() {
        return "reset";
      }
    });
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    setName("");
    setProperty("");
    value = null;
  }
  private @Nullable String value;

  /**
   * Value of the label to be placed on this button.
   * [Body of this tag (if any), or "Reset"]
   */
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  protected String processValue(@Nullable Object v) {
    return value == null ? RESET_BUTTON_DEFAULT_LABEL : value;
  }

  private static final String RESET_BUTTON_DEFAULT_LABEL = "Reset";
}
