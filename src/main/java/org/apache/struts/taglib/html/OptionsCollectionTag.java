package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableReference;
import springing.struts1.taglib.StrutsOptionsTagBase;
import java.util.Collections;
import java.util.Iterator;

import static springing.util.ObjectUtils.asIterator;

/**
 * Render a Collection of Select Options
 * Renders a set of HTML `option` elements, representing possible choices for a
 * `select` element. This tag can be used multiple times within a single
 * `html:select` element, either in conjunction with or instead of one or more
 * `html:option` or `html:options` elements.
 * This tag operates on a collection of beans, where each bean has a `label`
 * property and a `value` property. The actual names of these properties can be
 * configured using the `label` and `value` attributes of this tag.
 * This tag differs from the `html:options` tag in that it makes more
 * consistent use of the `name` and `property` attributes, and allows the
 * collection to be more easily obtained from the enclosing form bean.
 * Note that this tag does not support a `styleId` attribute, as it would have
 * to apply the value to all the `option` elements created by this element,
 * which would mean that more than one `id` element might have the same value,
 * which the HTML specification says is illegal.
 */
public class OptionsCollectionTag extends StrutsOptionsTagBase {

  public OptionsCollectionTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    value = DEFAULT_VALUE_PROPERTY;
    label = DEFAULT_LABEL_PROPERTY;
    name = null;
    property = null;
  }

  private String value = DEFAULT_VALUE_PROPERTY;
  private String label = DEFAULT_LABEL_PROPERTY;
  private @Nullable String name;
  private @Nullable String property;

  protected @Nullable String getValuePropertyName() {
    return value;
  }

  @Override
  protected @Nullable String getLabelPropertyName() {
    return label;
  }

  @Override
  protected @Nullable Iterator<?> getOptionBeans() {
    var ref = JspVariableReference.create();
    ref.setName(name);
    ref.setProperty(property);
    var beans = ref.resolve(getPageContext());
    if (beans == null) {
      return Collections.emptyIterator();
    }
    return asIterator(beans);
  }

  @Nullable
  @Override
  protected Iterator<?> getOptionValues() {
    return null;
  }

  @Nullable
  @Override
  protected Iterator<?> getOptionLabels() {
    return null;
  }

  /**
   * The property of the bean within the collection which represents the label
   * to be rendered for each option. Defaults to "label".
   */
  public void setLabel(String label) {
    this.label = label;
  }
  private static final String DEFAULT_LABEL_PROPERTY = "label";

  /**
   * The attribute name of the bean whose properties are consulted when
   * rendering the current value of this input field. If not specified, the
   * bean associated with the form tag we are nested within is utilized.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * The property of the form bean, or the bean specified by the name
   * attribute, that will return the collection of objects to be rendered for
   * these options.
   */
  public void setProperty(String property) {
    this.property = property;
  }

  /**
   * The property of the bean within the collection which represents the value
   * to be rendered for each option. Defaults to "value".
   */
  public void setValue(String value) {
    this.value = value;
  }
  private static final String DEFAULT_VALUE_PROPERTY = "value";
}
