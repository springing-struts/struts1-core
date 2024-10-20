package org.apache.struts.config;

import org.springframework.lang.Nullable;

/**
 * The "struts-config" element is the root of the configuration file hierarchy,
 * and contains nested elements for all of them other configuration settings.
 */
public interface ModuleConfig {
  /**
   * The prefix of the context-relative portion of the request URI, used to
   * select this configuration versus others supported by the controller
   * servlet.  A configuration with a prefix of a zero-length String is the
   * default configuration for this web module.
   */
  String getPrefix();

  /**
   * The controller configuration object for this module.
   */
  @Nullable ControllerConfig getControllerConfig();

  /**
   * The default class name to be used when creating action form bean
   * instances.
   */
  String getActionFormBeanClass();

  /**
   * Return the form bean configuration for the specified key, if any;
   * otherwise return `null`.
   */
  @Nullable FormBeanConfig findFormBeanConfig(String name);

  /**
   * Return the action configuration for the specified path, if any; otherwise
   * return `null`.
   */
  @Nullable ActionConfig findActionConfig(String path);

  /**
   * Return the action configurations for this module. If there are none,
   * a zero-length array is returned.
   */
  ActionConfig[] findActionConfigs();

 /**
   * Return the forward configuration for the specified key, if any; otherwise
   * return `null`.
   */
  @Nullable ForwardConfig findForwardConfig(String name);

  /**
   * Return the form bean configurations for this module. If there are none,
   * a zero-length array is returned.
   */
  ForwardConfig[] findForwards();
}
