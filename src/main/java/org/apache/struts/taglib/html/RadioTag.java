package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;

import java.util.Map;

/**
 * Render A Radio Button Input Field.
 * Renders an HTML input element of type radio, populated from the specified property of
 * the bean associated with our current form. This tag is only valid when nested inside
 * a form tag body.
 * If an iterator is used to render a series of radio tags, the idName attribute may be
 * used to specify the name of the bean exposed by the iterator. In this case, the value
 * attribute is used as the name of a property on the idName bean that returns the value
 * of the radio tag in this iteration.
 */
public class RadioTag extends StrutsInputElementTagBase {

  public RadioTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    idName = null;
  }
  private @Nullable String idName;

  @Override
  protected @Nullable String getType() {
    return "input";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs =  super.getAdditionalAttributes();
    attrs.put("type", "radio");
    return attrs;
  }

  /**
   * Name of the bean (in some scope) that will return the value of the radio tag.
   * Usually exposed by an iterator. When the idName attribute is present, the value
   * attribute is used as the name of the property on the idName bean that will return
   * the value of the radio tag for this iteration.
   */
  public void setIdName(String idName) {
    this.idName = idName;
  }
}
