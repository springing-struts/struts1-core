package org.apache.struts.taglib.html;

import static java.util.Collections.emptyIterator;
import static org.springframework.util.StringUtils.hasText;
import static springing.util.ObjectUtils.asIterator;

import jakarta.servlet.jsp.JspException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsInputElementTagBase;

/**
 * Renders an HTML input element of type checkbox, whose "checked" status is
 * initialized based on whether the specified value matches one of the elements
 * of the underlying property's array of current values. This element is useful
 * when you have large numbers of checkboxes, and prefer to combine the values
 * into a single array-valued property instead of multiple boolean properties.
 * This tag is only valid when nested inside a form tag body.
 * **WARNING**:
 * In order to correctly recognize cases where none of the associated
 * checkboxes are selected, the `ActionForm` bean associated with this form
 * must include a statement setting the corresponding array to zero length in
 * the `reset()` method.
 * The value to be returned to the server, if this checkbox is selected, must
 * be defined by one of the following methods:
 * - Specify a `value` attribute, whose contents will be used literally as the
 *   value to be returned.
 * - Specify no `value` attribute, and the nested body content of this tag will
 *   be used as the value to be returned.
 * Also note that a map backed attribute cannot be used to hold a `String[]`
 * for a group of multibox tags.
 */
public class MultiboxTag extends StrutsInputElementTagBase {

  public MultiboxTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    processesBodyContent();
    checked = false;
  }

  private boolean checked;

  @Override
  protected String getType() {
    return "checkbox";
  }

  @Override
  protected Object processBoundValue(@Nullable Object propertyValue) {
    checked = isChecked(propertyValue);
    return getDefaultValue();
  }

  private boolean isChecked(@Nullable Object propertyValue) {
    Iterator<?> iterator = (propertyValue == null)
      ? emptyIterator()
      : (propertyValue instanceof String str)
        ? List.of(str).iterator()
        : (propertyValue instanceof List<?> list)
          ? list.iterator()
          : propertyValue.getClass().isArray()
            ? asIterator(propertyValue)
            : null;
    if (iterator == null) throw new IllegalStateException(
      String.format(
        "The value of bounded property for the multibox tag should be an Array or List or String. but was [%s]",
        propertyValue.getClass().getName()
      )
    );
    var itemValue = getValue();
    while (iterator.hasNext()) {
      var item = iterator.next();
      if (itemValue.equals(item.toString())) {
        return true;
      }
    }
    return false;
  }

  private String getValue() {
    var attrValue = getDefaultValue();
    if (hasText(attrValue)) {
      return attrValue;
    }
    var contentValue = readBodyContentAsText();
    if (hasText(contentValue)) {
      return contentValue;
    }
    return DEFAULT_VALUE_STRING;
  }

  private static final String DEFAULT_VALUE_STRING = "on";

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    attrs.put("checked", checked ? "checked" : null);
    attrs.put("value", getValue());
    return attrs;
  }
}
