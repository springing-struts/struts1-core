package org.apache.commons.beanutils;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DefaultDynaClass implements DynaClass {

  public DefaultDynaClass(String name, DynaProperty... properties) {
    this(name, false, properties);
  }

  public DefaultDynaClass(String name, boolean isLazy, DynaProperty... properties) {
    this.name = name;
    this.lazy = isLazy;
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

  private final boolean lazy;

  public Map<String, DynaProperty> getDynaPropertiesByName() {
    return dynaPropertiesByName;
  }
  private final Map<String, DynaProperty> dynaPropertiesByName;

  @Override
  public DynaProperty[] getDynaProperties() {
    return getDynaPropertiesByName().values().toArray(new DynaProperty[]{});
  }

  @Override
  public @Nullable DynaProperty getDynaProperty(String name) {
    var prop = getDynaPropertiesByName().get(name);
    if (prop == null && lazy) {
      prop = new DynaProperty(name);
      dynaPropertiesByName.put(name, prop);
    }
    return prop;
  }

  @Override
  public DynaBean newInstance() {
    return new DefaultDynaBean();
  }
}
