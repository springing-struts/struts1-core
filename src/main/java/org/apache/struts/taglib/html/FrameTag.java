package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import java.util.Map;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsHtmlElementTagBase;
import springing.struts1.taglib.UrlBuilder;
import springing.struts1.taglib.UrlBuilderSupport;

/**
 * Render an HTML frame element.
 * Renders an HTML `frame` element with processing for the `src` attribute that
 * is identical to that performed by the `html:link` tag for the `href`
 * attribute. URL rewriting will be applied automatically, to maintain session
 * state in the absence of cookies.
 * The base URL for this frame is calculated based on which of the following
 * attributes you specify (you must specify exactly one of them):
 * - **forward:**
 *   Use the value of this attribute as the name of a global `ActionForward` to
 *   be looked up, and use the module-relative or context-relative URI found
 *   there.
 * - **href:**
 *   Use the value of this attribute unchanged.
 * - **page:**
 *   Use the value of this attribute as a module-relative URI, and generate a
 *   server-relative URI by including the context path and application prefix.
 * - **action:**
 *   Use the value of this attribute as the logical name of a global Action
 *   that contains the actual content-relative URI of the destination of this
 *   transfer.
 * Normally, the hyperlink you specify with one of the attributes described in
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
 * - **Specify only the `paramName` attribute:**
 *   The named JSP bean (optionally scoped by the value of the `paramScope`
 *   attribute) must identify a value that can be converted to a String.
 * - **Specify both the `paramName` and `paramProperty` attributes:**
 *   The specified property getter method will be called on the JSP bean
 *   identified by the `paramName` (and optional `paramScope`) attributes, in
 *   order to select a value that can be converted to a String.
 * If you prefer to specify a `java.util.Map` that contains all the request
 * parameters to be added to the hyperlink, use one of the following
 * techniques:
 * - **Specify only the `name` attribute:**
 *   The named JSP bean (optionally scoped by the value of the `scope`
 *   attribute) must identify a `java.util.Map` containing the parameters.
 * - **Specify both `name` and `property` attributes:**
 *   The specified property getter method will be called on the bean identified
 *   by the `name` (and optional `scope`) attributes, in order to return the
 *   `java.util.Map` containing the parameters.
 * As the `Map` is processed, the keys are assumed to be the names of query
 * parameters to be appended to the hyperlink. The value associated with each
 * key must be either a String or a String array representing the parameter
 * value(s), or an object whose toString() method will be called. If a String
 * array is specified, more than one value for the same query parameter name
 * will be created.
 * Additionally, you can request that the current transaction control token, if
 * any, be included in the generated hyperlink by setting the `transaction`
 * attribute to `true`. You can also request that an anchor ("#xxx") be added
 * to the end of the URL that is created by any of the above mechanisms, by
 * using the `anchor` attribute.
 */
public class FrameTag
  extends StrutsHtmlElementTagBase
  implements UrlBuilderSupport {

  public FrameTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    urlBuilder = new UrlBuilder();
    frameBorder = null;
    frameName = null;
    marginHeight = null;
    marginWidth = null;
    nonResize = false;
    scrolling = null;
  }

  private UrlBuilder urlBuilder;
  private @Nullable String frameBorder;
  private @Nullable String frameName;
  private @Nullable String marginHeight;
  private @Nullable String marginWidth;
  private boolean nonResize;
  private @Nullable String scrolling;

  @Override
  protected String getTagName() {
    return "frame";
  }

  @Override
  public UrlBuilder getUrlBuilder() {
    return urlBuilder;
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    attrs.put("src", urlBuilder.buildUrl(getPageContext()));
    attrs.put("frameboader", frameBorder);
    attrs.put("name", frameName);
    attrs.put("marginheight", marginHeight);
    attrs.put("marginwidth", marginWidth);
    attrs.put("nonresize", nonResize ? "nonresize" : null);
    attrs.put("scrolling", scrolling);
    return attrs;
  }

  /**
   * Should a frame border be generated around this frame (1) or not (0)?
   */
  public void setFrameborder(String frameBorder) {
    this.frameBorder = frameBorder;
  }

  /**
   * Value for the `name` attribute of the rendered `frame` element.
   */
  public void setFrameName(String frameName) {
    this.frameName = frameName;
  }

  /**
   * URI of a long description of the frame. This description should supplement
   * the short description provided by the `title` attribute, and may be
   * particularly useful for non-visual user agents.
   */
  public void setLongdesc(String longDesc) {
    throw new UnsupportedOperationException();
  }

  /**
   * The amount of space (in pixels) to be left between the frame's contents
   * and its top and bottom margins.
   */
  public void setMarginheight(String marginHeight) {
    this.marginHeight = marginHeight;
  }

  /**
   * The amount of space (in pixels) to be left between the frame's contents
   * and its left and right margins.
   */
  public void setMarginwidth(String marginWidth) {
    this.marginWidth = marginWidth;
  }

  /**
   * Should users be disallowed from resizing the frame? (true, false).
   */
  public void setNonresize(boolean nonResize) {
    this.nonResize = nonResize;
  }

  /**
   * Should scroll bars be created unconditionally (yes), never (no), or only
   * when needed (auto)?
   */
  public void setScrolling(String scrolling) {
    this.scrolling = scrolling;
  }
}
