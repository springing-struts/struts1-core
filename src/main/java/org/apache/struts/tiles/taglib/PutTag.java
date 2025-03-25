package org.apache.struts.tiles.taglib;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.jsp.JspException;
import java.util.Objects;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.tiles.config.TilesAttribute;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableReference;

/**
 * Put an attribute into tile/component/template context.
 * Define an attribute to pass to tile/component/template. This tag can only
 * be used inside 'insert' or 'definition' tag. Value (or content) is specified
 * using attribute 'value' (or 'content'), or using the tag body. It is also
 * possible to specify the type of the value:
 * - string:
 *     Content is written directly.
 * - page | template:
 *     Content is included from specified URL. Name is used as a URL.
 * - definition:
 *     Content come from specified definition (from factory). Name is used as
 *     definition name.
 * If 'type' attribute is not specified, content is `untyped`, unless it comes
 * from a typed bean.
 * Note that using 'direct="true"' is equivalent to 'type="string"'.
 */
public class PutTag extends TagSupport {

  public PutTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    this.name = null;
    this.value = null;
    this.ref = JspVariableReference.create();
  }

  private @Nullable String name;
  private @Nullable String value;
  private JspVariableReference ref;

  @Override
  public int doStartTag() throws JspException {
    var tilesDefinition = ServletActionContext.current().getTilesDefinition();
    var attribute = new TilesAttribute(getAttributeName(), getValue());
    tilesDefinition.getAttributes().put(attribute.getName(), attribute);
    return super.doStartTag();
  }

  private String getAttributeName() {
    if (!hasText(name)) throw new IllegalArgumentException(
      "The name attribute is required for the <tiles:put> tag."
    );
    return name;
  }

  /**
   * Name of the attribute.
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    var v = (ref.getName() != null) ? ref.resolve(getPageContext()) : value;
    return Objects.toString(v, "");
  }

  private PageContext getPageContext() {
    return requireNonNull(pageContext);
  }

  /**
   * Attribute value. Could be a String or an Object.
   * Value can come from a direct assignment (value="aValue") or from a bean.
   * One of 'value' 'content' or 'beanName' must be present.
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Content that's put into tile scope.
   * Synonym to value. Attribute added for compatibility with JSP Template.
   */
  public void setContent(String content) {
    setValue(content);
  }

  /**
   * Determines how content is handled: true means content is printed **direct**
   */
  public void setDirect(boolean direct) {
    throw new UnsupportedOperationException();
  }

  /**
   * Specify content type: string, page, template or definition.
   * - string:
   *     Content is printed directly.
   * - page | template:
   *     Content is included from specified URL. Name is used as a URL.
   * - definition:
   *     Value is the name of a definition defined in factory (xml file).
   *     Definition will be searched in the inserted tile, in a
   *     {@code <tiles:insert attribute="attributeName" />} tag, where
   *     'attributeName' is the name used for this tag.
   */
  public void setType(String type) {}

  /**
   * Name of the bean used as value. Bean is retrieved from specified context,
   * if any. Otherwise, method pageContext.findAttribute is used.
   * If beanProperty is specified, retrieve value from the corresponding bean
   * property.
   */
  public void setBeanName(String beanName) {
    ref.setName(beanName);
  }

  /**
   * Bean property name. If specified, value is retrieve from this property.
   * Support nested/indexed properties.
   */
  public void setBeanProperty(String beanProperty) {
    ref.setProperty(beanProperty);
  }

  /**
   * Scope into which bean is searched.
   * If not specified, method pageContext.findAttribute is used. Scope can be
   * any JSP scope, 'tile', 'component', or 'template'. In these three later
   * cases, bean is search in tile/component/template context.
   */
  public void setBeanScope(String beanScope) {
    ref.setScope(beanScope);
  }

  /**
   * If the user is in the specified role, the tag is taken into account;
   * otherwise, the tag is ignored (skipped).
   */
  public void setRole(String role) {
    throw new UnsupportedOperationException();
  }
}
