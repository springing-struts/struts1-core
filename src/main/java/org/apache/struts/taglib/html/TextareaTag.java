package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;

import java.util.Map;

/**
 * Render a textarea element. This tag is only valid when nested inside a form
 * tag body.
 */
public class TextareaTag extends StrutsInputElementTagBase {

  public TextareaTag() {
    init();
  }

  @Override
  protected String getTagName() {
    return "textarea";
  }

  @Override
  protected String getType() {
    return null;
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    processesBodyContent();
    cols = null;
    rows = null;
    onselect = null;
  }

  @Override
  protected String getBodyTextForOutput() {
    return getBoundValueOrElse("").toString();
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    attrs.put("cols", cols);
    attrs.put("rows", rows);
    attrs.put("onselect", onselect);
    return attrs;
  }

  private @Nullable String cols;
  private @Nullable String rows;
  private @Nullable String onselect;

  /**
   * The number of columns to display.
   */
  public void setCols(String cols) {
    this.cols = cols;
  }

  /**
   * The number of rows to display.
   */
  public void setRows(String rows) {
    this.rows = rows;
  }

  /**
   * JavaScript event handler executed when a when a user selects some text in
   * a text field.
   */
  public void setOnselect(String onselect) {
    this.onselect = onselect;
  }
}
