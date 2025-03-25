package org.apache.struts.taglib.logic;

import static springing.util.ObjectUtils.isEmpty;

import springing.struts1.taglib.JspVariableAware;
import springing.struts1.taglib.JspVariableReference;
import springing.struts1.taglib.StrutsConditionalTagBase;

/**
 * Evaluate the nested body content of this tag if the requested variable is
 * either null or an empty string.
 * This tag evaluates its nested body content only if the specified value is
 * either absent (i.e. `null`), an empty string (i.e. a `java.lang.String` with
 * a length of zero), or an empty `java.util.Collection` or `java.util.Map`
 * (tested by the `.isEmpty()` method on the respective interface).
 * **JSTL**:
 *   The equivalent JSTL tag is `c:if` using the `empty` operator. For example,
 * <pre>{@code
 * <c:if test="${empty sessionScope.myBean.myProperty}">
 *   do something
 * </c:if>
 * }</pre>
 */
public class EmptyTag
  extends StrutsConditionalTagBase
  implements JspVariableAware {

  public EmptyTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    ref = JspVariableReference.create();
  }

  private JspVariableReference ref;

  @Override
  public JspVariableReference getReference() {
    return ref;
  }

  @Override
  protected boolean meetsCondition() {
    var value = resolveValue(getPageContext());
    return isEmpty(value);
  }
}
