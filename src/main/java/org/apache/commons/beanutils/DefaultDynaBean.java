package org.apache.commons.beanutils;

import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DefaultDynaBean implements MapBackedDynaBean {

  public DefaultDynaBean() {
  }

  public DefaultDynaBean(String name, DynaProperty<?>... properties) {
    dynaClass = new DefaultDynaClass(name, Arrays.asList(properties));
  }

  @Override
  public DynaClass getDynaClass() {
    if (dynaClass == null) throw new IllegalStateException(
      "The dynaClass property of a DyanaBean must not be null."
    );
    return dynaClass;
  }
  public void setDynaClass(DynaClass clazz) {
    dynaClass = clazz;
  }
  private @Nullable DynaClass dynaClass = null;

  public Map<String, Object> getValues() {
    return values;
  }
  private final Map<String, Object> values = new HashMap<>();
}
