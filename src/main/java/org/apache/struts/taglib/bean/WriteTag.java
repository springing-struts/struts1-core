package org.apache.struts.taglib.bean;

import jakarta.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.OutSupport;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableAware;

/**
 * Render the value of the specified bean property to the current JspWriter.
 * Retrieve the value of the specified bean property, and render it to the
 * current JspWriter as a String by the ways:
 * - If `format` attribute exists then value will be formatted on base of
 *   format string from `format` attribute and default system locale.
 * - If in resources exists format string for value data type (view `format`
 *   attribute description) then value will be formatted on base of format
 *   string from resources. Resources bundle and target locale can be specified
 *   with `bundle` and `locale` attributes. If nothing specified then default
 *   resource bundle and current user locale will be used.
 * - If there is a PropertyEditor configured for the property value's class,
 *   the `getAsText()` method will be called.
 * - Otherwise, the usual `toString()` conversions will be applied.
 * When a format string is provided, numeric values are formatted using the
 * `java.text.DecimalFormat` class; if the format string came from a resource,
 * the `applyLocalisedPattern()` method is used, and `applyPattern()` is used
 * otherwise. Dates are formatted using the `SimpleDateFormat` class. For
 * details of the specific format patterns, please see the Javadocs for those
 * classes.
 * If a problem occurs while retrieving the specified bean property, a request
 * time exception will be thrown.
 */
public class WriteTag extends OutSupport implements JspVariableAware {

  public WriteTag() {
    init();
  }

  private void init() {
    bundle = null;
    filter = true;
    format = null;
    formatKey = null;
    ignore = true;
    locale = null;
    ref = JspVariableReference.create();
  }
  private @Nullable String bundle;
  private boolean filter;
  private @Nullable String format;
  private @Nullable String formatKey;
  private boolean ignore;
  private @Nullable String locale;
  private JspVariableReference ref;

  @Override
  public void release() {
    super.release();
    init();
  }

  /**
   * The name of the application scope bean under which the `MessageResources`
   * object containing our messages is stored.
   */
  public void setBundle(String bundle) {
    this.bundle = bundle;
  }

  /**
   * If this attribute is set to `true`, the rendered property value will be
   * filtered for characters that are sensitive in HTML, and any such
   * characters will be replaced by their entity equivalents.
   */
  public void setFilter(boolean filter) {
    this.filter = filter;
  }

  /**
   * Specifies the format string to use to convert bean or property value to
   * the `String`. If nothing specified, then default format string for value
   * data type will be searched in message resources by according key.
   * ### Key to search format string for each Data types
   * **org.apache.struts.taglib.bean.format.int**
   *   - java.lang.Byte
   *   - java.lang.Short
   *   - java.lang.Integer
   *   - java.lang.Long,
   *   - java.math.BigInteger
   * **org.apache.struts.taglib.bean.format.float**
   *   - java.lang.Float
   *   - java.lang.Double
   *   - java.math.BigDecimal
   * **org.apache.struts.taglib.bean.format.sql.timestamp**
   *   - java.sql.Timestamp
   * **org.apache.struts.taglib.bean.format.sql.date**
   *   - java.sql.Date<
   * **org.apache.struts.taglib.bean.format.sql.time**
   *   - java.sql.Time
   * **org.apache.struts.taglib.bean.format.date**
   *   - java.util.Date<
   * ### Default format strings in resources can be written as
   * <pre>
   * org.apache.struts.taglib.bean.format.int=######
   * org.apache.struts.taglib.bean.format.float=######,####
   * org.apache.struts.taglib.bean.format.sql.timestamp=hh 'o''clock' a, zzzz
   * org.apache.struts.taglib.bean.format.sql.date=EEE, MMM d, ''yy
   * org.apache.struts.taglib.bean.format.sql.time=h:mm a
   * org.apache.struts.taglib.bean.format.date=hh 'o''clock' a, zzzz
   * </pre>
   * values for resource file entries are standard Java format strings for
   * date, time and number values.
   */
  public void setFormat(String format) {
    this.format = format;
  }

  /**
   * Specifies the key to search format string in application resources.
   */
  public void setFormatKey(String formatKey) {
    this.formatKey = formatKey;
  }

  /**
   * If this attribute is set to `true`, and the bean specified by the `name`
   * and `scope` attributes does not exist, simply return without writing
   * anything. If this attribute is set to `false`, a runtime exception to be
   * thrown, consistent with the other tags in this tag library.
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * The name of the session scope bean under which our currently selected
   * `Locale` object is stored.
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  @Override
  public int doStartTag() throws JspException {
    escapeXml = filter;
    value = getValue(pageContext);
    return super.doStartTag();
  }

  @Override
  public JspVariableReference getReference() {
    return ref;
  }
}
