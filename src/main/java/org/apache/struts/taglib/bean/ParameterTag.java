package org.apache.struts.taglib.bean;

import jakarta.servlet.http.HttpServletRequest;
import springing.struts1.taglib.SetTagBase;

import java.util.Arrays;
import java.util.List;

/**
 * Define a scripting variable based on the value(s) of the specified request
 * parameter.
 * Retrieve the value of the specified request parameter (as a single value or
 * multiple values, depending on the `multiple` attribute), and define the
 * result as a page scope attribute of type `String` (if `multiple` is not
 * specified) or `String[]` (if `multiple` is specified).
 * If no request parameter with the specified name can be located, and no
 * default value is specified, a request time exception will be thrown.
 */
public class ParameterTag extends SetTagBase<String> {


  @Override
  protected List<String> retrieveValues(HttpServletRequest request, String name) {
    var params = request.getParameterValues(name);
    if (params == null) return List.of();
    return Arrays.asList(params);
  }

  @Override
  protected String getDefaultValue(
    HttpServletRequest request, String name, String defaultValueString
  ) {
    return defaultValueString;
  }
}
