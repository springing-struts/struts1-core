package org.apache.struts.taglib.html;

import static java.util.Collections.emptyIterator;
import static springing.util.ObjectUtils.asIterator;

import jakarta.servlet.jsp.JspException;
import java.util.Map;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;

/**
 * Render A Select Element.
 * Renders an HTML select element, associated with a bean property specified by
 * our attributes.  This tag is only valid when nested inside a form tag body.
 * This tag operates in two modes, depending upon the state of the `multiple`
 * attribute, which affects the data type of the associated property you should
 * use:
 * - **multiple="false" or multiple IS NOT selected**
 *   The corresponding property should be a scalar value of any supported data
 *   type.
 * - **multiple="true" IS selected**
 *   The corresponding property should be an array of any supported data type.
 * **WARNING**:
 * In order to correctly recognize cases where no selection at all is made, the
 * `ActionForm` bean associated with this form must include a statement
 * resetting the scalar property to a default value (if `multiple` is not set),
 * or the array property to zero length (if **multiple** is set) in the
 * `reset()` method.
 */
public class SelectTag extends StrutsInputElementTagBase {

  public SelectTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  @Override
  protected void doWriteTagContent() throws JspException {
    getPageContext().setAttribute(Constants.SELECT_KEY, this);
    super.doWriteTagContent();
  }

  @Override
  public int doEndTag() throws JspException {
    var status = super.doEndTag();
    getPageContext().removeAttribute(Constants.SELECT_KEY);
    return status;
  }

  private void init() {
    multiple = false;
    size = null;
  }

  private boolean multiple;
  private @Nullable Integer size;

  @Override
  protected String getTagName() {
    return "select";
  }

  @Override
  protected @Nullable String getType() {
    return null;
  }

  /**
   * If set to any arbitrary value except `false`, the rendered select element
   * will support multiple selections.
   */
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }

  /**
   * The number of available options displayed at one time.
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   * Does the specified value match one of those we are looking for?
   */
  public boolean isMatch(String optionValue) {
    var iterator = asIterator(getBoundValueOrElse(emptyIterator()));
    while (iterator.hasNext()) {
      var item = iterator.next();
      if (item == null) continue;
      if (optionValue.equals(item.toString())) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    attrs.put("multiple", multiple ? "multiple" : null);
    attrs.put("size", size == null ? null : size.toString());
    attrs.put("value", null);
    return attrs;
  }
}
