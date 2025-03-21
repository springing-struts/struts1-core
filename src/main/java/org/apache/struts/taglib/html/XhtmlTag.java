package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import springing.struts1.taglib.StrutsHtmlElementTagBase;

/**
 * Renders HTML tags as XHTML.
 * Using this tag in a page tells all other html taglib tags to render
 * themselves as XHTML 1.0.  This is useful when composing pages with JSP
 * includes or Tiles. A html:html tag with xhtml="true"` has a similar effect.
 * **Note**:
 *   Included pages do not inherit the rendering style of the including page
 *   by default. Without setting the `scope` attribute, which defaults to
 *   `page` scope, each JSP fragment or Tile must use this tag to render as
 *   XHTML.
 */
public class XhtmlTag extends StrutsHtmlElementTagBase {

  public XhtmlTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    this.scope = null;
  }
  private @Nullable String scope;

  @Override
  protected String getTagName() {
    return "xhtml";
  }

  /**
   * Specifies the variable scope searched to retrieve the rendering setting.
   * If not specified, the default is page scope.
   */
  public void setScope(String scope) {
    this.scope = scope;
  }
}
