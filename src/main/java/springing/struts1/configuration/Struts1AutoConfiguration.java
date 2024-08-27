package springing.struts1.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 5)
@Import({MessageResourcesConfiguration.class, WebMvcConfiguration.class, Struts1Configuration.class})
public class Struts1AutoConfiguration {}
