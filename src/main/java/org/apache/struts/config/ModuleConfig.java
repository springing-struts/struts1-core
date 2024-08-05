package org.apache.struts.config;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.PlugIn;
import org.apache.struts.validator.ValidatorPlugIn;
import org.springframework.lang.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static javax.xml.stream.XMLStreamConstants.*;
import static springing.util.ObjectUtils.parseConfigFileAt;
import static springing.util.StringUtils.normalizeForwardPath;

/**
 * The "struts-config" element is the root of the configuration file hierarchy,
 * and contains nested elements for all of them other configuration settings.
 */
@JacksonXmlRootElement(localName = "struts-config")
public class ModuleConfig {

  /**
   * The prefix of the context-relative portion of the request URI, used to
   * select this configuration versus others supported by the controller
   * servlet.  A configuration with a prefix of a zero-length String is the
   * default configuration for this web module.
   */
  public String getPrefix() {
    return normalizeForwardPath(modulePrefix);
  }
  public void setPrefix(String module) {
    this.modulePrefix = normalizeForwardPath(module);
  }
  private @Nullable String modulePrefix;

  /**
   * The controller configuration object for this module.
   */
  public @Nullable ControllerConfig getControllerConfig() {
    return controllerConfig;
  }
  @JacksonXmlProperty(localName = "controller")
  private @Nullable ControllerConfig controllerConfig;

  /**
   * Returns the comma-separated file paths of the configuration files.
   */
  public String getConfigFilePaths() {
    return configFilePaths == null ? "" : configFilePaths;
  }
  private @Nullable String configFilePaths;

  /**
   * The default class name to be used when creating action form bean
   * instances.
   */
  public String getActionFormBeanClass() {
    return formBeans.getFormBeanClass().getCanonicalName();
  }

  /**
   * Return the form bean configuration for the specified key, if any;
   * otherwise return `null`.
   */
  public @Nullable FormBeanConfig findFormBeanConfig(String name) {
    for (var config : getFormBeanConfigs()) {
      if (name.equals(config.getName())) {
        return config;
      }
    }
    return null;
  }
  /**
   * Returns of the list of form-beans, which are JavaBeans that implement the
   * `org.apache.struts.action.ActionForm` class.
   */
  public List<FormBeanConfig> getFormBeanConfigs() {
    return formBeans.getEntries();
  }
  @JacksonXmlElementWrapper(localName = "form-beans")
  private FormBeansConfig formBeans;

  /**
   * Returns ActionConfig objects that is to be used to process a request for
   * a specific module-relative URI.
   */
  public List<ActionConfig> getActionConfigs() {
    return actionMappings.getEntries();
  }

  /**
   * Return the action configuration for the specified path, if any; otherwise
   * return `null`.
   */
  public @Nullable ActionConfig findActionConfig(String path) {
    var normalizedPath = normalizeForwardPath(path);
    return getActionConfigs()
     .stream()
     .filter(it -> normalizedPath.equals(it.getPath()))
     .findAny()
     .orElse(null);
  }

  /**
   * Return the action configurations for this module. If there are none,
   * a zero-length array is returned.
   */
  public ActionConfig[] findActionConfigs() {
    return actionMappings.getEntries().toArray(new ActionConfig[]{});
  }
  @JacksonXmlProperty(localName = "action-mappings")
  @JsonManagedReference
  private ActionMappingsConfig actionMappings;

  /**
   * Returns the list of ExceptionConfigs which describe a set of exceptions
   * that might be thrown by an Action object.
   */
  public List<ExceptionConfig> getGlobalExceptions() {
    return globalExceptions;
  }
  @JacksonXmlElementWrapper(localName = "global-exceptions")
  @JacksonXmlProperty(localName = "exception")
  private List<ExceptionConfig> globalExceptions = new ArrayList<>();

  /**
   * Return the forward configuration for the specified key, if any; otherwise
   * return `null`.
   */
  public @Nullable ForwardConfig findForwardConfig(String name) {
    for (var forwardConfig : getGlobalForwards()) {
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
  public ForwardConfig[] findForwards() {
    return getGlobalForwards().toArray(new ForwardConfig[]{});
  }

  /**
   * Returns set of `ActionForward` objects that are available to all Action
   * objects as a return value.
   */
  public List<ActionForward> getGlobalForwards() {
    return globalForwards;
  }
  @JacksonXmlElementWrapper(localName = "global-forwards")
  @JacksonXmlProperty(localName = "forward")
  void setGlobalForwards(List<ActionForward> forwards) {
    forwards.forEach(it -> it.setModuleConfig(this));
    this.globalForwards = forwards;
  }
  private List<ActionForward> globalForwards = new ArrayList<>();

  /**
   * Returns a list of `MessageResourcesConfig` which describes a
   * MessageResources object with message templates for this module.
   */
  public List<MessageResourcesConfig> getMessageResourcesConfigs() {
    return messageResourcesConfigs;
  }
  @JacksonXmlProperty(localName = "message-resources")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<MessageResourcesConfig> messageResourcesConfigs = new ArrayList<>();

  public String getMessageResourcesBasename() {
    for (var config : messageResourcesConfigs) {
      if (config.getKey().isEmpty()) {
        return config.getConfig();
      }
    }
    throw new IllegalArgumentException(
      "There is no default message resources definition in the module: " + getPrefix()
    );
  }

  /**
   * Returns a set of plugin configuration objects which describe fully
   * qualified class name of a general-purpose application plug-in module that
   * receives notification of application startup and shutdown events.
   * An instance of the specified class is created for each element, and can be
   * configured with nested `<set-property>` elements.
   */
  public List<PlugInConfig> getPluginConfigs() {
    return pluginConfigs;
  }
  @JacksonXmlProperty(localName = "plug-in")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private List<PlugInConfig> pluginConfigs = new ArrayList<>();

  /**
   * Returns a loaded plugin instance of the given type, or creates a new one
   * from configuration if it has not been loaded yet. `null` will be returned
   * if the corresponding configuration is not included in this module.
   */
  public <T extends PlugIn> @Nullable T getPlugInByType(Class<T> pluginType) {
    var loadedPlugIn = loadedPlugIns.computeIfAbsent(pluginType, (type) -> {
      for (var config : pluginConfigs) {
        if (type.getName().equals(config.getClassName())) {
          return config.loadPlugIn();
        }
      }
      return null;
    });
    return (T) loadedPlugIn;
  }

  /**
   * Returns the validation resources associated with this module.
   */
  public ValidatorResources getValidatorResources() {
    var plugIn = getPlugInByType(ValidatorPlugIn.class);
    if (plugIn == null) {
      return new ValidatorResources();
    }
    return plugIn.getValidatorResources();
  }

  private final ConcurrentMap<Class<? extends PlugIn>, PlugIn>
    loadedPlugIns = new ConcurrentHashMap<>();

  public void merge(ModuleConfig another) {
    if (!getPrefix().equals(another.getPrefix())) throw new IllegalArgumentException(String.format(
      "Struts configuration file at [%s] could not be merged with configuration file at [%s]" +
      " because it is from another module.", configFilePaths, another.configFilePaths
    ));
    if (controllerConfig != null && another.controllerConfig != null) throw new RuntimeException(String.format(
      "Struts configuration file at [%s] could not be merged with configuration file at [%s]" +
      " because they have conflicting controller settings in a module [%s].",
        configFilePaths, another.configFilePaths, modulePrefix
    ));
    getActionConfigs().addAll(another.getActionConfigs());
    getFormBeanConfigs().addAll(another.getFormBeanConfigs());
    getGlobalForwards().addAll(another.getGlobalForwards());
    getGlobalExceptions().addAll(another.getGlobalExceptions());
    getMessageResourcesConfigs().addAll(another.getMessageResourcesConfigs());
    getPluginConfigs().addAll(another.getPluginConfigs());
    if (another.controllerConfig != null) {
      controllerConfig = another.controllerConfig;
    }
    this.configFilePaths = this.configFilePaths + ", " + another.configFilePaths;
    updateBackReferences();
  }

  public void updateBackReferences() {
    getActionConfigs().forEach(it -> it.setModuleConfig(this));
  }

  public static ModuleConfig loadFrom(
      String classPath,
      String module
  ) {
    var config = parseConfigFileAt(
            classPath,
            ModuleConfig.class,
            (reader, writer) -> {
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
            }
        );
        config.configFilePaths = classPath;
        config.setPrefix(module);
        config.updateBackReferences();
        return config;
  }
}
