package org.apache.commons.beanutils;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static springing.util.ObjectUtils.classFor;

public class DynaProperty<T> {

  public DynaProperty(String name, Class<T> type) {
    this.name = name;
    this.type = type;
  }

  public DynaProperty(String name, String type) {
    this(name, (Class<T>) classFor(type));
  }

  /**
   * The name of this property.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The Java class representing the data type of the underlying property values.
   */
  public Class<T> getType() {
    return type;
  }
  private final Class<T> type;

  /**
   * Returns whether this property represents an indexed value (ie an array or List).
   */
  public boolean isIndexed() {
    return type.isArray() || List.class.isAssignableFrom(type);
  }

  /**
   * Returns whether this property represents a keyed value (ie a Map).
   */
  public boolean isKeyed() {
    return Map.class.isAssignableFrom(type);
  }

  public @Nullable T getValueFrom(Map<String, Object> values) {
    var value = values.get(name);
    if (value == null) {
      return null;
    }
    TypeConverter converter = new SimpleTypeConverter();
    return converter.convertIfNecessary(value, getType());
  }

  public List<Object> getIndexedValueFrom(Map<java.lang.String, Object> values) {
    if (!isIndexed()) throw new IllegalStateException(String.format(
        "%s (type: %s) is not a indexed property.", name, getType()
    ));
    T v = getValueFrom(values);
    if (v instanceof int[] array) {
      return IntStream.of(array).boxed().map(it -> (Object) it).toList();
    }
    if (v instanceof long[] array) {
      return LongStream.of(array).boxed().map(it -> (Object) it).toList();
    }
    if (v instanceof double[] array) {
      return DoubleStream.of(array).boxed().map(it -> (Object) it).toList();
    }
    if (v instanceof Object[] array) {
      return Arrays.asList(array);
    }
    return (List<Object>) v;
  }

  public Map<String, Object> getKeyedValueFrom(Map<java.lang.String, Object> values) {
    if (!isKeyed()) throw new IllegalStateException(String.format(
        "%s (type: %s) is not a map type property.", name, getType()
    ));
    return (Map<String, Object>) getValueFrom(values);
  }

  public void setValueTo(Map<String, Object> values, @Nullable Object value) {
    if (value == null) {
      values.put(name, value);
      return;
    }
    var valueType = value.getClass();
    if (!type.isAssignableFrom(valueType)) throw new IllegalArgumentException(String.format(
        "The type of the assigned value [%s] is not applicable to the type [%s] of the property [%s].",
        valueType.getSimpleName(), getType(), name
    ));
    values.put(name, value);
  }
}
