package springing.struts1.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({WebMvcConfiguration.class, Struts1Configuration.class, MessageResourcesConfiguration.class})
public class Struts1AutoConfiguration {
}
