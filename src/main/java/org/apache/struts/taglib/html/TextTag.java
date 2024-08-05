package org.apache.struts.taglib.html;

import org.springframework.web.servlet.tags.form.InputTag;
import springing.struts1.taglib.DelegatingHtmlInputElementTagBase;

/**
 * Renders an input field of type text. This tag is only valid when nested
 * inside a form tag body.
 */
public class TextTag extends DelegatingHtmlInputElementTagBase<InputTag> {
  public TextTag() {
    super(new InputTag());
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  protected void init() {
  }

  /**
   * Number of character positions to allocate. [Browser default]
   */
  public void setSize(String size) {
    getBaseTag().setSize(size);
  }

  /**
   * Maximum number of input characters to accept. [No limit]
   */
  public void setMaxlength(String maxlength) {
    getBaseTag().setMaxlength(maxlength);
  }

  /**
   * JavaScript event handler executed when a user selects some text in a text
   * field.
   */
  public void setOnselect(String onselect) {
    getBaseTag().setOnselect(onselect);
  }
}
