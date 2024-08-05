package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.ActionFormBean;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The "form-beans" element describes the set of form bean descriptors for this
 */
public class FormBeansConfig {

  public FormBeansConfig(
    @JacksonXmlProperty(localName = "type", isAttribute = true)
    @Nullable String type
  ) throws ClassNotFoundException {
    this.type = type;
    formBeanClass = (type == null) ? ActionFormBean.class : (Class<? extends ActionFormBean>) Class.forName(type);
  }

  /**
   * Fully qualified Java class to use when instantiating ActionFormBean
   * objects. If specified, the object must be a subclass of the default class
   * type.
   */
  public @Nullable String getType() {
    return type;
  }
  private final @Nullable String type;

  public Class<? extends ActionFormBean> getFormBeanClass() {
    return formBeanClass;
  }
  private final Class<? extends ActionFormBean> formBeanClass;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "form-bean")
  @JsonManagedReference
  private List<FormBeanConfig> entries = new ArrayList<>();
  public List<FormBeanConfig> getEntries() {
    return entries;
  }
}
