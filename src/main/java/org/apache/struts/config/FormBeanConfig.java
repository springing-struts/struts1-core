package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.taglib.html.Constants;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static springing.util.ObjectUtils.createInstanceOf;

/**
 * A JavaBean representing the configuration information of a `<form-bean>`
 * element in a Struts configuration file.
 */
public class FormBeanConfig {
  public FormBeanConfig(
      @JacksonXmlProperty(localName = "name", isAttribute = true) String name,
      @JacksonXmlProperty(localName = "type", isAttribute = true) String type
  ) {
    this.name = name;
    this.type = type;
  }

  @JsonBackReference
  private FormBeansConfig container;

  public FormBeanConfig createActionFormBean() {
    var config = MAPPER.convertValue(this, Map.class);
    for (var prop : properties) {
      config.put(prop.getProperty(), prop.getValue());
    }
    return new ObjectMapper().convertValue(config, container.getFormBeanClass());
  }
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private List<PropertyConfig> properties = new ArrayList<>();

  protected FormBeanConfig() {
    this("", "");
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
  private final String name;

  /**
   * The fully qualified Java class name of the implementation class to be
   * used or generated.
   */
  public String getType() {
    return type;
  }
  private final String type;


  /**
   * The set of FormProperty elements defining dynamic form properties for this
   * form bean, keyed by property name.
   */
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "form-property")
  private List<FormPropertyConfig> formProperties;

  /**
   * Create and return an `ActionForm` instance appropriate to the information
   * in this `FormBeanConfig`.
   */
  public ActionForm createActionForm(Map<String, Object> props) {
    return createInstanceOf(getType(), props);
  }

  /**
   * Create and return an `ActionForm` instance appropriate to the information
   * in this `FormBeanConfig`.
   */
  public ActionForm createActionForm() {
    return createInstanceOf(getType());
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
