package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import java.util.Map;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;

/**
 * Renders an input field of type text. This tag is only valid when nested
 * inside a form tag body.
 */
public class TextTag extends StrutsInputElementTagBase {

  public TextTag() {
    init();
  }

  @Override
  protected String getType() {
    return "text";
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  protected void init() {
    size = null;
    maxlength = null;
    onselect = null;
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    attrs.put("size", size);
    attrs.put("maxlength", maxlength);
    attrs.put("onselect", onselect);
    return attrs;
  }

  private @Nullable String size;
  private @Nullable String maxlength;
  private @Nullable String onselect;

  /**
   * Number of character positions to allocate. [Browser default]
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * Maximum number of input characters to accept. [No limit]
   */
  public void setMaxlength(String maxlength) {
    this.maxlength = maxlength;
  }

  /**
   * JavaScript event handler executed when a user selects some text in a text
   * field.
   */
  public void setOnselect(String onselect) {
    this.onselect = onselect;
  }
}
