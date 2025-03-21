package springing.struts1.taglib;

import jakarta.servlet.jsp.JspException;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.taglib.html.SelectTag;
import org.springframework.lang.Nullable;
import java.util.Iterator;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static springing.util.ObjectUtils.retrieveValue;

public abstract class StrutsOptionsTagBase extends StrutsHtmlElementTagBase {

  public StrutsOptionsTagBase() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    filter = true;
  }

  private boolean filter;

  /**
   * Returns a blank string, as this tag does not generate any enclosing tag.
   */
  @Override
  protected String getTagName() {
    return "";
  }

  @Override
  protected void doWriteTagContent() throws JspException {
    var selectTag = getEnclosingSelectTag();
    var optionBeans = getOptionBeans();
    if (optionBeans != null) {
      writeOptionsWithBeans(selectTag, optionBeans);
      return;
    }
    var optionValues = getOptionValues();
    if (optionValues == null) {
      return;
    }
    var optionLabels = requireNonNullElse(getOptionLabels(), optionValues);
    writeOptionsWithValues(selectTag, optionValues, optionLabels);
  }

  protected abstract @Nullable String getValuePropertyName();

  protected abstract @Nullable String getLabelPropertyName();

  protected abstract @Nullable Iterator<?> getOptionBeans();

  protected abstract @Nullable Iterator<?> getOptionValues();

  protected abstract @Nullable Iterator<?> getOptionLabels();

  private void writeOptionsWithBeans(
    SelectTag selectTag,
    Iterator<?> optionBeans
  ) throws JspException {
    var valuePropName = getValuePropertyName();
    var labelPropName = getLabelPropertyName();
    while (optionBeans.hasNext()) {
      var bean = optionBeans.next();
      if (bean == null) {
        continue;
      }
      var value = (String) retrieveValue(bean, valuePropName);
      if (value == null) {
        continue;
      }
      var label = getLabelPropertyName() == null ? value : (String) retrieveValue(bean, labelPropName);
      writeOptionTag(selectTag, value, label);
    }
  }

  private void writeOptionsWithValues(
    SelectTag selectTag,
    Iterator<?> optionValues,
    Iterator<?> optionLabels
  ) throws JspException {
    while (optionValues.hasNext()) {
      var value = optionValues.next();
      var label = optionLabels.next();
      if (value == null) continue;
      writeOptionTag(selectTag, value, label);
    }
  }

  private void writeOptionTag(
    SelectTag selectTag,
    Object value,
    @Nullable Object label
  ) throws JspException {
    var v = value.toString();
    var selected = selectTag.isMatch(v);
    var tagWriter = getTagWriter();
    tagWriter.startTag("option");
    tagWriter.writeAttribute("value", v);
    tagWriter.writeOptionalAttributeValue(
      ATTR_SELECTED, selected ? ATTR_SELECTED : null
    );
    var text = getDisplayString(requireNonNullElse(label, v));
    tagWriter.appendValue(text);
    tagWriter.endTag();
  }

  private static final String ATTR_SELECTED = "selected";

  @Override
  public int doEndTag() throws JspException {
    return EVAL_PAGE;
  }

  private SelectTag getEnclosingSelectTag() {
    return (SelectTag) requireNonNull(
      getPageContext().getAttribute(Constants.SELECT_KEY),
      "Failed to retrieve the enclosing select tag instance."
    );
  }

  /**
   * Set to `false` if you do NOT want the option labels filtered for sensitive
   * characters in HTML. By default, such values are filtered.
   */
  public void setFilter(boolean filter) {
    this.filter = filter;
  }
}
