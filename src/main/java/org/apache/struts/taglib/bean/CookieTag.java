package org.apache.struts.taglib.bean;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.springframework.lang.Nullable;

import java.util.Arrays;

import static org.springframework.util.StringUtils.hasText;

/**
 * Define a scripting variable based on the value(s) of the specified request
 * cookie.
 * Retrieve the value of the specified request cookie (as a single value or
 * multiple values, depending on the `multiple` attribute), and define the
 * result as a page scope attribute of type `Cookie` (if `multiple` is not
 * specified) or `Cookie[]` (if `multiple` is specified).
 * If no cookie with the specified name can be located, and no default value is
 * specified, a request time exception will be thrown.
 */
public class CookieTag extends SetSupport {

  public CookieTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    multiple = false;
    name = null;
    defaultValue = null;
  }

  private boolean multiple = false;
  private @Nullable String name;
  private @Nullable String defaultValue;

  @Override
  public int doEndTag() throws JspException {
    var request = (HttpServletRequest) pageContext.getRequest();
    if (!hasText(name)) throw new IllegalStateException(
      "The name property of a <bean:cookie> tag is required."
    );
    var cookies = Arrays.stream(request.getCookies()).filter(it -> name.equals(it.getName())).toList();
    if (cookies.isEmpty()) {
      var dummy = new Cookie(name, defaultValue);
      this.value = multiple ? new Cookie[] {dummy} : dummy;
    }
    else {
      this.value = multiple ? cookies : cookies.getFirst();
    }
    return super.doEndTag();
  }

  /**
   * (Required) Specifies the name of the scripting variable (and associated
   * page scope attribute) that will be made available with the value of the
   * specified request cookie.
   */
  public void setId(String id) {
    setVar(id);
  }

  /**
   * If any arbitrary value for this attribute is specified, causes all
   * matching cookies to be accumulated and stored into a bean of type
   * `Cookie[]`. If not specified, the first value for the specified cookie
   * will be retrieved as a value of type `Cookie`.
   */
  public void setMultiple(String multiple) {
    this.multiple = !multiple.isBlank();
  }

  /**
   * (Required) Specifies the name of the request cookie whose value, or
   * values, is to be retrieved.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * The default cookie value to return if no cookie with the specified name
   * was included in this request.
   */
  public void setValue(String value) {
    this.defaultValue = value;
  }
}
