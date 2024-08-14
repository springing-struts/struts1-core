package org.apache.commons.beanutils;


import org.springframework.beans.BeanUtils;
import springing.util.BeanMap;

import java.util.HashMap;
import java.util.Map;

public class WrapDynaBean implements MapBackedDynaBean {

  public WrapDynaBean(Object bean) {
    var clazz = bean.getClass();
    for (var d : BeanUtils.getPropertyDescriptors(clazz)) {
      propertiesByName.put(
        d.getName(),
        new DynaProperty<>(d.getName(), d.getPropertyType())
      );
    }
    className = clazz.getSimpleName();
    values = new BeanMap(bean);
  }

  private final String className;
  private final BeanMap values;
  private final Map<String, DynaProperty<?>> propertiesByName = new HashMap<>();

  @Override
  public String getName() {
    return className;
  }

  @Override
  public Map<String, DynaProperty<?>> getPropertiesByPropertyName() {
    return propertiesByName;
  }

  @Override
  public Map<String, Object> getValuesByPropertyName() {
    return values;
  }
}
