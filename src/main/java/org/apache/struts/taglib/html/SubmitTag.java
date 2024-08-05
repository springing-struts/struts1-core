package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.InputTag;
import springing.struts1.taglib.DelegatingHtmlInputElementTagBase;

/**
 * Renders an HTML `input` element of type `submit`.
 * If a graphical button is needed (a button with an image), then the
 * `html:image` tag is more appropriate.
 */
public class SubmitTag extends DelegatingHtmlInputElementTagBase<InputTag> {
  public SubmitTag() {
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

  private void init() {
    value = null;
  }
  private @Nullable String value;

  /**
   * The value of the button label.
   */
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  protected String processValue(@Nullable Object v) {
    return value == null ? SUBMIT_BUTTON_DEFAULT_LABEL : value;
  }
  private static final String SUBMIT_BUTTON_DEFAULT_LABEL = "Submit";
}
