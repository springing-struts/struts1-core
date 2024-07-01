package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

public class MessageResourceConfig {

  public MessageResourceConfig(
    @JacksonXmlProperty(localName = "parameter", isAttribute = true)
    String parameter
  ) {
    this.parameter = parameter;
  }
  private final String parameter;

  @JsonBackReference
  private StrutsConfig config;

  public MessageSource toMessageSource() {
    var messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(parameter);
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
}
