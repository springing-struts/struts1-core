package springing.struts1.taglib;

import jakarta.servlet.jsp.PageContext;
import org.apache.struts.taglib.html.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag;
import springing.util.ServletRequestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static springing.util.ServletRequestUtils.*;

/**
 * Another implementation of BindStatus that allows binding form fields to
 * 'type-less' objects, such as Map or Struts' DynaBean.
 */
public class StrutsDataBinding extends BindStatus {

  public static StrutsDataBinding onScope(
    PageContext pageContext,
    String name,
    @Nullable String propertyPath,
    boolean awareNestedTag
  ) {
    return onScope(pageContext, name, propertyPath, awareNestedTag, null);
  }

  public static StrutsDataBinding onScope(
    PageContext pageContext,
    String name,
    @Nullable String propertyPath,
    boolean awareNestedTag,
    @Nullable Function<Object, Object> valueProcessor
  ) {
    var property = propertyPath == null ? "" : propertyPath;
    var path = name + (name.isBlank() || property.isBlank() ? "" : ".") + property;
    return new StrutsDataBinding(
      pageContext,
      null,
      path,
      awareNestedTag,
      valueProcessor
    );
  }

  public static StrutsDataBinding onForm(
      PageContext pageContext,
      @Nullable String name,
      boolean awareNestedTag,
      String property
  ) {
    return onForm(pageContext, name, property, awareNestedTag, null);
  }

  public static StrutsDataBinding onForm(
    PageContext pageContext,
    @Nullable String name,
    String property,
    boolean awareNestedTag,
    @Nullable Function<Object, Object> valueProcessor
  ) {
    if (name == null) {
      var attr = getFormBeanAttributeKey(pageContext);
      return new StrutsDataBinding(pageContext, attr, property, awareNestedTag, valueProcessor);
    }
    var path = name + "." + property;
    return new StrutsDataBinding(pageContext, null, path, awareNestedTag, valueProcessor);
  }

  private static @Nullable String getFormBeanAttributeKey(PageContext pageContext) {
    return (String) pageContext.getRequest().getAttribute(Constants.BEAN_KEY);
  }

  public void setTag(AbstractDataBoundFormElementTag tag) {
    try {
      BIND_STATUS_FIELD.set(tag, this);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static final Field BIND_STATUS_FIELD;
  static {
    try {
      BIND_STATUS_FIELD = AbstractDataBoundFormElementTag.class.getDeclaredField("bindStatus");
      BIND_STATUS_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
  }

  private StrutsDataBinding(
    PageContext pageContext,
    @Nullable String attributeName,
    @Nullable String relPath,
    boolean awareNestedTag,
    @Nullable Function<Object, Object> valueProcessor
  ) throws IllegalStateException {
    super(
      new RequestContextWrapper(),
      relPath == null ? "" : relPath,
      false
    );
    this.relPath = relPath;
    var value = resolveValueOnScope(
      attributeName, relPath, awareNestedTag, pageContext
    );
    this.value = valueProcessor == null
        ? value
        : valueProcessor.apply(value);
  }
  private final @Nullable String relPath;
  private final @Nullable Object value;

  @Override
  public @Nullable String getExpression() {
    return relPath;
  }

  private static class RequestContextWrapper extends RequestContext{
    public RequestContextWrapper() {
      //super(((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest());
      super(ServletRequestUtils.getCurrent());
    }

    @Nullable
    @Override
    protected Object getModelObject(String modelName) {
      return "";
    }

    @Override
    public Errors getErrors(String name) {
      return getErrors(name, false);
    }

    @Override
    public Errors getErrors(String name, boolean htmlEscape) {
      return new MapBindingResult(Map.of(), "empty");
    }
  }

  @Override
  public String getPath() {
    return relPath;
  }

  @Override
  public @Nullable Object getValue() {
    return value;
  }

  public Map<String, Object> getValueAsMap() {
    if (value instanceof Map) {
      return (Map<String, Object>) value;
    }
    var result = new HashMap<String, Object>();
    if (value == null) {
      return result;
    }
    var props = BeanUtils.getPropertyDescriptors(value.getClass());
    for (var prop : props) {
      try {
        result.put(
          prop.getName(),
          prop.getReadMethod().invoke(value)
        );
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
    return result;
  }

  @Override
  public @Nullable Class<?> getValueType() {
    return value == null ? null : value.getClass();
 }

  @Nullable
  @Override
  public Object getActualValue() {
    return super.getActualValue();
  }

  @Override
  public String getDisplayValue() {
    return super.getDisplayValue();
  }

  @Override
  public boolean isError() {
    return super.isError();
  }

  @Override
  public String[] getErrorCodes() {
    return super.getErrorCodes();
  }

  @Override
  public String getErrorCode() {
    return super.getErrorCode();
  }
}
