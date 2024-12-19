package org.apache.struts.webapp.examples;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import springing.struts1.configuration.StrutsConfiguration;
import springing.struts1.configuration.WebMvcConfiguration;

@SpringBootApplication
@Import({StrutsConfiguration.class, WebMvcConfiguration.class})
public class ExampleApp {
}
