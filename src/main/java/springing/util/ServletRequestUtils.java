package springing.util;

import jakarta.servlet.jsp.PageContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.NestedPathTag;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static springing.util.ObjectUtils.*;

public class ServletRequestUtils {

  public ServletRequestUtils(
    jakarta.servlet.http.HttpServletRequest request,
    ConversionService conversionService
  ) {
    this.request = HttpServletRequest.toJavaxNamespace(request);
    this.conversionService = conversionService;
    INSTANCE = this;
  }

  private final HttpServletRequest request;
  private final ConversionService conversionService;

  private static @Nullable ServletRequestUtils INSTANCE;

  public static ServletRequestUtils getInstance() {
    if (INSTANCE == null) throw new IllegalStateException(
        "The ServletRequestUtils should be initialized before being used."
    );
    return INSTANCE;
  }

  public static HttpServletRequest getCurrent() {
    return getInstance().request;
  }

  public static <T> @Nullable T convertValue(@Nullable Object value, Class<T> toType) {
    return getInstance().conversionService.convert(value, toType);
  }


  public static @Nullable Object getAttributeFromScope(
    PageContext pageContext,
    @Nullable String key
  ) {
    return getAttributeFromScope(pageContext, key, null);
  }

  public static @Nullable Object getAttributeFromScope(
      PageContext pageContext,
      @Nullable String key,
      @Nullable Integer scope
  ) {
    if (key == null) {
      return null;
    }
    var m = INDEXED_PROPERTY.matcher(key);
    var isIndexed = m.matches();
    var index = isIndexed ? Integer.valueOf(m.group(2)) : null;
    var name = isIndexed ? m.group(1) : key;
    var model = (scope == null)
      ? pageContext.findAttribute(name)
      : pageContext.getAttribute(name, scope);
    if (model == null) {
      return null;
    }
    return isIndexed ? getAt(model, index) : model;
  }

  public static @Nullable Object resolveValueOnScope(
    @Nullable String attributeName,
    @Nullable String relPath,
    boolean awareNestedTag,
    PageContext pageContext
  ) {
    return resolveValueOnScope(
      attributeName, relPath, awareNestedTag, pageContext, null
    );
  }

  public static @Nullable Object resolveValueOnScope(
    @Nullable String attributeName,
    @Nullable String relPath,
    boolean awareNestedTag,
    PageContext pageContext,
    @Nullable Integer scope
  ) {
    if (attributeName != null) {
      var bean = getAttributeFromScope(pageContext, attributeName, scope);
      if (relPath == null) {
        return bean;
      }
      return retrieveValue(bean, awareNestedTag ? resolveNestedPath(relPath, pageContext) : relPath);
    }
    if (relPath == null) {
      return null;
    }
    var fullPath = resolveNestedPath(relPath, pageContext);
    var pos = fullPath.indexOf(".");
    var attr = pos < 0 ? fullPath : fullPath.substring(0, pos);
    var propertyPath = pos < 0 ? null : relPath.substring(pos + 1);
    var bean = getAttributeFromScope(pageContext, attr, scope);
    return retrieveValue(bean, propertyPath);
  }

  public static String resolveNestedPath(
    String relPath,
    PageContext pageContext
  ) {
    var nestedPath = (String) pageContext.getAttribute(
      NestedPathTag.NESTED_PATH_VARIABLE_NAME, PageContext.REQUEST_SCOPE
    );
    var path = (nestedPath == null ? "" : (nestedPath + ".")) + relPath;
    return path
        .replaceAll("(\\.{2,})", ".")
        .replaceAll("\\.$", "");
  }

  /**
   * Forwards or includes the request to the specified URI.
   * This method dispatches the current HTTP request to the given URI either by forwarding
   * the request (i.e., the current response will be replaced with the response from the
   * target resource) or by including the content of the target resource within the current
   * response. The method determines whether to forward or include based on the value of the
   * `includes` parameter.
   */
  public static void forwardRequest(
    String uri,
    HttpServletRequest request,
    HttpServletResponse response,
    boolean includes
  ) throws ServletException, IOException {
    var dispatcher = request.getRequestDispatcher(uri);
    if (dispatcher == null) throw new ServletException(String.format(
      "Failed to retrieve a RequestDispatcher for url [%s].", uri
    ));
    try {
      if (includes) {
        dispatcher.include(request.unwrap(), response.unwrap());
      }
      else {
        dispatcher.forward(request.unwrap(), response.unwrap());
      }
    } catch (jakarta.servlet.ServletException e) {
      throw new ServletException(e);
    }
  }

  public static void forwardRequest(
    String uri,
    PageContext pageContext
  ) throws ServletException, IOException {
    forwardRequest(
      uri,
      (HttpServletRequest) pageContext.getRequest(),
      (HttpServletResponse) pageContext.getResponse(),
      false
    );
  }
}
