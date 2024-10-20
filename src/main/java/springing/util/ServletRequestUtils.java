package springing.util;

import jakarta.servlet.jsp.PageContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.tags.NestedPathTag;
import javax.servlet.http.HttpServletRequest;

import static springing.util.ObjectUtils.*;

public class ServletRequestUtils {

  public ServletRequestUtils(
    jakarta.servlet.http.HttpServletRequest request,
    ConversionService conversionService
  ) {
    this.request = HttpServletRequest.wrap(request);
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
    if (key == null) {
      return null;
    }
    var m = INDEXED_PROPERTY.matcher(key);
    var isIndexed = m.matches();
    var index = isIndexed ? Integer.valueOf(m.group(2)) : null;
    var name = isIndexed ? m.group(1) : key;
    var model = pageContext.getAttribute(name);
    if (model != null) {
      return getAt(model, index);
    }
    return getAt(RequestContextHolder
      .currentRequestAttributes()
      .getAttribute(key, RequestAttributes.SCOPE_REQUEST), index);
  }

  public static @Nullable Object resolveValueOnScope(
    @Nullable String attributeName,
    @Nullable String relPath,
    boolean awareNestedTag,
    PageContext pageContext
  ) {
    if (attributeName != null) {
      var bean = getAttributeFromScope(pageContext, attributeName);
      if (relPath == null) return bean;

      return retrieveValue(bean, awareNestedTag ? resolveNestedPath(relPath, pageContext) : relPath);
    }
    if (relPath == null) {
      return null;
    }
    var fullPath = resolveNestedPath(relPath, pageContext);
    var pos = fullPath.indexOf(".");
    var attr = pos < 0 ? fullPath : fullPath.substring(0, pos);
    var propertyPath = pos < 0 ? null : relPath.substring(pos + 1);
    var bean = getAttributeFromScope(pageContext, attr);
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




}
