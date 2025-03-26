package org.apache.commons.beanutils;

import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import springing.util.BeanMap;

public class WrapDynaBean implements MapBackedDynaBean {

  public WrapDynaBean(Object bean) {
    var clazz = bean.getClass();
    var className = clazz.getSimpleName();
    var properties = new ArrayList<DynaProperty>();
    for (var d : BeanUtils.getPropertyDescriptors(clazz)) {
      var prop = new DynaProperty(d.getName(), d.getPropertyType());
      properties.add(prop);
    }
    dynaClass = new DefaultDynaClass(
      className,
      properties.toArray(DynaProperty[]::new)
    );
    values = new BeanMap(bean);
  }

  private final DynaClass dynaClass;
  private final BeanMap values;

  @Override
  public DynaClass getDynaClass() {
    return dynaClass;
  }

  @Override
  public Map<String, Object> getValues() {
    return values;
  }
}
