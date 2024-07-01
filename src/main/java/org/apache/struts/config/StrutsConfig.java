package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import javax.xml.stream.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.stream.XMLStreamConstants.*;

@JacksonXmlRootElement(localName = "struts-config")
public class StrutsConfig {
  private static final XmlMapper xmlMapper = new XmlMapper();

  @JacksonXmlProperty(localName = "action-mappings")
  @JsonManagedReference
  private ActionMappingsConfig actionMappings;
  public List<ActionConfig> getActionConfigs() {
    return actionMappings.getEntries();
  }

  @JacksonXmlElementWrapper(localName = "form-beans")
  private FormBeansConfig formBeans;
  public List<FormBeanConfig> getFormBeanConfigs() {
    return formBeans.getEntries();
  }

  @JacksonXmlElementWrapper(localName = "global-exceptions")
  @JacksonXmlProperty(localName = "exception")
  @JsonManagedReference
  private List<ExceptionConfig> globalExceptions;

  @JacksonXmlElementWrapper(localName = "global-forwards")
  @JacksonXmlProperty(localName = "forward")
  //@JsonManagedReference
  private @Nullable List<ForwardConfig> globalForwards;

  public List<ForwardConfig> getGlobalForwards() {
    if (globalForwards == null) {
      return new ArrayList<>();
    }
    return globalForwards;
  }

  @JacksonXmlProperty(localName = "message-resources")
  @JsonManagedReference
  private @Nullable MessageResourceConfig messageResourceConfig;
  public @Nullable MessageResourceConfig getMessageResourceConfig() {
    return messageResourceConfig;
  }

  @JacksonXmlProperty(localName = "plug-in")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private List<PluginConfig> plugins;



  public static StrutsConfig parse(String xml) {
    try {
      return xmlMapper.readValue(xml, StrutsConfig.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static StrutsConfig parseConfigFileAt(String resourceClassPath) {
    var resource = new ClassPathResource(resourceClassPath);
    try (var in = resource.getInputStream()) {
      return xmlMapper.readValue(rewriteDuplicatedPropertyName(in), StrutsConfig.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static InputStream rewriteDuplicatedPropertyName(InputStream in) {
    XMLStreamReader reader = null;
    XMLStreamWriter writer = null;
    var out = new StringWriter();
    try {
      reader = XMLInputFactory.newFactory().createXMLStreamReader(in);
      writer = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
      while (reader.hasNext()) {
        var event = reader.next();
        switch (event) {
          case START_ELEMENT:
            var elementName = reader.getLocalName();
            writer.writeStartElement(elementName);
            for (var i = 0; i < reader.getAttributeCount(); i++) {
              var key = reader.getAttributeLocalName(i);
              var value = reader.getAttributeValue(i);
              writer.writeAttribute(
                "forward".equals(key) ? "forward-path" : key,
                value
              );
            }
            break;
          case CHARACTERS:
            writer.writeCharacters(reader.getText());
            break;
          case END_ELEMENT:
            writer.writeEndElement();
            break;
          default:
            break;
        }
      }

    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (reader != null) reader.close();
        if (writer != null) writer.close();
      } catch (XMLStreamException e) {
        // NOP
      }
    }
    var xml = out.toString();
    return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
  }
}
