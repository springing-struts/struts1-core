package org.apache.struts.config;

import static springing.util.ObjectUtils.classFor;
import static springing.util.ObjectUtils.createInstanceOf;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.ServletException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import springing.util.ObjectUtils;

public class PlugInConfig {

  public PlugInConfig(
    @JacksonXmlProperty(
      localName = "className",
      isAttribute = true
    ) String className
  ) {
    this.className = className;
  }

  public String getClassName() {
    return className;
  }

  private final String className;
  private static final String CONFIG_BACKREFERENCE_PROPERTY_NAME =
    "currentPlugInConfigObject";

  public <T extends PlugIn> T loadPlugIn(ActionServlet actionServlet) {
    var plugInClass = classFor(className);
    var plugIn = (T) createInstanceOf(plugInClass, getProperties());
    var bean = new BeanWrapperImpl(plugIn);
    if (bean.isWritableProperty(CONFIG_BACKREFERENCE_PROPERTY_NAME)) {
      bean.setPropertyValue(CONFIG_BACKREFERENCE_PROPERTY_NAME, this);
    }
    try {
      plugIn.init(actionServlet, config);
    } catch (ServletException e) {
      throw new RuntimeException(
        "Failed to initialize plugin: " + className,
        e
      );
    }
    return plugIn;
  }

  @JsonBackReference
  private ModuleConfig config;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private void setProperties(List<PlugInProperty> properties) {
    for (var property : properties) {
      this.properties.put(property.property, property.value);
    }
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  private final Map<String, String> properties = new HashMap<>();

  public static class PlugInProperty {

    public PlugInProperty(
      @JacksonXmlProperty(
        localName = "property",
        isAttribute = true
      ) String property,
      @JacksonXmlProperty(localName = "value", isAttribute = true) String value
    ) {
      this.property = property;
      this.value = value;
    }

    private final String property;
    private final String value;
  }
}
