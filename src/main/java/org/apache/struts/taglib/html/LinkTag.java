package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import springing.struts1.taglib.HtmlElementTagBase;
import springing.struts1.taglib.UrlBuilderBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders an HTML <a> element as an anchor definition (if "linkName" is
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
 * parameter ids and corresponding values, or nest one or more <html:param>
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
 * Supplementing these two methods, you can nest one or more <html:param> tags
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
public class LinkTag extends HtmlElementTagBase implements UrlBuilderBean {

  /**
   * Optional anchor tag ("#xxx") to be added to the generated hyperlink.
   * Specify this value *without* any "#" character. Exactly one of `forward`,
   * `href`, or `page` attribute must still be specified with the anchor.
   */
  public void setAnchor(String anchor) {
    throw new UnsupportedOperationException();
  }

  /**
   * The URL to which this hyperlink will transfer control if activated. This
   * hyperlink may be dynamically modified by the inclusion of query
   * parameters, as described in the tag description. You *must* specify
   * exactly one of the `action` attribute, the `forward` attribute, the `href`
   * attribute, the `linkName` attribute, or the `page` attribute.
   */
  public void setHref(String href) {
    throw new UnsupportedOperationException();
  }

  /**
   * Valid only inside `logic:iterate` tag. If `true` then indexed parameter
   * with name from indexId attribute will be added to the query string.
   * Indexed parameter looks like `"index[32]"`. Number in brackets will be
   * generated for every iteration and taken from ancestor `logic:iterate` tag.
   */
  public void setIndexed(boolean indexed) {
    throw new UnsupportedOperationException();
  }

  /**
   * By this attribute different name for the indexed parameter can be
   * specified.Take a look to the "indexed" attribute for details.
   */
  public void setIndexId(String indexId) {
    throw new UnsupportedOperationException();
  }


  /**
   * The anchor name to be defined within this page, so that you can reference
   * it with intra-page hyperlinks. In other words, the value specified here
   * will render a "name" element in the generated anchor tag.
   */
  public void setLinkName(String linkName) {
    throw new UnsupportedOperationException();
  }

  /**
   * The window target in which the resource requested by this hyperlink will
   * be displayed, for example in a framed presentation.
   */
  public void setTarget(String target) {
    this.target = target;
  }
  private @Nullable String target;

  /**
   * If set to `true`, any current transaction control token will be included
   * in the generated hyperlink, so that it will pass an `isTokenValid()` test
   * in the receiving Action.
   */
  public void setTransaction(boolean transaction) {
    throw new UnsupportedOperationException();
  }


  @Override
  protected String getTagName() {
    return "a";
  }

  @Override
  public UrlData getUrlData() {
    return urlDate;
  }
  private final UrlData urlDate = new UrlData();

  @Override
  protected Map<String, String> getAdditionalAttributes() {
    var attrs = new HashMap<String, String>();
    attrs.put("href", buildUrl(pageContext));
    if (target != null) {
      attrs.put("target", target);
    }
    return attrs;
  }
}
