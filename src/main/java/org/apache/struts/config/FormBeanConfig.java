package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.action.DynaActionFormClass;
import org.apache.struts.taglib.html.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static springing.util.ObjectUtils.classFor;
import static springing.util.ObjectUtils.createInstanceOf;

/**
 * A JavaBean representing the configuration information of a `form-bean`
 * element in a Struts configuration file.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class FormBeanConfig {

  @JsonBackReference
  private FormBeansConfig container;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private void setProperties(List<PropertyConfig> properties) {
    for (var property : properties) {
      property.applyTo(this);
    }
  }

  /**
   * The unique identifier of this form bean, which is used to reference this
   * bean in `ActionMapping` instances as well as for the name of the request
   * or session attribute under which the corresponding* form bean instance is
   * created or accessed.
   */
  public String getName() {
    return name;
  }
  @JacksonXmlProperty(localName = "name", isAttribute = true)
  private String name;

  /**
   * The fully qualified Java class name of the implementation class to be
   * used or generated.
   */
  public String getType() {
    return getFormClass().getName();
  }
  public Class<? extends ActionForm> getFormClass() {
    return requireNonNull(
      getInheritedProperty(Class.class, it -> it.type)
    );
  }
  private Class<? extends ActionForm> type;
  @JacksonXmlProperty(localName = "type", isAttribute = true)
  private void setType(String type) {
    this.type = classFor(type);
  }

  /**
   * The name of the FormBeanConfig that this config inherits configuration
   * information from.
   */
  @JacksonXmlProperty(localName = "extends", isAttribute = true)
  private @Nullable String inherit;

  /**
   * The set of FormProperty elements defining dynamic form properties for this
   * form bean, keyed by property name.
   */
  public List<FormPropertyConfig> getFormProperties() {
    var props = new ArrayList<FormPropertyConfig>(formProperties);
    var parent = getParentConfig();
    if (parent != null) {
      props.addAll(parent.getFormProperties());
    }
    return props;
  }
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "form-property")
  private List<FormPropertyConfig> formProperties = new ArrayList<>();

  /**
   * Create and return an `ActionForm` instance appropriate to the information
   * in this `FormBeanConfig`.
   */
  public ActionForm createActionForm(Map<String, Object> props) {
    if (getDynamic()) {
      var dynaBean = getDynaActionFormClass().newInstance();
      props.forEach(dynaBean::set);
      return (ActionForm) dynaBean;
    }
    return createInstanceOf(getFormClass(), props);
  }

  public DynaActionFormClass getDynaActionFormClass() {
    return DynaActionFormClass.createDynaActionFormClass(this);
  }

  public boolean getDynamic() {
    return DynaActionForm.class.isAssignableFrom(getFormClass());
  }

  /**
   * Create and return an `ActionForm` instance appropriate to the information
   * in this `FormBeanConfig`.
   */
  public ActionForm createActionForm() {
    return createActionForm(Map.of());
  }

  private <T> @Nullable T getInheritedProperty(
    Class<T> propType, Function<FormBeanConfig, T> getProp
  ) {
    var selfValue = getProp.apply(this);
    if (selfValue != null) {
      return selfValue;
    }
    var parentConfig = getParentConfig();
    if (parentConfig == null) {
      return null;
    }
    return parentConfig.getInheritedProperty(propType, getProp);
  }

  private @Nullable FormBeanConfig getParentConfig() {
    if (inherit == null) {
      return null;
    }
    for (var mapping : container.getEntries()) {
      if (inherit.equals(mapping.name)) {
        return mapping;
      }
    }
    throw new IllegalArgumentException(format(
      "Unknown parent form bean config name [%s] of the form bean config [%s].",
      inherit, name
    ));
  }
}
