package org.apache.struts.taglib.html;

import static java.util.Objects.requireNonNull;

import jakarta.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.taglibs.standard.tag.common.core.OutSupport;
import springing.struts1.taglib.UrlBuilder;
import springing.struts1.taglib.UrlBuilderSupport;

/**
 * Render a URI.
 *  Renders a request URI based on exactly the same rules as the `html:link` tag
 * does, but without creating the `a` hyperlink. This value is useful when you
 * want to generate a string constant for use by a JavaScript procedure.
 */
public class RewriteTag
  extends OutSupport
  implements UrlBuilderSupport, ParamTagParent {

  public RewriteTag() {
    init();
  }

  private void init() {
    this.urlBuilder = new UrlBuilder();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  @Override
  public UrlBuilder getUrlBuilder() {
    return urlBuilder;
  }

  private UrlBuilder urlBuilder;

  @Override
  public int doStartTag() throws JspException {
    value = urlBuilder.buildUrl(getPageContext());
    super.doStartTag();
    return EVAL_BODY_INCLUDE;
  }

  private PageContext getPageContext() {
    return PageContext.toJavaxNamespace(requireNonNull(pageContext));
  }

  @Override
  public void handleParam(ParamTag param) {
    addParam(param.getName(), param.getValue());
  }
}
