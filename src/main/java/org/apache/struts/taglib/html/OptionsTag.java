package org.apache.struts.taglib.html;

import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableReference;
import springing.struts1.taglib.StrutsOptionsTagBase;
import java.util.Iterator;
import static springing.util.ObjectUtils.asIterator;

/**
 * Render a Collection of Select Options.
 * Renders a set of HTML option elements, representing possible choices for a
 * `select` element. This tag can be used multiple times within a single
 * `html:select` element, either in conjunction with or instead of one or more
 * `html:option` or `html:optionsCollection` elements.
 * This tag operates in one of two major modes, depending on whether the
 * `collection` attribute is specified. If the `collection` attribute is
 * included, the following rules apply:
 * - The **collection** attribute is interpreted as the name of a JSP bean, in
 *   some scope, that itself represents a collection of individual beans, one
 *   per option value to be rendered.
 * - The **property** attribute is interpreted as the name of a property of the
 *   individual beans included in the collection, and is used to retrieve the
 *   value that will be returned to the server if this option is selected.
 * - The **labelProperty** attribute is interpreted as the name of a property
 *   of the individual beans included in the collection, and is used to
 *   retrieve the label that will be displayed to the user for this option. If
 *   the `labelProperty` attribute is not specified, the property named by the
 *   `property` attribute will be used to select both the value returned to the
 *   server and the label displayed to the user for this option.
 * If the `collection` attribute is not specified, the rules described in the
 * remainder of this section apply.
 * The collection of values actually selected depends on the presence or
 * absence of the `name` and `property` attributes. The following combinations
 * are allowed:
 * - **Only `name` is specified**
 *   The value of this attribute is the name of a JSP bean in some scope that
 *   is the collection.
 * - **Only `property` is specified**
 *   The value of this attribute is the name of a property of the `ActionForm`
 *   bean associated with our form, which will return the collection.
 * - **Both `name` and `property` are specified**
 *   The value of the `name` attribute identifies a JSP bean in some scope.
 *   The value of the `property` attribute is the name of some property of that
 *   bean which will return the collection.
 * The collection of labels displayed to the user can be the same as the option
 * values themselves, or can be different, depending on the presence or absence
 * of the `labelName` and `labelProperty` attributes. If this feature is used,
 * the collection of labels must contain the same number of elements as the
 * corresponding collection of values. The following combinations are allowed:
 * - **Neither `labelName` nor `labelProperty` is specified**
 *   The labels will be the same as the option values themselves.
 * - **Only `labelName` is specified**
 *   The value of this attribute is the name of a JSP bean in some scope that
 *   is the collection.
 * - **Only `labelProperty` is specified**
 *   The value of this attribute is the name of a property of the ActionForm
 *   bean associated with our form, which will return the collection.
 * - **Both `labelName` and `labelProperty` are specified**
 *   The value of the `labelName` attribute identifies a JSP bean in some
 *   scope.
 * The value of the `labelProperty` attribute is the name of some property of
 * that bean which will return the collection.
 * Note that this tag does not support a `styleId` attribute, as it would have
 * to apply the value to all the `option` elements created by this element,
 * which would mean that more than one `id` element might have the same value,
 * which the HTML specification says is illegal.
 */
public class OptionsTag extends StrutsOptionsTagBase {

  public OptionsTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    collection = null;
    labelName = null;
    labelProperty = null;
    name = null;
    property = null;
  }

  private @Nullable String collection;
  private @Nullable String labelName;
  private @Nullable String labelProperty;
  private @Nullable String name;
  private @Nullable String property;

  @Override
  protected @Nullable String getValuePropertyName() {
    return property;
  }

  @Override
  protected @Nullable String getLabelPropertyName() {
    return labelProperty;
  }

  @Override
  protected @Nullable Iterator<?> getOptionBeans() {
    if (collection == null) {
      return null;
    }
    return getItems(collection, null);
  }

  @Nullable
  @Override
  protected Iterator<?> getOptionValues() {
    return getItems(name, property);
  }

  @Nullable
  @Override
  protected Iterator<?> getOptionLabels() {
    return getItems(labelName, labelProperty);
  }

  private @Nullable Iterator<?> getItems(
    @Nullable String name,
    @Nullable String property
  ) {
    var ref = JspVariableReference.create();
    ref.setName(name);
    ref.setProperty(property);
    var values = ref.resolve(getPageContext());
    if (values == null) {
      return null;
    }
    return asIterator(values);
  }

  /**
   * Name of the JSP bean (in some scope) which is itself a Collection of other
   * beans, each of which has properties named by the `property` and
   * `labelProperty` attributes that are used to retrieve the value and label
   * for each option, respectively.
   */
  public void setCollection(String collection) {
    this.collection = collection;
  }

  /**
   * Name of the JSP bean (in some scope) containing the collection of labels
   * to be displayed to the user for these options.
   */
  public void setLabelName(String labelName) {
    this.labelName = labelName;
  }

  /**
   * Property of the form bean, or the bean specified by the labelName
   * attribute, that will return the collection of labels to be displayed to
   * the user for these options.
   */
  public void setLabelProperty(String labelProperty) {
    this.labelProperty = labelProperty;
  }

  /**
   * Name of the JSP bean (in some scope) containing the collection of values
   * to be returned to the server for these options. If not specified, the form
   * bean associated with our form is assumed.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Property of the form bean, or the bean specified by the name attribute,
   * that will return the collection of values to returned to the server for
   * these options.
   */
  public void setProperty(String property) {
    this.property = property;
  }
}
