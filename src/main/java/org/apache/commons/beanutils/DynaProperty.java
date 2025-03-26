package org.apache.commons.beanutils;

import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;
import static springing.util.ObjectUtils.classFor;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.lang.Nullable;

public class DynaProperty {

  public DynaProperty(String name) {
    this.name = name;
    this.type = String.class;
    this.lazy = true;
  }

  public DynaProperty(String name, Class<?> type) {
    this.name = name;
    this.type = type;
    this.lazy = false;
  }

  public DynaProperty(String name, String type) {
    this(name, classFor(type));
  }

  private final boolean lazy;

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
  public Class<?> getType() {
    return requireNonNullElse(type, String.class);
  }

  private @Nullable Class<?> type;

  /**
   * Returns whether this property represents an indexed value (ie an array or List).
   */
  public boolean isIndexed() {
    return (
      lazy || getType().isArray() || List.class.isAssignableFrom(getType())
    );
  }

  /**
   * Returns whether this property represents a keyed value (ie a Map).
   */
  public boolean isKeyed() {
    return lazy || Map.class.isAssignableFrom(getType());
  }

  public @Nullable Object getValueFrom(Map<String, Object> values) {
    var value = values.get(name);
    if (value == null) {
      return null;
    }
    return new SimpleTypeConverter().convertIfNecessary(value, getType());
  }

  public List<Object> getIndexedValueFrom(
    Map<java.lang.String, Object> values
  ) {
    if (!isIndexed()) throw new IllegalStateException(
      format("%s (type: %s) is not a indexed property.", name, getType())
    );
    Object v = getValueFrom(values);
    if (lazy && v == null) {
      type = List.class;
      v = new ArrayList<>();
      values.put(name, v);
    }
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

  public Map<String, Object> getKeyedValueFrom(
    Map<java.lang.String, Object> values
  ) {
    if (!isKeyed()) throw new IllegalStateException(
      format("%s (type: %s) is not a map type property.", name, getType())
    );
    var v = getValueFrom(values);
    if (lazy && v == null) {
      type = Map.class;
      v = new HashMap<String, Object>();
      values.put(name, v);
    }
    return (Map<String, Object>) v;
  }

  public void setValueTo(Map<String, Object> values, @Nullable Object value) {
    if (lazy) {
      if (value == null) {
        type = null;
      } else if (type == null) {
        type = value.getClass();
      }
    }
    if (value == null) {
      values.put(name, value);
      return;
    }
    var valueType = value.getClass();
    values.put(
      name,
      new SimpleTypeConverter().convertIfNecessary(value, valueType)
    );
  }
}
