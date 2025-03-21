package springing.struts1.taglib;

import org.springframework.lang.Nullable;

import javax.servlet.jsp.PageContext;
import java.util.Map;
import java.util.regex.Pattern;

import static jakarta.servlet.jsp.PageContext.*;
import static java.lang.String.format;
import static org.apache.struts.chain.contexts.ServletActionContext.resolveValueOnScope;
import static org.springframework.util.StringUtils.hasText;
import static springing.util.ObjectUtils.setProperties;

/**
 * A class that represents a reference to a variable or property in a JSP context.
 * This class can resolve values from different scopes, such as cookies, headers,
 * request parameters, and also evaluate property paths.
 */
public class JspVariableReference {

  /**
   * Factory method to create a new instance of {@link JspVariableReference}.
   *
   * @return a new {@link JspVariableReference} instance.
   */
  public static JspVariableReference create() {
    return new JspVariableReference();
  }

  // Private constructor to prevent direct instantiation.
  private JspVariableReference() {
  }

  /**
   * Gets the name of the object reference.
   */
  public @Nullable String getName() {
    return name;
  }

  /**
   * Sets the name of the object reference.
   * The name must match the pattern defined by {@link #PROPERTY_PATH}.
   * @throws IllegalArgumentException if the name is invalid.
   */
  public void setName(@Nullable String name) {
    if (name != null && !PROPERTY_PATH.asMatchPredicate().test(name)) {
      throw new IllegalArgumentException("Invalid object reference name: " + name);
    }
    this.name = name;
  }

  private @Nullable String name;

  /**
   * Gets the property path to be resolved on the object reference.
   */
  public @Nullable String getProperty() {
    return property;
  }

  /**
   * Sets the property path to be resolved on the object reference.
   * The property path must match the pattern defined by {@link #PROPERTY_PATH}.
   * @throws IllegalArgumentException if the property path is invalid.
   */
  public void setProperty(@Nullable String property) {
    if (property != null && !PROPERTY_PATH.asMatchPredicate().test(property)) {
      throw new IllegalArgumentException("Invalid object property path: " + property);
    }
    this.property = property;
  }

  private @Nullable String property;

  /**
   * Sets whether the tag is aware of nested tags.
   */
  public void setAwareNestedTag(boolean awareNestedTag) {
    this.awareNestedTag = awareNestedTag;
  }

  // Flag to indicate if the tag is aware of nested tags.
  private boolean awareNestedTag = false;

  /**
   * Sets the name of the cookie to resolve.
   */
  public void setCookie(String cookieName) {
    this.cookieName = cookieName;
  }

  private @Nullable String cookieName;

  /**
   * Sets the name of the http header to resolve.
   */
  public void setHeader(String headerName) {
    this.headerName = headerName;
  }

  private @Nullable String headerName;

  /**
   * Sets the name of the http request parameter to resolve.
   * @param parameterName the name of the parameter.
   */
  public void setParameter(String parameterName) {
    this.parameterName = parameterName;
  }

  private @Nullable String parameterName;

  public void setScope(String scope) {
    var s = scope.trim().toLowerCase();
    switch (s) {
      case "page" -> {
        this.scope = PAGE_SCOPE;
        return;
      }
      case "request" -> {
        this.scope = REQUEST_SCOPE;
        return;
      }
      case "session" -> {
        this.scope = SESSION_SCOPE;
        return;
      }
      case "application" -> {
        this.scope = APPLICATION_SCOPE;
        return;
      }
    }
    throw new IllegalArgumentException(format(
      "Invalid scope name: [%s].", scope
    ));
  }

  private @Nullable Integer scope;

  /**
   * Resolves the value of the variable reference in the given {@link PageContext}.
   * The resolution process looks for the value in the following order:
   * - Cookie: If a cookie name is provided, it looks for the cookie value.
   * - Header: If a header name is provided, it looks for the header value.
   * - Request Parameter: If a parameter name is provided, it looks for the parameter value.
   * - Object Property: Finally, if the name and property are provided, it resolves the value on the object scope.
   * @param context the {@link PageContext} in which the variable reference is resolved.
   * @return the resolved value, or {@code null} if the value cannot be resolved.
   */
  public @Nullable Object resolve(PageContext context) {
    var request = context.getHttpRequest();
    if (hasText(cookieName)) {
      for (var cookie : request.getCookies()) {
        if (cookie.getName().equals(cookieName)) {
          return cookie.getValue();
        }
      }
      return null;
    }
    if (hasText(headerName)) {
      return request.getHeader(headerName);
    }
    if (hasText(parameterName)) {
      return request.getParameter(parameterName);
    }
    return resolveValueOnScope(name, property, awareNestedTag, context, scope);
  }

  public void assign(PageContext context, Object value) {
    if (!hasText(property)) {
      context.setAttribute(name, value, scope == null ? PAGE_SCOPE : scope);
      return;
    }
    var bean = resolveValueOnScope(name, null, awareNestedTag, context, scope);
    if (bean == null) throw new IllegalArgumentException(format(
      "Failed to set property [%s] because bean [%s] was null.", property, name
    ));
    setProperties(bean, Map.of(property, value));
  }

  /**
   * A regular expression pattern that defines the valid structure of a property path.
   * The property path can consist of identifiers (e.g., object.property) and array indexes (e.g., object.property[0]).
   */
  private static final String PROPERTY_NODE = "[_a-zA-Z][_a-zA-Z0-9]*(\\[(0|[1-9][0-9]*)])?";
  private static final Pattern PROPERTY_PATH = Pattern.compile(
    "^" + PROPERTY_NODE + "(\\." + PROPERTY_NODE + ")*$"
  );
}
