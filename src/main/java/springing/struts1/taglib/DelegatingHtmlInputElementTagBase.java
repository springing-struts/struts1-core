package springing.struts1.taglib;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.InputTag;

public class DelegatingHtmlInputElementTagBase<TAG extends AbstractHtmlInputElementTag>
  extends DelegatingHtmlElementTagBase<TAG> {

  public DelegatingHtmlInputElementTagBase(TAG tag) {
    super(tag);
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    name = null;
    initialValue = null;
    property = null;
    errorKey = null;
    errorStyleClass = null;
    errorStyleId = null;
    errorStyle = null;
    awareNestedTag = false;
  }
  private @Nullable String name;
  private @Nullable String initialValue;
  private @Nullable String property;
  private @Nullable String errorKey;
  private @Nullable String errorStyleClass;
  private @Nullable String errorStyleId;
  private @Nullable String errorStyle;
  private boolean awareNestedTag;

  /**
   * The attribute name of the bean whose properties are consulted when
   * rendering the current value of this input field. If not specified, the
   * bean associated with the form tag we are nested within is utilized.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Valid only inside `logic:iterate` tag. If `true` then name of the html tag
   * will be rendered as `id[34].propertyName`. Number in brackets will be
   * generated for every iteration and taken from ancestor `logic:iterate` tag.
   */
  public void setIndexed(boolean indexed) {
    throw new UnsupportedOperationException();
  }

  /**
   * Value to which this field should be initialized. [Use the corresponding
   * bean property value]
   */
  public void setValue(String value) {
    this.initialValue = initialValue;
  }

  /**
   * Name of this input field, and the name of the corresponding bean property
   * if value is not specified. The corresponding bean property (if any) must
   * be of type String.
   */
  public void setProperty(String property) {
    getBaseTag().setPath(property);
    this.property = property;
  }

  public void setAwareNestedTag(boolean awareNestedTag) {
    this.awareNestedTag = awareNestedTag;
  }

  protected String processValue(@Nullable Object value) {
    return value == null ? "" : value.toString();
  }

  @Override
  public int doStartTag() throws JspException {
    var tag = getBaseTag();
    if (property != null) {
      tag.setPath(property);
    }
    var binding = StrutsDataBinding.onForm(
      getPageContext(),
      name,
      property,
      awareNestedTag,
      this::processValue
    );
    binding.setTag(tag);
    return super.doStartTag();
  }

  /**
   * The keyboard character used to move focus immediately to this element.
   */
  public void setAccesskey(String accesskey) {
    getBaseTag().setAccesskey(accesskey);
  }

  /**
   * The alternate text for this element.
   */
  public void setAlt(String alt) {
    if (getBaseTag() instanceof InputTag inputTag) {
      inputTag.setAlt(alt);
    }
  }

  /**
   * The message resources key of the alternate text for this element.
   */
  public void setAltKey(String altKey) {
    throw new UnsupportedOperationException();
  }

  /**
   * Set to `true` if this input field should be read only.
   */
  public void setReadonly(boolean readonly) {
    getBaseTag().setReadonly(readonly);
  }

  /**
   * Set to `true` if this input field should be disabled.
   */
  public void setDisabled(boolean disabled) {
    getBaseTag().setDisabled(disabled);
  }

  /**
   * JavaScript event handler executed when this element receives input focus.
   */
  public void setOnfocus(String onfocus) {
    getBaseTag().setOnfocus(onfocus);
  }

  /**
   * JavaScript event handler executed when this element loses input focus.
   */
  public void setOnblur(String onblur) {
    getBaseTag().setOnblur(onblur);
  }

  /**
   * JavaScript event handler executed when this element loses input focus and
   * its value has changed.
   */
  public void setOnchange(String onchange) {
    getBaseTag().setOnchange(onchange);
  }

  /**
   * Name of the bean (in any scope) under which our error messages have been
   * stored. If not present, the name specified by the `Globals.ERROR_KEY`
   * constant string will be used.
   * **N.B.**
   *   This is used in conjunction with the `errorStyle`, `errorStyleClass` and
   *   `errorStyleId` attributes and should be set to the same value as the
   *   `name` attribute on the `html:errors` tag.
   */
  public void setErrorKey(String errorKey) {
    this.errorKey = errorKey;
  }

  /**
   * CSS styles to be applied to this HTML element if an error exists for it.
   * **N.B.**
   *   If present, this overrides the `style` attribute in the event of an
   *   error.
   */
  public void setErrorStyleClass(String errorStyleClass) {
    this.errorStyleClass = errorStyleClass;
  }

  /**
   * Identifier to be assigned to this HTML element if an error exists for it
   * (renders an "id" attribute).
   * **N.B.**
   *   If present, this overrides the `styleId` attribute in the event of an
   *   error.
   */
  public void setErrorStyleId(String errorStyleId) {
    this.errorStyleId = errorStyleId;
  }

  /**
   * CSS styles to be applied to this HTML element if an error exists for it.
   * **N.B.** If present, this overrides the `style` attribute in the event of
   * an error.
   */
  public void setErrorStyle(String errorStyle) {
    this.errorStyle = errorStyle;
  }
}
