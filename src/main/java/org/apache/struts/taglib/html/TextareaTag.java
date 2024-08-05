package org.apache.struts.taglib.html;

import springing.struts1.taglib.DelegatingHtmlInputElementTagBase;

/**
 * Render a textarea element. This tag is only valid when nested inside a form
 * tag body.
 */
public class TextareaTag extends DelegatingHtmlInputElementTagBase<org.springframework.web.servlet.tags.form.TextareaTag> {

  public TextareaTag() {
    super(new org.springframework.web.servlet.tags.form.TextareaTag());
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
  }

  /**
   * The number of columns to display.
   */
  public void setCols(String cols) {
    getBaseTag().setCols(cols);
  }

  /**
   * The number of rows to display.
   */
  public void setRows(String rows) {
    getBaseTag().setRows(rows);
  }

  /**
   * JavaScript event handler executed when a when a user selects some text in
   * a text field.
   */
  public void setOnselect(String onselect) {
    getBaseTag().setOnselect(onselect);
  }
}
