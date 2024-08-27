package springing.struts1.taglib;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import java.util.Map;

public abstract class HtmlElementTagBase extends AbstractHtmlElementTag {

  public HtmlElementTagBase() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    tagWriter = null;
    accesskey = null;
    onblur = null;
    onfocus = null;
  }

  private @Nullable TagWriter tagWriter;
  private @Nullable String accesskey;
  private @Nullable String onblur;
  private @Nullable String onfocus;

  @Override
  protected int writeTagContent(TagWriter tagWriter) throws JspException {
    tagWriter.startTag(getTagName());
    StrutsDataBinding.onScope(pageContext, "", "", false).setTag(this);
    writeOptionalAttributes(tagWriter);
    writeOptionalAttribute(tagWriter, "accesskey", accesskey);
    writeOptionalAttribute(tagWriter, "onblur", onblur);
    writeOptionalAttribute(tagWriter, "onfocus", onfocus);
    for (var entry : getAdditionalAttributes().entrySet()) {
      var key = entry.getKey();
      var value = entry.getValue();
      if ("href".equals(key)) {
        tagWriter.writeAttribute(key, value);
        continue;
      }
      writeOptionalAttribute(tagWriter, key, value);
    }
    tagWriter.forceBlock();
    this.tagWriter = tagWriter;
    return EVAL_BODY_INCLUDE;
  }

  @Override
  public int doEndTag() throws JspException {
    if (tagWriter == null) throw new IllegalStateException();
    tagWriter.endTag();
    return EVAL_PAGE;
  }

  protected abstract String getTagName();

  protected abstract Map<String, String> getAdditionalAttributes();

  /**
   * CSS styles to be applied to this HTML element.
   * If present, the `errorStyle` overrides this attribute in the event of an
   * error for the element.
   */
  public void setStyle(String style) {
    setCssStyle(style);
  }

  /**
   * CSS stylesheet class to be applied to this HTML element (renders a "class"
   * attribute).
   * If present, the `errorStyleClass` overrides this attribute in the event of
   * an error for the element.
   */
  public void setStyleClass(String styleClass) {
    setCssClass(styleClass);
  }

  /**
   * Identifier to be assigned to this HTML element (renders an "id"
   * attribute).
   * If present, the `errorStyleId` overrides this attribute in the event of an
   * error for the element.
   */
  public void setStyleId(String styleId) {
    setId(styleId);
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

  /**
   * The keyboard character used to move focus immediately to this element.
   */
  public void setAccesskey(String accesskey) {
    this.accesskey = accesskey;
  }

  /**
   * avaScript event handler that is executed when this element loses input
   * focus.
   */
  public void setOnblur(String onblur) {
    this.onblur = onblur;
  }

  /**
   * JavaScript event handler that is executed when this element receives input
   * focus.
   */
  public void setOnfocus(String onfocus) {
    this.onfocus = onfocus;
  }
}
