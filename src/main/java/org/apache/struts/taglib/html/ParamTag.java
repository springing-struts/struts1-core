package org.apache.struts.taglib.html;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.springframework.lang.Nullable;

/**
 * Adds a parameter to the following tags:
 * - `html:frame`
 * - `html:link`
 * - `html:rewrite`
 */
public class ParamTag extends TagSupport {

  public ParamTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    name = null;
    value = null;
  }

  private @Nullable String name;
  private @Nullable String value;

  @Override
  public int doStartTag() throws JspException {
    var parent = (ParamTagParent) findAncestorWithClass(
      this,
      ParamTagParent.class
    );
    if (parent == null) throw new IllegalStateException(
      "An <html:param> tag should be enclosed by one of the following tags:" +
      " <html:link>, <html:frame>, or <html:rewrite>."
    );
    parent.handleParam(this);
    return super.doStartTag();
  }

  /**
   * [Required] The String containing the name of the request parameter.
   */
  public String getName() {
    if (!hasText(name)) throw new IllegalArgumentException(
      "An <html:param> tag requires the `name` property."
    );
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * The value of the request parameter specified by the `name` attribute,
   * whose return value must be a String or String[] that will be dynamically
   * added to this hyperlink.
   */
  public @Nullable String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
