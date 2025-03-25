package springing.struts1.taglib;

import static java.util.Objects.requireNonNullElse;
import static org.apache.struts.chain.contexts.ServletActionContext.resolveNestedPath;
import static org.apache.struts.chain.contexts.ServletActionContext.resolveValueOnScope;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import org.apache.struts.taglib.html.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag;

/**
 * Another implementation of BindStatus that allows binding form fields to
 * 'type-less' objects, such as Map or Struts' DynaBean.
 */
public class StrutsDataBinding extends BindStatus {

  /**
   * Creates a binding to a property of the bean with the given name in the
   * scope.
   * @param beanName The name of the bean.
   * @param propertyPath The path of the property that this binding targets.
   */
  public static StrutsDataBinding onScope(
    PageContext pageContext,
    String beanName,
    @Nullable String propertyPath,
    boolean awareNestedTag
  ) {
    return onScope(pageContext, beanName, propertyPath, awareNestedTag, null);
  }

  /**
   * Creates a binding to a property of the bean with the given name in the
   * scope.
   * @param beanName The name of the bean.
   * @param propertyPath The path of the property that this binding targets.
   * @param valueProcessor A function that converts the property value to
   *                       the bound value.
   */
  public static StrutsDataBinding onScope(
    PageContext pageContext,
    String beanName,
    @Nullable String propertyPath,
    boolean awareNestedTag,
    @Nullable Function<Object, Object> valueProcessor
  ) {
    var property = propertyPath == null ? "" : propertyPath;
    var path =
      beanName +
      (beanName.isBlank() || property.isBlank() ? "" : ".") +
      property;
    return new StrutsDataBinding(
      pageContext,
      null,
      path,
      awareNestedTag,
      valueProcessor
    );
  }

  /**
   * Creates a binding to a property of a form bean for the page.
   *
   * @param property The path of the property that this binding targets.
   */
  public static StrutsDataBinding onForm(
    PageContext pageContext,
    @Nullable String name,
    boolean awareNestedTag,
    String property
  ) {
    return onForm(pageContext, name, property, awareNestedTag, null);
  }

  /**
   * Creates a binding to a property of a form bean with the given name.
   * If `formName` is null, the default form bean for the page is used.
   *
   * @param formName The name of the form bean.
   * @param property The path of the property that this binding targets.
   * @param valueProcessor A function that converts the property value to
   *                       the bound value.
   */
  public static StrutsDataBinding onForm(
    PageContext pageContext,
    @Nullable String formName,
    String property,
    boolean awareNestedTag,
    @Nullable Function<Object, Object> valueProcessor
  ) {
    if (formName == null) {
      var attr = getFormBeanAttributeKey(pageContext);
      return new StrutsDataBinding(
        pageContext,
        attr,
        property,
        awareNestedTag,
        valueProcessor
      );
    }
    var path = formName + "." + property;
    return new StrutsDataBinding(
      pageContext,
      null,
      path,
      awareNestedTag,
      valueProcessor
    );
  }

  public static @Nullable String getFormBeanAttributeKey(
    PageContext pageContext
  ) {
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
      BIND_STATUS_FIELD =
        AbstractDataBoundFormElementTag.class.getDeclaredField("bindStatus");
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
      new RequestContextWrapper(pageContext.getHttpRequest()),
      requireNonNullElse(relPath, ""),
      false
    );
    this.pageContext = pageContext;
    this.relPath = relPath;
    this.attributeName = attributeName;
    this.awareNestedTag = awareNestedTag;
    this.valueProcessor = valueProcessor;
  }

  private final PageContext pageContext;
  private final @Nullable String relPath;
  private final @Nullable String attributeName;
  private final boolean awareNestedTag;
  private final @Nullable Function<Object, Object> valueProcessor;

  @Override
  public @Nullable String getExpression() {
    return relPath;
  }

  @Override
  public String getPath() {
    return relPath;
  }

  @Override
  public @Nullable Object getValue() {
    var value = resolveValueOnScope(
      attributeName,
      relPath,
      awareNestedTag,
      pageContext
    );
    return (valueProcessor == null) ? value : valueProcessor.apply(value);
  }

  public Map<String, Object> getValueAsMap() {
    var value = getValue();
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
        result.put(prop.getName(), prop.getReadMethod().invoke(value));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
    return result;
  }

  @Override
  public @Nullable Class<?> getValueType() {
    var value = getValue();
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

  private static class RequestContextWrapper extends RequestContext {

    public RequestContextWrapper(HttpServletRequest request) {
      super(request);
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
}
