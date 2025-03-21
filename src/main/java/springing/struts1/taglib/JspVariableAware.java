package springing.struts1.taglib;

import org.springframework.lang.Nullable;

import javax.servlet.jsp.PageContext;

public interface JspVariableAware {

  JspVariableReference getReference();

  default void setAwareNestedTag(boolean awareNestedTag) {
    getReference().setAwareNestedTag(awareNestedTag);
  }

  /**
   * The name of the JSP bean to be used as a variable (if `property` is not
   * specified), or whose property is to be accessed (if `property` is
   * specified).
   */
  default void setName(String name) {
    getReference().setName(name);
  }

  /**
   * The name of the bean property to be used as a variable.
   */
  default void setProperty(String property) {
    getReference().setProperty(property);
  }

  /**
   * The name of the cookie to be used as a variable.
   */
  default void setCookie(String cookie) {
    getReference().setCookie(cookie);
  }

  /**
   * The name of the HTTP request header to be used as a variable.
   */
  default void setHeader(String header) {
    getReference().setHeader(header);
  }

  /**
   * The name of the HTTP request parameter to be used as a variable.
   */
  default void setParameter(String parameter) {
    getReference().setParameter(parameter);
  }

  /**
   * The scope to search for the bean named by the name property, or "any
   * scope" if null.
   */
  default void setScope(@Nullable String scope) {
    getReference().setScope(scope);
  }

  default @Nullable Object resolveValue(PageContext pageContext) {
    return getReference().resolve(pageContext);
  }
}
