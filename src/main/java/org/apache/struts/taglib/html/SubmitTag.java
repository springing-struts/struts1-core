package org.apache.struts.taglib.html;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.jsp.JspException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsHtmlElementTagBase;

/**
 * Renders an HTML `input` element of type `submit`.
 * If a graphical button is needed (a button with an image), then the
 * `html:image` tag is more appropriate.
 */
public class SubmitTag extends StrutsHtmlElementTagBase {

  public SubmitTag() {
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
    property = null;
  }

  private @Nullable String value;
  private @Nullable String property;

  @Override
  protected String getTagName() {
    return "input";
  }

  @Override
  public int doAfterBody() throws JspException {
    super.doAfterBody();
    return SKIP_BODY;
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    var attrs = new HashMap<String, String>();
    attrs.put("type", "submit");
    attrs.put("value", getValue());
    attrs.put("name", property);
    return attrs;
  }

  /**
   * The value of the button label.
   */
  public void setValue(String value) {
    this.value = value;
  }

  private String getValue() {
    if (hasText(value)) {
      return value;
    }
    var content = readBodyContentAsText();
    return content.isBlank() ? getDefaultValue() : content;
  }

  /**
   * The name of the request parameter that will be included with this
   * submission, set to the specified value.
   */
  public void setProperty(String property) {
    this.property = property;
  }

  public String getDefaultValue() {
    return SUBMIT_BUTTON_DEFAULT_LABEL;
  }

  private static final String SUBMIT_BUTTON_DEFAULT_LABEL = "Submit";
}
