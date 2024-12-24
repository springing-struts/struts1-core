package springing.struts1.taglib;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

public abstract class StrutsHtmlElementTagBase extends AbstractHtmlElementTag implements BodyTag {

  public StrutsHtmlElementTagBase() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    try {
      setHtmlEscape(true);
    } catch (JspException e) {
      throw new RuntimeException(e); // won't happen
    }
    processesBodyContent = false;
    bodyContent = null;
    tagWriter = null;
    accesskey = null;
    onblur = null;
    onfocus = null;
    titleKey = null;
    bundle = null;
  }

  private boolean processesBodyContent;
  private @Nullable BodyContent bodyContent;
  private @Nullable TagWriter tagWriter;
  private @Nullable String accesskey;
  private @Nullable String onblur;
  private @Nullable String onfocus;
  private @Nullable String titleKey;
  private @Nullable String bundle;

  protected StrutsDataBinding createBinding() {
    return StrutsDataBinding.onScope(
      pageContext,
      "",
      "",
      false
    );
  }

  protected Object getBoundValueOrElse(Object defaultValue) {
    try {
      return requireNonNullElse(getBoundValue(), defaultValue);
    } catch (JspException e) {
      throw new IllegalStateException("Failed to get bound value to this tag.");
    }
  }

  @Override
  protected final int writeTagContent(TagWriter tagWriter) throws JspException {
    this.tagWriter = tagWriter;
    if (processesBodyContent) {
      return EVAL_BODY_BUFFERED;
    }
    doWriteTagContent();
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Declares that this class processes the content of this tag instead of
   * having it evaluated by the JSP engine.
   */
  protected void needsProcessingBodyContent() {
    this.processesBodyContent = true;
  }

  protected TagWriter getTagWriter() {
    return requireNonNull(tagWriter, "TagWriter is not initialized.");
  }

  protected void doWriteTagContent() throws JspException {
    var tagWriter = getTagWriter();
    tagWriter.startTag(getTagName());
    var binding = createBinding();
    binding.setTag(this);
    writeDefaultAttributes(tagWriter);
    for (var entry : getAdditionalAttributes().entrySet()) {
      var key = entry.getKey();
      var value = entry.getValue();
      if ("href".equals(key)) {
        tagWriter.writeAttribute(key, value);
        continue;
      }
      writeOptionalAttribute(tagWriter, key, value);
    }
    if (processesBodyContent) {
      var bodyText = getBodyTextForOutput();
      if (bodyText != null) {
        tagWriter.appendValue(writesRowContent() ? bodyText : getDisplayString(bodyText));
      }
    }
    tagWriter.forceBlock();
  }

  private boolean writesRowContent() {
    return "script".equals(getTagName());
  }

  @Override
  public int doEndTag() throws JspException {
    if (processesBodyContent) {
      doWriteTagContent();
    }
    getTagWriter().endTag();
    return EVAL_PAGE;
  }

  @Override
  public final void setBodyContent(BodyContent b) {
    this.bodyContent = b;
  }

  public final String readBodyContentAsText() {
    return (bodyContent == null) ? "" : bodyContent.getString().trim();
  }

  protected @Nullable String getBodyTextForOutput() {
    return null;
  }

  @Override
  public void doInitBody() throws JspException {
    //NOP
  }

  /**
   * The name of the enclosing tag.
   */
  protected abstract String getTagName();

  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = new HashMap<String, String>();
    attrs.put("accesskey", accesskey);
    attrs.put("onblur", onblur);
    attrs.put("onfocus", onfocus);
    return attrs;
  }

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

  @Override
  public @Nullable String getTitle() {
    var title = super.getTitle();
    if (title != null && titleKey != null) throw new IllegalArgumentException(
      "The title attribute and the titleKey attribute are mutually exclusive."
    );
    if (title != null) {
      return title;
    }
    if (titleKey == null) {
      return null;
    }
    return ModuleUtils.getCurrent().getMessageResources(bundle).getMessage(titleKey);
  }

  /**
   * The message resources key for the advisory title for this element.
   */
  public void setTitleKey(String titleKey) {
    this.titleKey = titleKey;
  }

  /**
   * The servlet context attributes key for the `MessageResources` instance to
   * use. If not specified, defaults to the application resources configured
   * for our action servlet.
   */
  public void setBundle(String bundle) {
    this.bundle = bundle;
  }

  public @Nullable String getBundle() {
    return bundle;
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
