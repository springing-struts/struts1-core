package org.apache.struts.taglib.html;

import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsHtmlElementTagBase;
import springing.struts1.taglib.UrlBuilder;
import springing.struts1.taglib.UrlBuilderSupport;

/**
 * Renders an HTML `a`` element as an anchor definition (if "linkName" is
 * specified) or as a hyperlink to the specified URL. URL rewriting will be
 * applied automatically, to maintain session state in the absence of cookies.
 * The content displayed for this hyperlink will be taken from the body of this
 * tag.
 * The base URL for this hyperlink is calculated based on which of the
 * following attributes you specify (you must specify exactly one of them):
 * - forward
 *   Use the value of this attribute as the name of a global `ActionForward`
 *   to be looked up, and use the module-relative or context-relative URI found
 *   there. If the forward is module-relative then it must point to an action
 *   and NOT to a page.
 * - action
 *   Use the value of this attribute as the name of a `Action` to be looked up,
 *   and use the module-relative or context-relative URI found there.
 * - href
 *   Use the value of this attribute unchanged.
 * - page
 *   Use the value of this attribute as a module-relative URI, and generate a
 *   server-relative URI by including the context path and module prefix.
 * Normally, the hyperlink you specify with one of the attributes described in
 * the previous paragraph will be left unchanged (other than URL rewriting if
 * necessary). However, there are three ways you can append one or more
 * dynamically defined query parameters to the hyperlink - specify a single
 * parameter with the `paramId` attribute (and its associated attributes to
 * select the value), or specify the `name` (and optional `property`)
 * attributes to select a `java.util.Map` bean that contains one or more
 * parameter ids and corresponding values, or nest one or more `html:param`
 * tags in the tag body.
 * To specify a single parameter, use the `paramId` attribute to define the
 * name of the request parameter to be submitted. To specify the corresponding
 * value, use one of the following approaches:
 * - Specify only the `paramName` attribute
 *   The named JSP bean (optionally scoped by the value of the `paramScope`
 *   attribute) must identify a value that can be converted to a String.
 * - Specify both the `paramName` and `paramProperty` attributes
 *   The specified property getter method will be called on the JSP bean
 *   identified by the `paramName` (and optional `paramScope`) attributes, in
 *   order to select a value that can be converted to a String.
 * If you prefer to specify a `java.util.Map` that contains all the request
 * parameters to be added to the hyperlink, use one of the following
 * techniques:
 * - Specify only the `name` attribute
 *   The named JSP bean (optionally scoped by the value of the `scope`
 *   attribute) must identify a `java.util.Map` containing the parameters.
 * - Specify both `name` and `property` attributes
 *   The specified property getter method will be called on the bean identified
 *   by the `name` (and optional `scope`) attributes, in order to return the
 *   `java.util.Map` containing the parameters.
 * As the `Map` is processed, the keys are assumed to be the names of query
 * parameters to be appended to the hyperlink. The value associated with each
 * key must be either a String or a String array representing the parameter
 * value(s), or an object whose toString() method will be called. If a String
 * array is specified, more than one value for the same query parameter name
 * will be created.
 * Supplementing these two methods, you can nest one or more `html:param` tags
 * to dynamically add parameters in a logic-friendly way (such as executing a
 * for loop that assigns the name/value pairs at runtime). This method does not
 * compete with the aforementioned; it will add its parameters *in addition* to
 * whatever parameters are already specified.
 * Additionally, you can request that the current transaction control token, if
 * any, be included in the generated hyperlink by setting the `transaction`
 * attribute to `true` You can also request that an anchor ("#xxx") be added to
 * the end of the URL that is created by any of the above mechanisms, by using
 * the `anchor` attribute.
 */
public class LinkTag
  extends StrutsHtmlElementTagBase
  implements UrlBuilderSupport, ParamTagParent {

  public LinkTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    urlDate = new UrlBuilder();
    target = null;
  }

  private UrlBuilder urlDate;
  private @Nullable String target;

  /**
   * The window target in which the resource requested by this hyperlink will
   * be displayed, for example in a framed presentation.
   */
  public void setTarget(String target) {
    this.target = target;
  }

  @Override
  public void setBundle(String bundle) {
    super.setBundle(bundle);
    UrlBuilderSupport.super.setBundle(bundle);
  }

  @Override
  protected String getTagName() {
    return "a";
  }

  @Override
  public UrlBuilder getUrlBuilder() {
    return urlDate;
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    var attrs = new HashMap<String, String>();
    var href = buildUrl(getPageContext());
    attrs.put("href", href);
    attrs.put("target", target);
    return attrs;
  }

  @Override
  public void handleParam(ParamTag param) {
    addParam(param.getName(), param.getValue());
  }
}
