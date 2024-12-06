package springing.struts1.taglib;

import jakarta.servlet.jsp.PageContext;
import org.springframework.lang.Nullable;
import springing.util.ServletRequestUtils;

import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasText;
import static springing.util.ServletRequestUtils.resolveValueOnScope;

public class JspVariableReference {
  public static JspVariableReference create() {
    return new JspVariableReference();
  }

  private JspVariableReference() {
  }

  public @Nullable String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    if (name != null && !PROPERTY_PATH.asMatchPredicate().test(name)) throw new IllegalArgumentException(
      "Invalid object reference name: " + name
    );
    this.name = name;
  }
  private @Nullable String name;

  public @Nullable String getProperty() {
    return property;
  }

  public void setProperty(@Nullable String property) {
    if (property != null && !PROPERTY_PATH.asMatchPredicate().test(property)) throw new IllegalArgumentException(
      "Invalid object property path: " + property
    );
    this.property = property;
  }
  private @Nullable String property;

  public void setAwareNestedTag(boolean awareNestedTag) {
    this.awareNestedTag = awareNestedTag;
  }
  private boolean awareNestedTag = false;

  public void setCookie(String cookieName) {
    this.cookieName = cookieName;
  }
  private @Nullable String cookieName;

  public void setHeader(String headerName) {
    this.headerName = headerName;
  }
  private @Nullable String headerName;

  public void setParameter(String parameterName) {
    this.parameterName = parameterName;
  }
  private @Nullable String parameterName;

  public @Nullable Object resolve(PageContext context) {
    var request = ServletRequestUtils.getCurrent();
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
    return resolveValueOnScope(
      name,
      property,
      awareNestedTag,
      context
    );
  }
  private static final String PROPERTY_NODE = "[_a-zA-Z][_a-zA-Z0-9]*(\\[(0|[1-9][0-9]*)])?";
  private static final Pattern PROPERTY_PATH = Pattern.compile(
    "^" + PROPERTY_NODE + "(\\." + PROPERTY_NODE + ")*$"
  );

}
