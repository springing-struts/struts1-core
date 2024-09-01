package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.TagWriter;
import springing.struts1.taglib.HtmlElementTagBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders an HTML `input` element of type `submit`.
 * If a graphical button is needed (a button with an image), then the
 * `html:image` tag is more appropriate.
 */
public class SubmitTag extends HtmlElementTagBase implements BodyTag {
  public SubmitTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    value = null;
    property = null;
    bodyContent = null;
  }
  private @Nullable String value;
  private @Nullable String property;
  private @Nullable BodyContent bodyContent;

  @Override
  protected String getTagName() {
    return "input";
  }

  @Override
  protected int writeTagContent(TagWriter tagWriter) throws JspException {
    //super.writeTagContent(tagWriter);
    this.tagWriter = tagWriter;
    return EVAL_BODY_BUFFERED;
  }
  private @Nullable TagWriter tagWriter;

  @Override
  public int doAfterBody() throws JspException {
    super.doAfterBody();
    return SKIP_BODY;
  }

  @Override
  public int doEndTag() throws JspException {
    super.writeTagContent(tagWriter);
    return super.doEndTag();
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    var attrs = new HashMap<String, String>();
    attrs.put("type", "submit");
    attrs.put("value", getValue());
    if (property != null) {
      attrs.put("name", property);
    }
    return attrs;
  }

  /**
   * The value of the button label.
   */
  public void setValue(String value) {
    this.value = value;
  }

  private String getValue() {
    if (value != null && !value.isBlank()) {
      return value;
    }
    var content = bodyContent == null ? "" : bodyContent.getString();
    if (!content.isBlank()) {
      return content;
    }
    return SUBMIT_BUTTON_DEFAULT_LABEL;
  }

  /**
   * The name of the request parameter that will be included with this
   * submission, set to the specified value.
   */
  public void setProperty(String property) {
    this.property = property;
  }
  private static final String SUBMIT_BUTTON_DEFAULT_LABEL = "Submit";

  @Override
  public void setBodyContent(BodyContent b) {
    this.bodyContent = b;
  }

  @Override
  public void doInitBody() throws JspException {
    //NOP
  }
}
