package org.apache.struts.taglib.bean;

import static springing.util.ObjectUtils.getSize;

import jakarta.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableAware;
import springing.struts1.taglib.JspVariableReference;

/**
 * Define a bean containing the number of elements in a Collection or Map.
 * Given a reference to an array, Collection or Map, creates a new bean, of
 * type `java.lang.Integer`, whose value is the number of elements in that
 * collection. You can specify the collection to be counted in any one of the
 * following ways:
 * - As a runtime expression specified as the value of the `collection`
 *   attribute.
 * - As a JSP bean specified by the `name` attribute.
 * - As the property, specified by the `property` attribute, of the JSP bean
 *   specified by the `name` attribute.
 */
public class SizeTag extends SetSupport implements JspVariableAware {

  public SizeTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    ref = JspVariableReference.create();
    collection = null;
  }

  @Override
  public JspVariableReference getReference() {
    return ref;
  }

  private JspVariableReference ref = JspVariableReference.create();
  private @Nullable Object collection;

  @Override
  public int doEndTag() throws JspException {
    var items = collection != null
      ? collection
      : resolveValue(PageContext.toJavaxNamespace(pageContext));
    value = getSize(items);
    return super.doEndTag();
  }

  /**
   * A runtime expression that evaluates to an array, a Collection, or a Map.
   */
  public void setCollection(Object collection) {
    this.collection = collection;
  }

  /**
   * [Required] The name of a page scope JSP bean, of type `java.lang.Integer`,
   * that will be created to contain the size of the underlying collection
   * being counted.
   */
  public void setId(String id) {
    setVar(id);
  }
}
