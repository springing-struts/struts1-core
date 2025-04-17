package org.apache.struts.config;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static javax.xml.stream.XMLStreamConstants.*;
import static org.springframework.util.StringUtils.hasText;
import static springing.util.ObjectUtils.parseConfigFileAt;
import static springing.util.StringUtils.normalizeForwardPath;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.action.*;
import org.apache.struts.tiles.TilesPlugin;
import org.apache.struts.tiles.config.TilesDefinitions;
import org.apache.struts.validator.ValidatorPlugIn;
import org.springframework.lang.Nullable;

/**
 * The "struts-config" element is the root of the configuration file hierarchy,
 * and contains nested elements for all of them other configuration settings.
 */
@JacksonXmlRootElement(localName = "struts-config")
public class ModuleConfigBean implements ModuleConfig {

  /**
   * A short (one line) description of this configuration.
   */
  @JacksonXmlProperty(localName = "display-name")
  private @Nullable String displayName;

  /**
   *  A descriptive (paragraph length) text about this configuration.
   */
  @JacksonXmlProperty(localName = "description")
  private @Nullable String description;

  /**
   * The prefix of the context-relative portion of the request URI, used to
   * select this configuration versus others supported by the controller
   * servlet. A configuration with a prefix of a zero-length String is the
   * default configuration for this web module.
   */
  @Override
  public String getPrefix() {
    return requireNonNull(modulePrefix);
  }

  public void setPrefix(String modulePrefix) {
    this.modulePrefix = normalizeModulePrefix(modulePrefix);
  }

  public boolean isDefaultModule() {
    return getPrefix().isBlank();
  }

  private @Nullable String modulePrefix;

  public static String normalizeModulePrefix(@Nullable String modulePrefix) {
    if (!hasText(modulePrefix) || modulePrefix.equals("/")) {
      return "";
    }
    return ("/" + modulePrefix).replaceAll("//+", "/").replaceAll("/$", "");
  }

  public String appendPrefix(String path) {
    return normalizeForwardPath(modulePrefix + "/" + path);
  }

  /**
   * The controller configuration object for this module.
   */
  @Override
  public @Nullable ControllerConfig getControllerConfig() {
    return controllerConfig;
  }

  @JacksonXmlProperty(localName = "controller")
  private @Nullable ControllerConfig controllerConfig;

  /**
   * Returns a request processor instance for this module.
   */
  public RequestProcessor getRequestProcessor() {
    if (requestProcessor != null) {
      return requestProcessor;
    }
    requestProcessor = requireNonNullElse(
      controllerConfig,
      new ControllerConfig()
    ).createInstance();
    return requestProcessor;
  }

  private @Nullable RequestProcessor requestProcessor;

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
  @Override
  public String getActionFormBeanClass() {
    return formBeans.getFormBeanConfigClass().getCanonicalName();
  }

  /**
   * Return the form bean configuration for the specified key, if any;
   * otherwise return `null`.
   */
  @Override
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
  public void setFormBeansConfig(FormBeansConfig formBeans) {
    this.formBeans = formBeans;
  }

  private FormBeansConfig formBeans = new FormBeansConfig(null);

  /**
   * Return the action configurations for this module. If there are none,
   * a zero-length array is returned.
   */
  @Override
  public ActionConfig[] findActionConfigs() {
    return actionConfigs.toArray(new ActionConfig[0]);
  }

  @JacksonXmlProperty(localName = "action-mappings")
  public void setActionMappings(ActionMappingsConfig config) {
    for (var actionMapping : config.getEntries()) {
      registerActionMapping(actionMapping);
    }
  }

  private void registerActionMapping(ActionConfig mapping) {
    actionConfigs.add(mapping);
  }

  private final List<ActionConfig> actionConfigs = new ArrayList<>();

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
   * Return the form bean configurations for this module. If there are none,
   * a zero-length array is returned.
   */
  @Override
  public ForwardConfig[] findForwards() {
    return getGlobalForwards().toArray(new ForwardConfig[] {});
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

  @Override
  public MessageResourcesConfig[] findMessageResourceConfigs() {
    return messageResourcesConfigs.toArray(new MessageResourcesConfig[] {});
  }

  /**
   * Returns a list of `MessageResourcesConfig` which describes a
   * MessageResources object with message templates for this module.
   */
  public List<MessageResourcesConfig> getMessageResourcesConfigs() {
    return messageResourcesConfigs;
  }

  @JacksonXmlProperty(localName = "message-resources")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setMessageResourcesConfigs(
    List<MessageResourcesConfig> messageResourcesConfigs
  ) {
    this.messageResourcesConfigs = messageResourcesConfigs;
  }

  private List<MessageResourcesConfig> messageResourcesConfigs =
    new ArrayList<>();

  /**
   * Returns a set of plugin configuration objects which describe fully
   * qualified class name of a general-purpose application plug-in module that
   * receives notification of application startup and shutdown events.
   * An instance of the specified class is created for each element, and can be
   * configured with nested `set-property` elements.
   */
  public List<PlugInConfig> getPluginConfigs() {
    return pluginConfigs;
  }

  @JacksonXmlProperty(localName = "plug-in")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonManagedReference
  private List<PlugInConfig> pluginConfigs = new ArrayList<>();

  /**
   * Returns a loaded plugin instance of the given type.
   * `null` will be returned if the corresponding configuration is not included
   * in this module.
   */
  public <T extends PlugIn> @Nullable T getPlugInByType(Class<T> pluginType) {
    if (loadedPlugIns == null) throw new IllegalStateException(
      "Plugins for this module have not been loaded yet."
    );
    return (T) loadedPlugIns.get(pluginType);
  }

  public synchronized void loadPlugins(ActionServlet actionServlet) {
    if (loadedPlugIns != null) {
      return;
    }
    loadedPlugIns = new ConcurrentHashMap<>();
    for (var config : pluginConfigs) {
      var loadedPlugIn = config.loadPlugIn(actionServlet);
      loadedPlugIns.put(loadedPlugIn.getClass(), loadedPlugIn);
    }
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

  /**
   * Returns Struts tiles definition list associated with this module.
   */
  public TilesDefinitions getTilesDefinitions() {
    var plugIn = getPlugInByType(TilesPlugin.class);
    if (plugIn == null) {
      return new TilesDefinitions();
    }
    return plugIn.getTilesDefinitions();
  }

  private @Nullable ConcurrentMap<
    Class<? extends PlugIn>,
    PlugIn
  > loadedPlugIns;

  public void merge(ModuleConfigBean another) {
    if (
      !getPrefix().equals(another.getPrefix())
    ) throw new IllegalArgumentException(
      format(
        "Struts configuration file at [%s] could not be merged with configuration file at [%s]" +
        " because it is from another module.",
        configFilePaths,
        another.configFilePaths
      )
    );
    if (
      controllerConfig != null && another.controllerConfig != null
    ) throw new RuntimeException(
      format(
        "Struts configuration file at [%s] could not be merged with configuration file at [%s]" +
        " because they have conflicting controller settings in a module [%s].",
        configFilePaths,
        another.configFilePaths,
        modulePrefix
      )
    );
    another.actionConfigs.forEach(this::registerActionMapping);
    getFormBeanConfigs().addAll(another.getFormBeanConfigs());
    getGlobalForwards().addAll(another.getGlobalForwards());
    getGlobalExceptions().addAll(another.getGlobalExceptions());
    getMessageResourcesConfigs().addAll(another.getMessageResourcesConfigs());
    getPluginConfigs().addAll(another.getPluginConfigs());
    if (another.controllerConfig != null) {
      controllerConfig = another.controllerConfig;
    }
    this.configFilePaths =
      this.configFilePaths + ", " + another.configFilePaths;
    updateBackReferences();
  }

  private void updateBackReferences() {
    actionConfigs.forEach(it -> it.setModuleConfig(this));
  }

  public static class Visitor {

    private @Nullable String actionMappingType = null;
    private @Nullable String formBeanConfigType = null;

    public void startElement(String elementName, XMLStreamWriter writer)
      throws XMLStreamException {
      writer.writeStartElement(elementName);
      if ("action".equals(elementName)) {
        writer.writeAttribute(
          "class",
          requireNonNullElse(actionMappingType, ActionMapping.class.getName())
        );
      }
      if ("form-bean".equals(elementName)) {
        writer.writeAttribute(
          "class",
          requireNonNullElse(formBeanConfigType, FormBeanConfig.class.getName())
        );
      }
    }

    public void sawText(
      @Nullable String elementName,
      String text,
      XMLStreamWriter writer
    ) throws XMLStreamException {
      writer.writeCharacters(text);
    }

    public void sawAttribute(
      String elementName,
      String key,
      String value,
      XMLStreamWriter writer
    ) throws XMLStreamException {
      if ("action-mappings".equals(elementName) && "type".equals(key)) {
        actionMappingType = value;
      }
      if ("form-beans".equals(elementName) && "type".equals(key)) {
        formBeanConfigType = value;
      }
      if ("action".equals(elementName) && "forward".equals(key)) {
        writer.writeAttribute("forward-path", value);
      } else {
        writer.writeAttribute(key, value);
      }
    }

    public void endElement(String elementName, XMLStreamWriter writer)
      throws XMLStreamException {
      writer.writeEndElement();
      if ("action-mappings".equals(elementName)) {
        actionMappingType = null;
      }
      if ("form-beans".equals(elementName)) {
        formBeanConfigType = null;
      }
    }
  }

  public static ModuleConfigBean loadFrom(String classPath, String moduleName) {
    var visitor = new Visitor();
    var config = parseConfigFileAt(
      classPath,
      ModuleConfigBean.class,
      (reader, writer) -> {
        String elementName = null;
        while (reader.hasNext()) {
          var event = reader.next();
          switch (event) {
            case START_ELEMENT:
              elementName = reader.getLocalName();
              visitor.startElement(elementName, writer);
              for (var i = 0; i < reader.getAttributeCount(); i++) {
                var key = reader.getAttributeLocalName(i);
                var value = reader.getAttributeValue(i);
                visitor.sawAttribute(elementName, key, value, writer);
              }
              break;
            case CHARACTERS:
              var text = reader.getText();
              visitor.sawText(elementName, text, writer);
              break;
            case END_ELEMENT:
              visitor.endElement(requireNonNull(elementName), writer);
              break;
            default:
              break;
          }
        }
      }
    );
    config.configFilePaths = classPath;
    config.setPrefix(moduleName);
    config.updateBackReferences();
    return config;
  }
}
