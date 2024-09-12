package org.apache.struts.taglib.bean;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.springframework.lang.Nullable;

import java.util.Collections;

import static org.springframework.util.StringUtils.hasText;

/**
 * Define a scripting variable based on the value(s) of the specified request
 * header.
 * Retrieve the value of the specified request header (as a single value or
 * multiple values, depending on the `multiple` attribute), and define the
 * result as a page scope attribute of type `String` (if `multiple` is not
 * specified) or `String[]` (if `multiple` is specified).
 * If no header with the specified name can be located, and no default value
 * is specified, a request time exception will be thrown.
 */
public class HeaderTag extends SetSupport {

  public HeaderTag() {
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
    if (!hasText(name)) throw new IllegalStateException(
      "The name property of a <bean:header> tag is required."
    );
    var request = (HttpServletRequest) pageContext.getRequest();
    var headers = Collections.list(request.getHeaders(name));
    if (headers.isEmpty()) {
      if (defaultValue == null) throw new IllegalStateException(String.format(
        "No header with the assigned name [%s] was sent and default value was not set.",
        name
      ));
      this.value = defaultValue;
    }
    else {
      this.value = multiple ? headers.toArray() : headers.getFirst();
    }
    return super.doEndTag();
  }

  /**
   * Specifies the name of the scripting variable (and associated page scope
   * attribute) that will be made available with the value of the specified
   * request header.
   */
  public void setId(String id) {
    setVar(id);
  }

  /**
   * If any arbitrary value for this attribute is specified, causes a call to
   * `HttpServletRequest.getHeaders()` and a definition of the result as a bean
   * of type `String[]`. Otherwise, `HttpServletRequest.getHeader()` will be
   * called, and a definition of the result as a bean of type `String` will be
   * performed.
   */
  public void setMultiple(String multiple) {
    this.multiple = hasText(multiple);
  }

  /**
   * (Required) Specifies the name of the request header whose value, or
   * values, is to be retrieved.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * The default header value to return if no header with the specified name
   * was included in this request.
   */
  public void setValue(String value) {
    this.defaultValue = value;
  }
}
