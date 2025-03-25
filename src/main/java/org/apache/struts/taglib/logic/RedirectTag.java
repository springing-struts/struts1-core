package org.apache.struts.taglib.logic;

import static java.util.Objects.requireNonNull;

import jakarta.servlet.jsp.JspException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.TagSupport;
import springing.struts1.taglib.UrlBuilder;
import springing.struts1.taglib.UrlBuilderSupport;

/**
 * Render an HTTP Redirect.
 * Performs an `HttpServletResponse.sendRedirect()` call to the hyperlink
 * specified by the attributes to this tag. URL rewriting will be applied
 * automatically, to maintain session state in the absence of cookies.
 * The base URL for this redirect is calculated based on which of the following
 * attributes you specify (you must specify exactly one of them):
 * - **forward**
 *   Use the value of this attribute as the name of a global `ActionForward` to
 *   be looked up, and use the module-relative or context-relative URI found
 *   there.
 * - **href**
 *   Use the value of this attribute unchanged.
 * - **page**
 *   Use the value of this attribute as a module-relative URI, and generate a
 *   server-relative URI by including the context path.
 * Normally, the redirect you specify with one of the attributes described in
 * the previous paragraph will be left unchanged (other than URL rewriting if
 * necessary). However, there are two ways you can append one or more
 * dynamically defined query parameters to the hyperlink - specify a single
 * parameter with the `paramId` attribute (and its associated attributes to
 * select the value), or specify the `name` (and optional `property`)
 * attributes to select a `java.util.Map` bean that contains one or more
 * parameter ids and corresponding values.
 * To specify a single parameter, use the `paramId` attribute to define the
 * name of the request parameter to be submitted.  To specify the corresponding
 * value, use one of the following approaches:
 * **Specify only the `paramName` attribute**
 *   The named JSP bean (optionally scoped by the value of the `paramScope`
 *   attribute) must identify a value that can be converted to a String.
 * **Specify both the `paramName` and `paramProperty` attributes**
 *   The specified property getter method will be called on the JSP bean
 *   identified by the `paramName` (and optional `paramScope`) attributes, in
 *   order to select a value that can be converted to a String.
 * If you prefer to specify a `java.util.Map` that contains all the request
 * parameters to be added to the hyperlink, use one of the following
 * techniques:
 * - **Specify only the `name` attribute**
 *   The named JSP bean (optionally scoped by the value of the `scope`
 *   attribute) must identify a `java.util.Map` containing the parameters.
 * - **Specify both `name` and `property` attributes**
 *   The specified property getter method will be called on the bean identified
 *   by the `name` (and optional `scope`) attributes, in order to return the
 *   `java.util.Map` containing the parameters.
 * As the Map` is processed, the keys are assumed to be the names of query
 * parameters to be appended to the hyperlink. The value associated with each
 * key must be either a String or a String array representing the parameter
 * value(s). If a String array is specified, more than one value for the same
 * query parameter name will be created.
 */
public class RedirectTag extends TagSupport implements UrlBuilderSupport {

  public RedirectTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    urlBuilder = new UrlBuilder();
  }

  private UrlBuilder urlBuilder;

  @Override
  public UrlBuilder getUrlBuilder() {
    return urlBuilder;
  }

  @Override
  public int doStartTag() throws JspException {
    var context = requireNonNull(pageContext);
    var response = (HttpServletResponse) context.getResponse();
    var redirectUrl = buildUrl(context);
    try {
      response.sendRedirect(redirectUrl);
    } catch (IOException e) {
      throw new RuntimeException(
        String.format("Failed to redirect to the path [%s].", redirectUrl),
        e
      );
    }
    return SKIP_PAGE;
  }
}
