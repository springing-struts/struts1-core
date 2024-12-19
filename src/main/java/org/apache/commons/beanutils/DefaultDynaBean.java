package org.apache.commons.beanutils;

import java.util.HashMap;
import java.util.Map;

public class DefaultDynaBean implements MapBackedDynaBean {

  public DefaultDynaBean() {
    this(DefaultDynaBean.class.getSimpleName(), false);
  }

  public DefaultDynaBean(String name, DynaProperty... properties) {
    this(name, false, properties);
  }

  public DefaultDynaBean(String name, boolean isLazy, DynaProperty... properties) {
    dynaClass = new DefaultDynaClass(name, isLazy, properties);
  }

  @Override
  public DynaClass getDynaClass() {
    return dynaClass;
  }

  private final DynaClass dynaClass;

  public Map<String, Object> getValues() {
    return values;
  }
  private final Map<String, Object> values = new HashMap<>();
}
