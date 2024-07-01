package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionFormBean;
import org.apache.struts.action.ActionMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  public ActionFormBean createActionFormBean() {
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
}
