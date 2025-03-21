package org.apache.struts.taglib.bean;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.SetTagBase;
import java.util.Collections;
import java.util.List;

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
public class HeaderTag extends SetTagBase<String> {

  private @Nullable String defaultValue;

  @Override
  protected List<String> retrieveValues(HttpServletRequest request, String name) {
    var headers = request.getHeaders(name);
    if (headers == null) return List.of();
    return Collections.list(headers);
  }

  @Override
  protected String getDefaultValue(HttpServletRequest request, String name, String defaultValueString) {
    return defaultValueString;
  }
}
