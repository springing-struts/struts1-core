package springing.struts1.taglib;

import jakarta.servlet.jsp.PageContext;
import org.springframework.lang.Nullable;
import java.util.regex.Pattern;
import static springing.util.ServletRequestUtils.*;

public interface JspVariableAware {

  JspVariableReference getReference();

  class JspVariableReference {
    public static JspVariableReference create() {
      return new JspVariableReference();
    }
    private JspVariableReference() {}
    public @Nullable String name;
    public @Nullable String property;
    public boolean awareNestedTag = false;
  }

  default void setAwareNestedTag(boolean awareNestedTag) {
    getReference().awareNestedTag = awareNestedTag;
  }

  /**
   * The name of the JSP bean to be used as a variable (if
   * `property` is not specified), or whose property is to be
   * accessed (if `property` is specified).
   */
  default void setName(String name) {
    if (!OBJECT_REFERENCE.asMatchPredicate().test(name)) throw new IllegalArgumentException(
        "Invalid object reference name: " + name
    );
    getReference().name = name;
  }
  Pattern OBJECT_REFERENCE = Pattern.compile(
    "^[_a-zA-Z][_a-zA-Z0-9]*(\\.[_a-zA-Z][_a-zA-Z0-9]*)*$"
  );

  /**
   * The name of the bean property to be used as a variable.
   */
  default void setProperty(String property) {
    if (!PROPERTY_NAME.asMatchPredicate().test(property)) throw new IllegalArgumentException(
        "Invalid object property name: " + property
    );
    getReference().property = property;
  }
  Pattern PROPERTY_NAME = Pattern.compile(
    "^[_a-zA-Z][_a-zA-Z0-9]*$"
  );

  /**
   * The name of the cookie to be used as a variable.
   */
  default void setCookie(String cookie) {
    throw new UnsupportedOperationException();
  }

  /**
   * The name of the HTTP request header to be used as a variable.
   */
  default void setHeader(String header) {
    throw new UnsupportedOperationException();
  }

  /**
   * The name of the HTTP request parameter to be used as a variable.
   */
  default void setParameter(String parameter) {
    throw new UnsupportedOperationException();
  }

  /**
   * The scope to search for the bean named by the name property, or "any
   * scope" if null.
   */
  default void setScope(String scope) {
    throw new UnsupportedOperationException();
  }

  default @Nullable Object getValue(PageContext pageContext) {
    var ref = getReference();
    return resolveValueOnScope(
      ref.name,
      ref.property,
      ref.awareNestedTag,
      pageContext
    );
  }
}
