package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.ServletException;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;

import java.util.HashMap;
import java.util.List;

import static springing.util.ObjectUtils.createInstanceOf;

public class PlugInConfig {
  public PlugInConfig(
    @JacksonXmlProperty(localName = "className", isAttribute = true) String className
  ) {
    this.className = className;
  }
  public String getClassName() {
    return className;
  }
  private final String className;

  public <T extends PlugIn> T loadPlugIn() {
    var props = new HashMap<String, String>();
    for (var p : properties) {
      props.put(p.property, p.value);
    }
    var plugIn = (T) createInstanceOf(className, props);
    try {
      plugIn.init(new ActionServlet(), config);
    } catch (ServletException e) {
      throw new RuntimeException(
        "Failed to initialize plugin: " + className, e
      );
    }
    return plugIn;
  }

  @JsonBackReference
  private ModuleConfig config;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "set-property")
  private List<PlugInProperty> properties;

  public static class PlugInProperty {
    public PlugInProperty(
      @JacksonXmlProperty(localName = "property", isAttribute = true) String property,
      @JacksonXmlProperty(localName = "value", isAttribute = true) String value
    ) {
      this.property = property;
      this.value = value;
    }
    private final String property;
    private final String value;
  }
}
