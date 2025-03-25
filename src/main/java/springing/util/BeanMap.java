package springing.util;

import static java.util.stream.Collectors.toSet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.DynaProperty;
import org.springframework.beans.BeanUtils;

public class BeanMap extends AbstractMap<String, Object> {

  public BeanMap(Object bean) {
    this.bean = bean;
    for (var descriptor : BeanUtils.getPropertyDescriptors(bean.getClass())) {
      var name = descriptor.getName();
      var type = descriptor.getPropertyType();
      descriptorsByName.put(name, descriptor);
      propertiesByName.put(name, new DynaProperty(name, type));
    }
  }

  private final Object bean;
  private final Map<String, DynaProperty> propertiesByName = new HashMap<>();
  private final Map<String, PropertyDescriptor> descriptorsByName =
    new HashMap<>();

  @Override
  public Set<Entry<String, Object>> entrySet() {
    return propertiesByName
      .keySet()
      .stream()
      .map(name -> {
        var descriptor = descriptorsByName.get(name);
        try {
          Object value = descriptor.getReadMethod().invoke(bean);
          return new SimpleEntry<>(name, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(
            String.format(
              "An error occurred while reading the value from the property [%s] of the class [%s].",
              name,
              bean.getClass().getSimpleName()
            ),
            e
          );
        }
      })
      .collect(toSet());
  }

  @Override
  public Object put(String key, Object value) {
    var descriptor = descriptorsByName.get(key);
    if (descriptor == null) throw new IllegalArgumentException(
      String.format(
        "Unknown property name [%s] for bean class [%s].",
        key,
        bean.getClass().getSimpleName()
      )
    );
    try {
      var currentValue = descriptor.getReadMethod().invoke(bean);
      descriptor.getWriteMethod().invoke(bean, value);
      return currentValue;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(
        String.format(
          "An error occurred while writing the value to the property [%s] of the class [%s].",
          key,
          bean.getClass().getSimpleName()
        )
      );
    }
  }
}
