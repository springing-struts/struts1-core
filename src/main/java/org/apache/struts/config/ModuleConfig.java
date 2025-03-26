package org.apache.struts.config;

import static java.util.Objects.requireNonNullElse;
import static springing.util.StringUtils.normalizeForwardPath;

import org.apache.struts.util.MessageResources;
import org.springframework.lang.Nullable;
import springing.struts1.message.ResourceBundleMessageResources;

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
  @Nullable
  ControllerConfig getControllerConfig();

  /**
   * The default class name to be used when creating action form bean
   * instances.
   */
  String getActionFormBeanClass();

  /**
   * Return the form bean configuration for the specified key, if any;
   * otherwise return `null`.
   */
  @Nullable
  FormBeanConfig findFormBeanConfig(String name);

  /**
   * Return the action configuration for the specified path, if any; otherwise
   * return `null`.
   */
  default @Nullable ActionConfig findActionConfig(String actionPath) {
    var path = getActionUrl(actionPath);
    for (var actionConfig : findActionConfigs()) {
      if (actionConfig.match(path)) {
        return actionConfig;
      }
    }
    return null;
  }

  default String getActionUrl(String actionPath) {
    return normalizeForwardPath(getPrefix() + "/" + actionPath);
  }

  /**
   * Return the action configurations for this module. If there are none,
   * a zero-length array is returned.
   */
  ActionConfig[] findActionConfigs();

  /**
   * Return the forward configuration for the specified key, if any; otherwise
   * return `null`.
   */
  default @Nullable ForwardConfig findForwardConfig(String name) {
    for (var forwardConfig : findForwards()) {
      if (name.equals(forwardConfig.getName())) {
        return forwardConfig;
      }
    }
    return null;
  }

  /**
   * Return the form bean configurations for this module. If there are none,
   * a zero-length array is returned.
   */
  ForwardConfig[] findForwards();

  /**
   * Return the message resources configuration for the specified key, if any;
   * otherwise return `null`.
   */
  default @Nullable MessageResourcesConfig findMessageResourceConfig(
    @Nullable String key
  ) {
    var normalizedKey = requireNonNullElse(key, "");
    for (var messageResourceConfig : findMessageResourceConfigs()) {
      if (normalizedKey.equals(messageResourceConfig.getKey())) {
        return messageResourceConfig;
      }
    }
    return null;
  }

  default MessageResources getMessageResources() {
    return getMessageResources("");
  }

  default ResourceBundleMessageResources getMessageResources(
    @Nullable String key
  ) {
    var config = findMessageResourceConfig(key);
    if (config != null) {
      return config.toMessageResources();
    }
    if (key == null) throw new IllegalArgumentException(
      "A message bundle name should not be null."
    );
    return ResourceBundleMessageResources.load(key);
  }

  /**
   * Return the message resources configurations for this module. If there are
   * none, a zero-length array is returned.
   */
  MessageResourcesConfig[] findMessageResourceConfigs();
}
