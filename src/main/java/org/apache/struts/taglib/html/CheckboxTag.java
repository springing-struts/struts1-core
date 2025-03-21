package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;
import java.util.Map;

/**
 * Renders an HTML `input` element of type `checkbox`, populated from the
 * specified value or the specified property of the bean associated with our
 * current form. This tag is only valid when nested inside a form tag body.
 * **NOTE:**
 * The underlying property value associated with this field should be of type
 * `boolean`, and any `value` you specify should correspond to one of the
 * Strings that indicate a true value ("true", "yes", or "on"). If you wish to
 * utilize a set of related String values, consider using the `multibox` tag.
 * **WARNING:**
 * In order to correctly recognize unchecked checkboxes, the `ActionForm` bean
 * associated with this form must include a statement setting the corresponding
 * boolean property to `false` in the `reset()` method.
 */
public class CheckboxTag extends StrutsInputElementTagBase {

  public CheckboxTag() {
    init();
  }

  @Override
  protected @Nullable String getType() {
    return "checkbox";
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    setValue("on");
    checked = false;
  }

  private boolean checked;

  @Override
  protected Object processBoundValue(@Nullable Object propertyValue) {
    checked = (propertyValue instanceof Boolean bool && bool);
    return getDefaultValue();
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    attrs.put("checked", checked ? "checked" : null);
    attrs.put("value", getDefaultValue());
    return attrs;
  }
}
