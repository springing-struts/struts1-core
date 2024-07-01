package springing.struts1.taglib.logic;

import jakarta.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.el.core.IfTag;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.DelegatingTagBase;

import java.util.regex.Pattern;

/**
 * Evaluate the nested body content of this tag if the specified value is not
 * present for this request.
 */
public class NotPresentTag extends DelegatingTagBase<IfTag> {

  public NotPresentTag() {
    super(new IfTag());
  }

  /**
   * The name of the cookie to be used as a variable.
   */
  public void setCookie(String cookie) {
    throw new UnsupportedOperationException();
  }

  /**
   * The name of the HTTP request header to be used as a variable.
   */
  public void setHeader(String header) {
    throw new UnsupportedOperationException();
  }

  /**
   * The name of the JSP bean to be used as a variable (if
   * `property` is not specified), or whose property is to be
   * accessed (if `property` is specified).
   */
  public void setName(String name) {
    if (!OBJECT_REFERENCE.asMatchPredicate().test(name)) throw new IllegalArgumentException(
      "Invalid object reference name: " + name
    );
    this.name = name;
  }
  private @Nullable String name;


  /**
   * The name of the HTTP request parameter to be used as a variable.
   */
  public void setParameter(String parameter) {
    throw new UnsupportedOperationException();
  }

  /**
   * The name of the bean property to be used as a variable.
   */
  public void setProperty(String property) {
    if (!PROPERTY_NAME.asMatchPredicate().test(name)) throw new IllegalArgumentException(
        "Invalid object property name: " + name
    );
    this.property = property;
  }
  private @Nullable String property = null;

  /**
   * The name of the security role to be checked for.
   */
  public void setRole(String role) {
    throw new UnsupportedOperationException();
  }

  /**
   * The scope to search for the bean named by the name property, or "any
   * scope" if null.
   */
  public void setScope(String scope) {
    getBaseTag().setScope(scope);
  }

  /**
   * The user principal name to be checked for.
   */
  public void setUser(String user) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int doStartTag() throws JspException {
    if (name == null || name.isEmpty()) {
      throw new IllegalStateException("name is required.");
    }
    var hasProperty = property != null && !property.isEmpty();
    var reference = name + (hasProperty ? ("." + property) : "");
    getBaseTag().setTest("${empty " + reference +  "}");
    return super.doStartTag();
  }

  private static final Pattern PROPERTY_NAME = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*$");
  private static final Pattern OBJECT_REFERENCE = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*(\\.[_a-zA-Z][_a-zA-Z0-9]*)*$");
}
