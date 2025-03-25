package org.apache.struts.taglib.bean;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.jsp.JspException;
import org.apache.struts.util.ModuleUtils;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.springframework.lang.Nullable;
import springing.util.StringUtils;

/**
 * Load the response from a dynamic application request and make it available
 * as a bean.
 * Perform an internal dispatch to the specified application component (or
 * external URL) and make the response data from that request available as a
 * bean of type `String`. This tag has a function similar to that of the
 * standard `jsp:include` tag, except that the response data is stored in a
 * page scope attribute instead of being written to the output stream. If the
 * current request is part of a session, the generated request for this include
 * tag will also include the session identifier (and thus be part of the same
 * session).
 * The URL used to access the specified application component is calculated
 * based on which of the following attributes you specify (you must specify
 * exactly one of them):
 * - **forward**
 *   Use the value of this attribute as the name of a global `ActionForward`
 *   to be looked up, and use the module-relative or context-relative URI found
 *   there.
 * - **href**
 *   Use the value of this attribute unchanged (since this might link to a
 *   resource external to the application, the session identifier is **not**
 *   included).
 * - **page**
 *   Use the value of this attribute as a module-relative URI to the desired
 *   resource.
 */
public class IncludeTag extends ImportSupport {

  public IncludeTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    anchor = null;
    forward = null;
    href = null;
    page = null;
  }

  private @Nullable String anchor;
  private @Nullable String forward;
  private @Nullable String href;
  private @Nullable String page;

  @Override
  public int doStartTag() throws JspException {
    var module = ModuleUtils.getCurrent();
    if (hasText(page) && !hasText(forward) && !hasText(href)) {
      var url = page.startsWith("/") ? page : (module.getPrefix() + "/" + page);
      this.url = StringUtils.normalizeForwardPath(url);
    } else if (!hasText(page) && hasText(forward) && !hasText(href)) {
      var forwardConfig = module.findForwardConfig(forward);
      if (forwardConfig == null) throw new IllegalArgumentException(
        String.format(
          "Unknown forward name [%s] for module [%s].",
          forward,
          module.getPrefix()
        )
      );
      this.url = forwardConfig.getUrl();
    } else if (!hasText(page) && !hasText(forward) && hasText(href)) {
      this.url = href;
    } else {
      conflictingProperties();
    }
    return super.doStartTag();
  }

  private void conflictingProperties() {
    throw new IllegalArgumentException(
      String.format(
        "The <bean:include> tag accepts only one of the following properties:" +
        " href [%s], forward [%s], or page [%s].",
        href,
        forward,
        page
      )
    );
  }

  /**
   * (Required) Specifies the name of the scripting variable (and associated
   * page scope attribute) that will be made available with the value of the
   * specified web application resource.
   */
  public void setId(String id) {
    setVar(id);
  }

  /**
   * Optional anchor tag ("#xxx") to be added to the generated hyperlink.
   * Specify this value **without** any "#" character.
   */
  public void setAnchor(String anchor) {
    this.anchor = anchor;
  }

  /**
   * Logical name of a global **ActionForward** that contains the actual
   * content-relative URI of the resource to be included.
   */
  public void setForward(String forward) {
    this.forward = forward;
  }

  /**
   * Absolute URL (including the appropriate protocol prefix such as `http:`)
   * of the resource to be included. Because this URL could be external to the
   * current web application, the session identifier will **not** be included
   * in the request.
   */
  public void setHref(String href) {
    this.href = href;
  }

  /**
   * Module-relative URI (starting with a '/') of the web application resource
   * to be included.
   */
  public void setPage(String page) {
    this.page = page;
  }

  /**
   * Set to `true` if you want the current transaction control token included
   * in the generated URL for this include.
   */
  public void setTransaction(boolean transaction) {
    throw new UnsupportedOperationException();
  }
}
