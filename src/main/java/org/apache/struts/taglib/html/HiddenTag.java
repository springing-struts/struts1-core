package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.springframework.web.servlet.tags.form.TagWriter;
import springing.struts1.taglib.StrutsInputElementTagBase;

/**
 * Render A Hidden Field.
 * Renders an HTML `input` element of type hidden, populated from the
 * specified value or the specified property of the bean associated with our
 * current form. This tag is only valid when nested inside a form tag body.
 */
public class HiddenTag extends StrutsInputElementTagBase {

  public HiddenTag() {
    init();
  }

  @Override
  protected String getType() {
    return "hidden";
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    write = false;
  }

  @Override
  public int doEndTag() throws JspException {
    var result = super.doEndTag();
    if (write) {
      var tagWriter = new TagWriter(pageContext);
      tagWriter.startTag("span");
      tagWriter.appendValue(getBoundValueOrElse("") + "");
      tagWriter.endTag();
    }
    return result;
  }

  private boolean write;

  /**
   * Should the value of this field also be rendered to the response page to
   * make it visible, in addition to creating an HTML type="hidden" element?
   * By default, only the hidden element is created.
   */
  public void setWrite(boolean write) {
    this.write = write;
  }
}
