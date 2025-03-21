package springing.struts1.taglib;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import java.util.Map;

import static java.util.Objects.requireNonNullElse;

public abstract class StrutsInputElementTagBase extends StrutsHtmlElementTagBase {

  public StrutsInputElementTagBase() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  @Override
  protected String getTagName() {
    return "input";
  }

  protected abstract @Nullable String getType();

  @Override
  protected Map<String, String> getAdditionalAttributes() throws JspException {
    var attrs = super.getAdditionalAttributes();
    var type = getType();
    var value = getDisplayString(getBoundValue(), getPropertyEditor());
    attrs.put("value", processFieldValue(getName(), value, requireNonNullElse(type, "text")));
    attrs.put("type", getType());
    attrs.put("alt", alt);
    attrs.put("readonly", readonly ? "readonly" : null);
    attrs.put("disabled", disabled ? "disabled" : null);
    attrs.put("onchange", onchange);
    return attrs;
  }

  private void init() {
    name = null;
    defaultValue = null;
    property = null;
    errorKey = null;
    errorStyleClass = null;
    errorStyleId = null;
    errorStyle = null;
    awareNestedTag = false;
    readonly = false;
    disabled = false;
    onchange = null;
  }
  private @Nullable String name;
  private @Nullable String defaultValue;
  private @Nullable String property;
  private @Nullable String errorKey;
  private @Nullable String errorStyleClass;
  private @Nullable String errorStyleId;
  private @Nullable String errorStyle;
  private boolean awareNestedTag;
  private @Nullable String alt;
  private boolean readonly = false;
  private boolean disabled = false;
  private @Nullable String onchange;

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
    this.defaultValue = value;
  }

  public String getDefaultValue() {
    return requireNonNullElse(defaultValue, "");
  }

  /**
   * Name of this input field, and the name of the corresponding bean property
   * if value is not specified. The corresponding bean property (if any) must
   * be of type String.
   */
  public void setProperty(String property) {
    setPath(property);
    this.property = property;
  }

  public void setAwareNestedTag(boolean awareNestedTag) {
    this.awareNestedTag = awareNestedTag;
  }

  /**
   * Converts the value of the property bound to this tag into the content
   * of the `value` attribute before rendering the tag.
   */
  protected Object processBoundValue(@Nullable Object propertyValue) {
    return requireNonNullElse(
      propertyValue, getDefaultValue()
    );
  }

  @Override
  protected StrutsDataBinding createBinding() {
    if (property != null) {
      setPath(property);
    }
    return StrutsDataBinding.onForm(
      getPageContext(),
      name,
      property,
      awareNestedTag,
      this::processBoundValue
    );
  }

  /**
   * The alternate text for this element.
   */
  public void setAlt(String alt) {
    this.alt = alt;
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
    this.readonly = readonly;
  }

  /**
   * Set to `true` if this input field should be disabled.
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  /**
   * JavaScript event handler executed when this element loses input focus and
   * its value has changed.
   */
  public void setOnchange(String onchange) {
    this.onchange = onchange;
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