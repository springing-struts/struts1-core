package org.apache.struts.tiles;

import static java.util.Objects.requireNonNull;
import static springing.util.ObjectUtils.parseConfigFileAt;

import javax.servlet.ServletException;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.tiles.config.TilesDefinitions;
import org.springframework.lang.Nullable;

/**
 * Tiles Plugin used to initialize Tiles. This plugin is to be used with
 * Struts 1.1 in association with `TilesRequestProcessor`.
 * This plugin creates one definition factory for each Struts-module. The
 * definition factory configuration is read first from 'web.xml' (backward
 * compatibility), then it is overloaded with values found in the plugin
 * property values.
 * The plugin changes the Struts configuration by specifying a
 * `TilesRequestProcessor` as request processor. If you want to use your own
 * RequestProcessor, it should subclass TilesRequestProcessor.
 * This plugin can also be used to create one single factory for all modules.
 * This behavior is enabled by specifying `moduleAware=false` in each plugin
 * properties. In this case, the definition factory configuration file is read
 * by the first Tiles plugin to be initialized. The order is determined by the
 * order of modules declaration in web.xml. The first module is always the
 * default one if it exists.
 * The plugin should be declared in each struts-config.xml file in order to
 * properly initialize the request processor.
 */
public class TilesPlugin implements PlugIn {

  @Override
  public void init(ActionServlet servlet, ModuleConfig config)
    throws ServletException {
    if (definitionsConfigPaths == null) throw new IllegalArgumentException(
      "Definitions config paths is required for the TilesPlugin of the module: " +
      config.getPrefix()
    );
    var paths = definitionsConfigPaths.split("\\s*,\\s*");
    tilesDefinitions = new TilesDefinitions();
    for (var path : paths) {
      var definitions = parseConfigFileAt(path, TilesDefinitions.class);
      tilesDefinitions.merge(definitions);
    }
  }

  @Override
  public void destroy() {
    // NOP
  }

  public TilesDefinitions getTilesDefinitions() {
    return requireNonNull(
      tilesDefinitions,
      "This plugin has not been initialized yet."
    );
  }

  private @Nullable TilesDefinitions tilesDefinitions;

  public void setDefinitionsConfig(String paths) {
    definitionsConfigPaths = paths;
  }

  private @Nullable String definitionsConfigPaths;
}
