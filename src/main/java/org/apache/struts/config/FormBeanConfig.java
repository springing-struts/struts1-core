package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.validator.DynaValidatorForm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static springing.util.ObjectUtils.classFor;
import static springing.util.ObjectUtils.createInstanceOf;

/**
 * A JavaBean representing the configuration information of a `<form-bean>`
 * element in a Struts configuration file.
 */
public class FormBeanConfig {
  @JsonBackReference
  private FormBeansConfig container;

  public FormBeanConfig createFormBeanConfig() {
    try {
      FormBeanConfig config = container.getFormBeanConfigClass()
          .getDeclaredConstructor()
          .newInstance();
      Map<String, Object> props = new HashMap<>();
      props.put("name", name);
      props.put("type", type.getName());
      props.put("formProperties", formProperties);
      for (var prop : properties) {
        props.put(prop.getProperty(), prop.getValue());
      }
      BeanUtils.copyProperties(props, config);
      return config;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException(String.format(
        "An error occurred while creating action form bean instance. name: [%s], type: [%s].",
        name, type.getSimpleName()
      ), e);
    }
  }
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private List<PropertyConfig> properties = new ArrayList<>();

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
    return type.getName();
  }
  public Class<? extends ActionForm> getFormClass() {
    return type;
  }
  private Class<? extends ActionForm> type;
  @JacksonXmlProperty(localName = "type", isAttribute = true)
  private void setType(String type) {
    this.type = classFor(type);
  }

  /**
   * The set of FormProperty elements defining dynamic form properties for this
   * form bean, keyed by property name.
   */
  public List<FormPropertyConfig> getFormProperties() {
    return formProperties;
  }
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "form-property")
  private List<FormPropertyConfig> formProperties = new ArrayList<>();

  /**
   * Create and return an `ActionForm` instance appropriate to the information
   * in this `FormBeanConfig`.
   */
  public ActionForm createActionForm(Map<String, Object> props) {
    if (type.equals(DynaActionForm.class)) {
      return new DynaActionForm(this);
    }
    if (type.equals(DynaValidatorForm.class)) {
      return new DynaValidatorForm(this);
    }
    return createInstanceOf(type, props);
  }

  /**
   * Create and return an `ActionForm` instance appropriate to the information
   * in this `FormBeanConfig`.
   */
  public ActionForm createActionForm() {
    return createActionForm(Map.of());
  }

  public ActionForm prepare(HttpServletRequest request) {
    var exitingForm = request.getAttribute(Constants.BEAN_KEY);
    if (exitingForm instanceof ActionForm exitingActionForm) {
      return exitingActionForm;
    }
    /*
    var props = new HashMap<String, Object>();
    request.getParameterMap().forEach((name, values) -> {
      var value = values.length == 0 ? null : values.length == 1 ? values[0] : List.of(values);
      props.put(name, value);
    });
    var form = (ActionForm) createInstanceOf(getType(), props);
    */
    var form = createActionForm();
    DataBinder binder = new DataBinder(form);
    binder.bind(new MutablePropertyValues(request.getParameterMap()));
    var errors = binder.getBindingResult();
    request.setAttribute(Constants.BEAN_KEY, getName());
    request.setAttribute(getName(), form);
    return form;
  }
}
