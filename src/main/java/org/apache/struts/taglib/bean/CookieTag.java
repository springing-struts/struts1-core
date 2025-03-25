package org.apache.struts.taglib.bean;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import springing.struts1.taglib.SetTagBase;

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
public class CookieTag extends SetTagBase<Cookie> {

  @Override
  protected List<Cookie> retrieveValues(
    HttpServletRequest request,
    String name
  ) {
    return Arrays.stream(request.getCookies())
      .filter(it -> name.equals(it.getName()))
      .toList();
  }

  @Override
  protected Cookie getDefaultValue(
    HttpServletRequest request,
    String name,
    String defaultValueString
  ) {
    return new Cookie(name, defaultValueString);
  }
}
