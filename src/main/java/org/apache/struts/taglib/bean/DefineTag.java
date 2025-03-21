package org.apache.struts.taglib.bean;

import jakarta.servlet.jsp.JspException;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableAware;
import springing.struts1.taglib.JspVariableReference;
import springing.util.ObjectUtils;

import javax.servlet.jsp.PageContext;

import static org.springframework.util.StringUtils.hasText;

/**
 * Define a scripting variable based on the value(s) of the specified bean
 * property.
 * Create a new attribute (in the scope specified by the `toScope` property,
 * if any), and a corresponding scripting variable, both of which are named by
 * the value of the `id` attribute. The corresponding value to which this new
 * attribute (and scripting variable) is set are specified via use of exactly
 * one of the following approaches (trying to use more than one will result in
 * a `JspException` being thrown):
 * - **Specify a `name` attribute**
 *   (plus optional **property** and **scope** attributes) The created
 *   attribute and scripting variable will be of the type of the retrieved
 *   JavaBean property, unless it is a Java primitive type, in which case it
 *   will be wrapped in the appropriate wrapper class (i.e. int is wrapped by
 *   java.lang.Integer).
 * - **Specify a `value` attribute**
 *   The created attribute and scripting variable will be of type
 *   `java.lang.String`, set to the value of this attribute.
 * - **Specify nested body content**
 *   The created attribute and scripting variable will be of type
 *   `java.lang.String`, set to the value of the nested body content.
 * If a problem occurs while retrieving the specified bean property, a request
 * time exception will be thrown.
 * The `bean:define` tag differs from `jsp:useBean` in several ways,
 * including:
 * - Unconditionally creates (or replaces) a bean under the specified
 *   identifier.
 * - Can create a bean with the value returned by a property getter of
 *   a different bean (including properties referenced with a nested and/or
 *   indexed property name).
 * - Can create a bean whose contents is a literal string (or the result of
 *   a runtime expression) specified by the `value` attribute.
 * - Does not support nested content (such as `jsp:setProperty` tags) that are
 *   only executed if a bean was actually created.
 * ### USAGE NOTE ###
 * There is a restriction in the JSP 1.1 Specification that disallows using
 * the same value for an `id` attribute more than once in a single JSP page.
 * Therefore, you will not be able to use `bean:define` for the same bean
 * name more than once in a single page.
 * ### USAGE NOTE
 * If you use another tag to create the body content (e.g. bean:write), that
 * tag must return a non-empty String. An empty String equates to an empty
 * body or a null String, and a new scripting variable cannot be defined as
 * null. Your bean must return a non-empty String, or the define tag must be
 * wrapped within a logic tag to test for an empty or null value.
 * ### USAGE NOTE ###
 * You cannot use `bean:define` to **instantiate** a DynaActionForm
 * (type="org.apache.struts.action.DynaActionForm") with the properties
 * specified in the struts-config. The mechanics of creating the
 * dyna-properties is complex and cannot be handled by a no-argument
 * constructor. If you need to create an ActionForm this way, you must use a
 * conventional ActionForm.
 */
public class DefineTag extends SetSupport implements JspVariableAware {

  public DefineTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    ref = JspVariableReference.create();
    assignedValue = null;
    isValueAssigned = false;
    type = null;
  }

  private JspVariableReference ref = JspVariableReference.create();
  private @Nullable String assignedValue;
  private boolean isValueAssigned = false;
  private @Nullable Class<?> type;

  /**
   * (Required) Specifies the name of the scripting variable (and associated
   * page scope attribute) that will be made available with the value of the
   * specified property.
   */
  public void setId(String id) {
    setVar(id);
  }

  @Override
  public JspVariableReference getReference() {
    return ref;
  }

  @Override
  public int doEndTag() throws JspException {
    var value = isValueAssigned ? assignedValue
              : hasText(ref.getName()) ? resolveValue(PageContext.toJavaxNamespace(pageContext))
              : getBodyContent().getString();
    this.value = type == null ? value : ServletActionContext.current().convertValue(value, type);
    return super.doEndTag();
  }

  /**
   * Specifies the variable scope searched to retrieve the bean specified by
   * `name`. If not specified, the default rules applied by
   * `PageContext.findAttribute()` are applied.
   */
  public void setScope(String scope) {
    throw new UnsupportedOperationException();
  }

  /**
   * Specifies the variable scope into which the newly defined bean will be
   * created. If not specified, the bean will be created in page scope.
   */
  public void setToScope(String toScope) {
    throw new UnsupportedOperationException();
  }

  /**
   * Specifies the fully qualified class name of the value to be exposed as
   * the `id` attribute.
   */
  public void setType(String type) {
    this.type = ObjectUtils.classFor(type);
  }

  /**
   * The `java.lang.String` value to which the exposed bean should be set.
   * This attribute is required unless you specify the `name` attribute or
   * nested body content.
   */
  public void setValue(String value) {
    isValueAssigned = true;
    assignedValue = value;
  }
}
