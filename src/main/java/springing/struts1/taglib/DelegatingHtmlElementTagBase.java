package springing.struts1.taglib;

import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;

public class DelegatingHtmlElementTagBase<TAG extends AbstractHtmlElementTag> extends DelegatingTagBase<TAG>{

  public DelegatingHtmlElementTagBase(TAG tag) {
    super(tag);
  }

  /**
   * The direction for weak/neutral text for this element.
   */
  public void setDir(String dir) {
    getBaseTag().setDir(dir);
  }

  /**
   * The language code for this element.
   */
  public void setLang(String lang) {
    getBaseTag().setLang(lang);
  }

  /**
   * JavaScript event handler executed when this element receives a mouse click.
   */
  public void setOnclick(String onclick) {
    getBaseTag().setOnclick(onclick);
  }

  /**
   * JavaScript event handler executed when this element receives a mouse
   * double click.
   */
  public void setOndblclick(String ondblclick) {
    getBaseTag().setOndblclick(ondblclick);
  }


  /**
   * JavaScript event handler executed when this element has focus and a key
   * is depressed.
   */
  public void setOnkeydown(String onkeydown) {
    getBaseTag().setOnkeydown(onkeydown);
  }

  /**
   * JavaScript event handler executed when this element has focus and a key
   * is depressed and released.
   */
  public void setOnkeypress(String onkeypress) {
    getBaseTag().setOnkeypress(onkeypress);
  }

  /**
   * JavaScript event handler executed when this element has focus and a key
   * is released.
   */
  public void setOnkeyup(String onkeyup) {
    getBaseTag().setOnkeyup(onkeyup);
  }

  /**
   * JavaScript event handler executed when this element is under the mouse
   * pointer and a mouse button is depressed.
   */
  public void setOnmousedown(String onmousedown) {
    getBaseTag().setOnmousedown(onmousedown);
  }

  /**
   * JavaScript event handler executed when this element is under the mouse
   * pointer and the pointer is moved.
   */
  public void setOnmousemove(String onmousemove) {
    getBaseTag().setOnmousemove(onmousemove);
  }

  /**
   * JavaScript event handler executed when this element was under the mouse
   * pointer but the pointer was moved outside the element.
   */
  public void setOnmouseout(String onmouseout) {
    getBaseTag().setOnmouseout(onmouseout);
  }

  /**
   * JavaScript event handler executed when this element was not under the
   * mouse pointer but the pointer is moved inside the element.
   */
  public void setOnmouseover(String onmouseover) {
    getBaseTag().setOnmouseover(onmouseover);
  }

  /**
   * JavaScript event handler executed when this element is under the mouse
   * pointer and a mouse button is released.
   */
  public void setOnmouseup(String onmouseup) {
    getBaseTag().setOnmouseup(onmouseup);
  }

  /**
   * CSS styles to be applied to this HTML element.
   * If present, the `errorStyle` overrides this attribute in the event of an
   * error for the element.
   */
  public void setStyle(String style) {
    getBaseTag().setCssStyle(style);
  }

  /**
   * CSS stylesheet class to be applied to this HTML element (renders a "class"
   * attribute).
   * If present, the `errorStyleClass` overrides this attribute in the event of
   * an error for the element.
   */
  public void setStyleClass(String styleClass) {
    getBaseTag().setCssClass(styleClass);
  }

  /**
   * Identifier to be assigned to this HTML element (renders an "id"
   * attribute).
   * If present, the `errorStyleId` overrides this attribute in the event of an
   * error for the element.
   */
  public void setStyleId(String styleId) {
    getBaseTag().setId(styleId);
  }

  /**
   * The tab order (ascending positive integers) for this element.
   */
  public void setTabindex(String tabindex) {
    getBaseTag().setTabindex(tabindex);
  }

  /**
   * The advisory title for this element.
   */
  public void setTitle(String title) {
    getBaseTag().setTitle(title);
  }

  /**
   * The message resources key for the advisory title for this element.
   */
  public void setTitleKey(String titleKey) {
    throw new UnsupportedOperationException();
  }

  /**
   * The servlet context attributes key for the `MessageResources` instance to
   * use. If not specified, defaults to the application resources configured
   * for our action servlet.
   */
  public void setBundle(String bundle) {
    throw new UnsupportedOperationException();
  }
}
