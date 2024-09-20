package springing.struts1.taglib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.springframework.lang.Nullable;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

public abstract class SetTagBase<T> extends SetSupport {
  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    multiple = false;
    name = null;
    defaultValue = null;
  }

  private boolean multiple = false;

  private @Nullable String name;

  private @Nullable String defaultValue;

  @Override
  public int doEndTag() throws JspException {
    if (!hasText(name)) throw new IllegalStateException(String.format(
      "The name property of this tag [%s] is required.", this.getClass().getName()
    ));
    var request = (HttpServletRequest) pageContext.getRequest();
    var values = retrieveValues(request, name);
    if (values.isEmpty()) {
      if (defaultValue == null) throw new IllegalStateException(String.format(
          "Failed to retrieve values assigned the name [%s] and default value was not set.",
          name
      ));
      this.value = getDefaultValue(request, name, defaultValue);
    }
    else {
      this.value = multiple ? values.toArray() : values.getFirst();
    }
    return super.doEndTag();
  }

  protected abstract List<T> retrieveValues(HttpServletRequest request, String name);

  protected abstract T getDefaultValue(HttpServletRequest request, String name, String defaultValueString);

  /**
   * (Required) Specifies the name of the scripting variable (and associated
   * page scope attribute) that will be made available with the value of the
   * specified request header.
   */
  public void setId(String id) {
    setVar(id);
  }

  /**
   * If any arbitrary value for this attribute is specified, The list of the values
   * will be set to the variable instead of single value.
   */
  public void setMultiple(String multiple) {
    this.multiple = hasText(multiple);
  }

  /**
   * (Required) Specifies the name of value, or values, is to be retrieved.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * The string expression of the default value to return if no value with the
   * specified name was included in this request.
   */
  public void setValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
