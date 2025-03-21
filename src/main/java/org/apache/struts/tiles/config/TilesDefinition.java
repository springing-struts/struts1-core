package org.apache.struts.tiles.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * The "definition" element describes a definition that can be inserted in a
 * jsp page. This definition is identified by its logical name. A definition
 * allows to define all the attributes that can be set in `insert` tag from a
 * jsp page.
 */
public class TilesDefinition {
  public TilesDefinition(
    @JacksonXmlProperty(localName = "name", isAttribute = true) String name,
    @JacksonXmlProperty(localName = "extends", isAttribute = true) @Nullable String inherits
  ) {
    this.name = name;
    this.inherits = inherits;
  }

  @JsonBackReference
  private @Nullable TilesDefinitions definitions;

  private TilesDefinitions getDefinitions() {
    return requireNonNull(definitions);
  }

  private TilesDefinition(TilesDefinition original) {
    this.name = original.name;
    this.path = original.path;
    this.inherits = original.inherits;
    this.definitions = original.definitions;
    this.attributeValuesByName = original.attributeValuesByName;
    this.attributes = original.attributes;
  }

  public TilesDefinition copy() {
    return new TilesDefinition(this);
  }

  /**
   * Name of a definition that is used as ancestor of this definition. All
   * attributes from the ancestor are available to the new definition. Any
   * attribute inherited from the ancestor can be overloaded by providing
   * a new value.
   */
  private final @Nullable String inherits;

  private @Nullable TilesDefinition getParent() {
    if (inherits == null) {
      return null;
    }
    var parent = getDefinitions().getTilesDefinitionByName(inherits);
    if (parent == null) throw new IllegalStateException(format(
      "Unknown tiles definition name [%s].", inherits
    ));
    return parent;
  }

  private <T> @Nullable T getInheritedValue(Class<T> type, Function<TilesDefinition, T> getValue) {
    var parent = getParent();
    if (parent == null) {
      return getValue.apply(this);
    }
    return parent.getInheritedValue(type, getValue);
  }

  /**
   * The unique identifier for this definition.
   */
  public String getName() {
    return name;
  }

  private final String name;

  /**
   * The context-relative path to the resource used as tiles to insert. This
   * tiles will be inserted and a tiles context containing appropriate
   * attributes will be available.
   */
  public String getPath() {
    return requireNonNull(
      getInheritedValue(String.class, it -> it.path),
      format("The path or page attribute is required to the tiles definition [%s].", getName())
    );
  }

  @JacksonXmlProperty(isAttribute = true, localName = "path")
  @JsonAlias("page")
  public void setPath(String path) {
    this.path = path;
  }
  private @Nullable String path;

  public Map<String, TilesAttribute> getAttributes() {
    if (attributeValuesByName != null) {
      return attributeValuesByName;
    }
    var parent = getParent();
    attributeValuesByName = parent == null
      ? new HashMap<>()
      : new HashMap<>(parent.getAttributes());
    for (var attr : attributes) {
      attributeValuesByName.put(attr.getName(), attr);
    }
    return attributeValuesByName;
  }
  private @Nullable Map<String, TilesAttribute> attributeValuesByName;

  @JacksonXmlProperty(localName = "put")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<TilesAttribute> attributes = new ArrayList<>();
}
