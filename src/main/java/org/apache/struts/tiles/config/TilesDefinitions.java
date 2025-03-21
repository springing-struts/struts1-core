package org.apache.struts.tiles.config;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The "tiles-definitions" element is the root of the configuration file
 * hierarchy, and contains nested elements for all the other configuration
 * settings.
 */
public class TilesDefinitions {

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "definition")
  @JsonManagedReference
  public void setTilesDefinitions(List<TilesDefinition> tilesDefinitions) {
    for (var definition : tilesDefinitions) {
      tilesDefinitionsByName.put(definition.getName(), definition);
    }
  }

  public @Nullable TilesDefinition getTilesDefinitionByName(String definitionName) {
    return tilesDefinitionsByName.get(definitionName);
  }

  private final Map<String, TilesDefinition> tilesDefinitionsByName = new HashMap<>();

  public void merge(TilesDefinitions another) {
    tilesDefinitionsByName.putAll(another.tilesDefinitionsByName);
  }
}
