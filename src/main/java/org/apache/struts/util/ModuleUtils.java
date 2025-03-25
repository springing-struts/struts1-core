package org.apache.struts.util;

import static org.apache.struts.config.ModuleConfigBean.normalizeModulePrefix;
import static springing.util.StringUtils.normalizeForwardPath;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ModuleConfigBean;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * General purpose utility methods related to module processing.
 */
public class ModuleUtils {

  private ModuleUtils(
    List<ModuleConfigBean> moduleConfigs,
    HttpServletRequest request
  ) {
    this.request = request;
    for (var moduleConfig : moduleConfigs) {
      this.moduleConfigs.put(moduleConfig.getPrefix(), moduleConfig);
    }
  }

  private final Map<String, ModuleConfigBean> moduleConfigs =
    new ConcurrentHashMap<>();
  private final HttpServletRequest request;

  public static ModuleUtils getInstance() {
    if (INSTANCE == null) throw new IllegalArgumentException(
      "ModuleUtils is not initialized yet."
    );
    return INSTANCE;
  }

  private static @Nullable ModuleUtils INSTANCE;

  public static void initialize(
    List<ModuleConfigBean> moduleConfigs,
    HttpServletRequest request
  ) {
    INSTANCE = new ModuleUtils(moduleConfigs, request);
  }

  /**
   * Return the current `ModuleConfig` object corresponding to the give request.
   */
  public ModuleConfigBean getModuleConfig(HttpServletRequest request) {
    var isOnRequestThread = RequestContextHolder.getRequestAttributes() != null;
    var modulePath = isOnRequestThread ? request.getServletPath() : "";
    return getModuleConfigFor(modulePath);
  }

  public ModuleConfigBean getModuleConfig() {
    return getModuleConfig(request);
  }

  /**
   * Return the ModuleConfig object is it exists, null otherwise.
   */
  public @Nullable ModuleConfig getModuleConfig(
    HttpServletRequest request,
    ServletContext context
  ) {
    return getModuleConfig(request);
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
    return moduleConfigs.get(normalizeModulePrefix(prefix));
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
  public String getModuleName(String matchPath, ServletContext context) {
    return getModuleConfigFor(matchPath).getPrefix().substring(1);
  }

  /**
   * Selects the module to which the specified request belongs and adds
   * the corresponding request attributes to this request.
   * **NOTE**
   *   This method does nothing; the module-config for the current request
   *   is managed as a request-scoped, Spring-managed bean.
   */
  public void selectModule(HttpServletRequest request, ServletContext context) {
    // NOP
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
      if (
        path.startsWith(prefix + "/") &&
        (longestMatch == null ||
          prefix.length() > longestMatch.getPrefix().length())
      ) {
        longestMatch = config;
      }
    }
    return longestMatch;
  }

  /**
   * Returns the module config object associated with the current request.
   */
  public static ModuleConfigBean getCurrent() {
    return getInstance().getModuleConfig();
  }

  public void loadPlugIns(ActionServlet actionServlet) {
    moduleConfigs.values().forEach(it -> it.loadPlugins(actionServlet));
  }
}
