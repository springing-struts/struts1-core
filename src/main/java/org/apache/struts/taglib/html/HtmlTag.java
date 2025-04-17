package org.apache.struts.taglib.html;

import jakarta.servlet.jsp.JspException;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;
import springing.struts1.taglib.StrutsHtmlElementTagBase;

/*
 * Renders an HTML <html> element with appropriate language attributes if
 * there is a current Locale available in the user's session.
 */
public class HtmlTag extends StrutsHtmlElementTagBase {

  public HtmlTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    lang = false;
    xhtml = false;
    xhtmlVersion = "1.0";
  }

  private boolean lang;
  private boolean xhtml;
  private String xhtmlVersion;

  @Override
  protected String getTagName() {
    return "html";
  }

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    if (lang) {
      attrs.put("lang", LocaleContextHolder.getLocale().getCountry());
    }
    if (xhtml) {
      attrs.put("xmlns", "http://www.w3.org/1999/xhtml");
      attrs.put("xml:lang", LocaleContextHolder.getLocale().getCountry());
    }
    return attrs;
  }

  /**
   * Renders a lang attribute with the locale stored in the user's session.
   * If not found in the session, the language from the `Accept-Language` HTTP
   * header is used. If still not found, the default language for the server is
   * used.
   */
  public void setLang(boolean lang) {
    this.lang = lang;
  }

  /**
   * Set to `true` in order to render `xml:lang` and `xmlns` attributes on the
   * generated `html` element. This also causes all other html tags to render
   * as XHTML 1.0 (the `html:xhtml` tag has a similar purpose).
   */
  public void setXhtml(boolean xhtml) {
    this.xhtml = xhtml;
  }

  /**
   * Sets the version of XHTML to render in the String form `MAJOR.MINOR`.
   * The default is "1.0" when not specified.
   */
  public void setXhtmlVersion(String version) {
    throw new UnsupportedOperationException();
  }
}
