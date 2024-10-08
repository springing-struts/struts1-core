package org.apache.struts.util;

import jakarta.servlet.ServletContext;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ModuleConfigBean;
import org.springframework.lang.Nullable;
import springing.util.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static springing.util.StringUtils.normalizeForwardPath;

/**
 * General purpose utility methods related to module processing.
 */
public class ModuleUtils {

  private ModuleUtils(
    List<ModuleConfigBean> moduleConfigs
  ) {
    for (var moduleConfig : moduleConfigs) {
      this.moduleConfigs.put(moduleConfig.getPrefix(), moduleConfig);
    }
  }

  private final Map<String, ModuleConfigBean> moduleConfigs = new ConcurrentHashMap<>();

  public static ModuleUtils getInstance() {
    if (INSTANCE == null) throw new IllegalArgumentException(
      "ModuleUtils is not initialized yet."
    );
    return INSTANCE;
  }

  private static @Nullable ModuleUtils INSTANCE;

  public static void initialize(List<ModuleConfigBean> moduleConfigs) {
    INSTANCE = new ModuleUtils(moduleConfigs);
  }

  /**
   * Return the current `ModuleConfig` object corresponding to the current
   * request.
   */
  public ModuleConfigBean getModuleConfig(HttpServletRequest request) {
    return getModuleConfigFor(request.getServletPath());
  }

  /**
   * Return the desired `ModuleConfig` object stored in context, if it exists,
   * otherwise return the current `ModuleConfig`.
   */
  public ModuleConfig getModuleConfig(
    String prefix,
    HttpServletRequest request,
    ServletContext context
  ) {
    var config = getModuleConfig(prefix, context);
    return config != null ? config : getModuleConfig(request);
  }

  /**
   * Return the desired ModuleConfig object stored in context, if it exists,
   * null otherwise.
   */
  public @Nullable ModuleConfig getModuleConfig(
    String prefix,
    ServletContext context
  ) {
    return moduleConfigs.get(normalizeForwardPath(prefix));
  }

  /**
   * Get the module name to which the specified request belong.
   */
  public String getModuleName(
    HttpServletRequest request,
    ServletContext context
  ) {
    return getModuleConfig(request).getPrefix().substring(1);
  }

  /**
   * Get the module name to which the specified uri belong.
   */
  public String getModuleName(
    String matchPath,
    ServletContext context
  ) {
    return getModuleConfigFor(matchPath).getPrefix().substring(1);
  }

  private ModuleConfigBean getModuleConfigFor(String requestPath) {
    var path = normalizeForwardPath(requestPath);
    var longestMatch = moduleConfigs.get("/");
    for (var entry : moduleConfigs.entrySet()) {
      var prefix = entry.getKey();
      var config = entry.getValue();
      if (path.equals(prefix)) {
        return config;
      }
      if (path.startsWith(prefix + "/") && prefix.length() > longestMatch.getPrefix().length()) {
        longestMatch = config;
      }
    }
    return longestMatch;
  }

  /**
   * Returns the module config object associated with the current request.
   */
  public static ModuleConfigBean getCurrent() {
    var currentRequest = ServletRequestUtils.getCurrent();
    return getInstance().getModuleConfig(currentRequest);
  }

  public void loadPlugIns(ActionServlet actionServlet) {
    moduleConfigs.values().forEach(it -> it.loadPlugins(actionServlet));
  }
}
