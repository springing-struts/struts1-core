package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import springing.struts1.taglib.DelegatingHtmlInputElementTagBase;

/**
 * Renders an HTML `<input>` element of type `checkbox`, populated from the
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
public class CheckboxTag extends
  DelegatingHtmlInputElementTagBase<org.springframework.web.servlet.tags.form.CheckboxTag> {

  public CheckboxTag() {
    super(new org.springframework.web.servlet.tags.form.CheckboxTag());
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    setValue("on");
  }
  private String value;

  /**
   * The value to be transmitted if this checkbox is checked when the form is
   * submitted. If not specified, the value "on" will be returned.
   */
  public void setValue(String value) {
    this.value = value;
    getBaseTag().setValue(value);
  }

  @Override
  protected String processValue(@Nullable Object v) {
    return (v instanceof Boolean bool && bool) ? value : "";
  }
}
