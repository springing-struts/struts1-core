package org.apache.struts.action;

import jakarta.servlet.http.HttpServlet;
import org.apache.struts.Globals;
import org.apache.struts.config.ModuleConfigBean;
import org.springframework.lang.Nullable;
import springing.struts1.configuration.ServletConfigBean;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

/**
 * Dummy implementation of a Struts Action Servlet for Action classes which
 * directly access the instance.
 */
public class ActionServlet extends HttpServlet {

  public ActionServlet(ServletConfigBean servletConfig) {
    this.servletConfig = servletConfig;
  }

  private final ServletConfigBean servletConfig;

  @Override
  public ServletContext getServletContext() {
    return servletConfig.getServletContext();
  }

  public void setServletContext(jakarta.servlet.ServletContext servletContext) {
    servletContext.setAttribute(Globals.SERVLET_KEY, servletConfig.getUrlPattern());
    servletConfig.setServletContext(
      ServletContext.toJavaxNamespace(servletContext)
    );
  }

  public ServletConfig getServletConfig() {
    return servletConfig;
  }

  public int getLoadOnStartup() {
    return requireNonNullElse(
      servletConfig.getLoadOnStartup(),1
    );
  }

  public List<ModuleConfigBean> getStrutsModules() {
    if (strutsModules == null) {
      strutsModules = loadStrutsModules();
    }
    return strutsModules;
  }

  private @Nullable List<ModuleConfigBean> strutsModules;

  private List<ModuleConfigBean> loadStrutsModules() {
    var results = new HashMap<String, ModuleConfigBean>();
    var params = servletConfig.getInitParams();
    params.forEach((name, value) -> {
      var moduleName = "config".equals(name) ? ""
        : name.startsWith("config/") ? name.substring("config/".length())
        : null;
      if (moduleName == null) {
        return;
      }
      var paths = value.split(",");
      for (var path : paths) {
        var config = ModuleConfigBean.loadFrom(path.trim(), moduleName);
        var existingConfig = results.get(config.getPrefix());
        if (existingConfig != null) {
          existingConfig.merge(config);
        }
        results.put(config.getPrefix(), existingConfig != null ? existingConfig : config);
      }
    });
    return results.values().stream().toList();
  }
}
