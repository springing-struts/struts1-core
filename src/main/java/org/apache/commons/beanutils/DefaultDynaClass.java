package org.apache.commons.beanutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDynaClass implements DynaClass{

  public DefaultDynaClass(String name, List<DynaProperty<?>> properties) {
    this.name = name;
    dynaPropertiesByName = new HashMap<>();
    for (var p : properties) {
      dynaPropertiesByName.put(p.getName(), p);
    }
  }

  @Override
  public String getName() {
    return name;
  }
  private final String name;

  @Override
  public Map<String, DynaProperty<?>> getDynaPropertiesByName() {
    return dynaPropertiesByName;
  }
  private final Map<String, DynaProperty<?>> dynaPropertiesByName;
}
