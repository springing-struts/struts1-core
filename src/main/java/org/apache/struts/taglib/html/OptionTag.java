package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsHtmlElementTagBase;
import java.util.Map;
import static java.util.Objects.requireNonNull;

/**
 * Render A Select Option.
 * Renders an HTML option element, representing one of the choices for an
 * enclosing select element. The text displayed to the user comes from either
 * the body of this tag, or from a message string looked up based on the
 * `bundle`, `locale`, and `key` attributes.
 * If the value of the corresponding bean property matches the specified value,
 * this option will be marked selected. This tag is only valid when nested
 * inside a html:select tag body.
 */
public class OptionTag extends StrutsHtmlElementTagBase {

  public OptionTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    processesBodyContent();
    value = null;
    filter = false;
    key = null;
    locale = null;
  }

  private @Nullable String value;
  private boolean filter;
  private @Nullable String key;
  private @Nullable String locale;

  @Override
  protected String getTagName() {
    return "option";
  }

  /**
   * Value to be submitted for this field if this option is selected by the
   * user.
   */
  public void setValue(@Nullable String value) {
    this.value = value;
  }

  /**
   * Set to `true` if you want the option label to be filtered for sensitive
   * characters in HTML. By default, such a value is NOT filtered.
   */
  public void setFilter(boolean filter) {
    this.filter = filter;
  }

  /**
   * If specified, defines the message key to be looked up in the resource
   * bundle specified by `bundle` for the text displayed to the user for this
   * option. If not specified, the text to be displayed is taken from the body
   * content of this tag.
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * The session attributes key for the Locale instance to use for looking up
   * the message specified by the `key` attribute. If not specified, uses the
   * standard Struts session attribute name.
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  @Override
  protected @Nullable String getBodyTextForOutput() {
    if (key != null) {
      return ModuleUtils.getCurrent().getMessageResources(getBundle()).requireMessage(key);
    }
    return readBodyContentAsText();
  }

  public boolean isSelected() {
    if (value == null) throw new IllegalArgumentException(
      "The value attribute is required to the html:option tag."
    );
    return getEnclosingSelectTag().isMatch(value);
  }

  private SelectTag getEnclosingSelectTag() {
    return (SelectTag) requireNonNull(
      getPageContext().getAttribute(Constants.SELECT_KEY),
      "Failed to retrieve the reference to the parent html:select tag."
    );
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    if (value == null) throw new IllegalStateException(
      "Value attribute must be set for <html:option> tag."
    );
    var attrs = super.getAdditionalAttributes();
    attrs.put("value", value);
    attrs.put("selected", isSelected() ? "selected" : null);
    return attrs;
  }
}
