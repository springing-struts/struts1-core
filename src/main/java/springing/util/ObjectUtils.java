package springing.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import javax.xml.stream.*;
import java.beans.FeatureDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class ObjectUtils {
  private ObjectUtils() {}

  private static final XmlMapper xmlMapper = new XmlMapper();

  public static <T> T createInstanceOf(String fqn) {
    return createInstanceOf(fqn, null);
  }

  public static <T> T createInstanceOf(Class<T> clazz) {
    return createInstanceOf(clazz, null);
  }

  public static <T> T createInstanceOf(String fqn, @Nullable Map<String, ?> props) {
    return createInstanceOf(classFor(fqn), props);
  }

  public static <T> T createInstanceOf(Class<T> clazz, @Nullable Map<String, ?> props) {
    try {
      var constructor = clazz.getDeclaredConstructor();
      constructor.setAccessible(true);
      var instance = (T) constructor.newInstance();
      if (props != null) {
        var descriptors = Arrays.asList(BeanUtils.getPropertyDescriptors(clazz));
        var names = descriptors.stream().map(FeatureDescriptor::getName).toList();
        var declaredProps = new HashMap<String, Object>();
        props.forEach((key, value) -> {
          if (names.contains(key)) {
            declaredProps.put(key, value);
            return;
          }
          var clearCheckboxKey = key.startsWith("_") ? key.substring(1) : "";
          if (clearCheckboxKey.isEmpty()) {
            return;
          }
          var checkboxCleared = descriptors.stream().anyMatch(d -> {
            if (!clearCheckboxKey.equals(d.getName())) return false;
            var type  = d.getPropertyType();
            return type.equals(Boolean.class) || type.equals(boolean.class);
          });
          if (checkboxCleared) {
            declaredProps.put(clearCheckboxKey, false);
          }
        });
        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(instance);
        wrapper.setPropertyValues(declaredProps);
      }
      return instance;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> Class<T> classFor(String fqn) {
    var classLoader = Thread.currentThread().getContextClassLoader();
    try {
      return (Class<T>) Class.forName(fqn, true, classLoader);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static @Nullable Object retrieveValue(
    @Nullable Object bean,
    @Nullable String propName
  ) {
    if (bean == null) {
      return null;
    }
    if (propName == null || propName.isEmpty()) {
      return bean;
    }
    var obj = bean;
    for (var prop : propName.split("\\.")) {
      obj = getProp(obj, prop);
      if (obj == null) {
        return null;
      }
    }
    return obj;
  }

  public static @Nullable Integer retrieveInt(
    @Nullable Object bean,
    @Nullable String propName
  ) {
    var value = retrieveValue(bean, propName);
    if (value == null) return null;
    if (value instanceof Number num) return num.intValue();
    return Integer.parseInt(value.toString());
  }

  private static @Nullable Object getProp(Object bean, String prop) {
    var m = INDEXED_PROPERTY.matcher(prop);
    var isIndexed = m.matches();
    var index = isIndexed ? Integer.valueOf(m.group(2)) : null;
    var name = isIndexed ? m.group(1) : prop;
    if (bean instanceof Map<?, ?> mapModel) {
      return getAt(mapModel.get(name), index);
    }
    var propDesc = BeanUtils.getPropertyDescriptor(bean.getClass(), name);
    if (propDesc == null) {
      return null;
    }
    try {
      var propValue = propDesc.getReadMethod().invoke(bean);
      return getAt(propValue, index);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static @Nullable Object getAt(
    @Nullable Object indexedValue,
    @Nullable Integer index
  ) {
    if (index == null || indexedValue == null) {
      return indexedValue;
    }
    var clazz = indexedValue.getClass();
    if (clazz.isArray()) {
      return Array.get(indexedValue, index);
    }
    if (indexedValue instanceof List<?> list) {
      return list.get(index);
    }
    if (indexedValue instanceof Iterable<?> iterable) {
      var iterator = iterable.iterator();
      for (var i = 0; iterator.hasNext(); i++) {
        var item = iterator.next();
        if (i == index) {
          return item;
        }
      }
    }
    throw new IllegalArgumentException(String.format(
        "Index access is not allowed for the type %s.", clazz.getCanonicalName()
    ));
  }

  public static final Pattern INDEXED_PROPERTY = Pattern.compile(
      "^([_a-zA-Z][_a-zA-Z0-9]*)\\[([0-9]+)]$"
  );

  public static Iterator<?> asIterator(
    @Nullable Object value,
    @Nullable Number maxSize,
    @Nullable Number offset
  ) {
    var from = offset == null ? 0 : offset.intValue();
    var to = maxSize == null ? Integer.MAX_VALUE : (from + maxSize.intValue());
    if (from < 0 || from > to) throw new IllegalArgumentException(String.format(
      "MaxSize and offset can not be negative: maxSize [%s], offset [%s]. ",
      maxSize, offset
    ));
    if (from == to) {
      return Collections.emptyIterator();
    }
    switch (value) {
      case null -> {
        return Collections.emptyIterator();
      }
      case Iterable<?> iterable -> {
        return subIterator(iterable.iterator(), from, to);
      }
      case Enumeration<?> enumeration -> {
        return subIterator(enumeration.asIterator(), from, to);
      }
      case Object[] array -> {
        var lastIndex = Math.min(array.length, to);
        return subIterator(Arrays.stream(array).iterator(), from, lastIndex);
      }
      case int[] array -> {
        var lastIndex = Math.min(array.length, to);
        return subIterator(Arrays.stream(array).iterator(), from, lastIndex);
      }
      case long[] array -> {
        var lastIndex = Math.min(array.length, to);
        return subIterator(Arrays.stream(array).iterator(), from, lastIndex);
      }
      case double[] array -> {
        var lastIndex = Math.min(array.length, to);
        return subIterator(Arrays.stream(array).iterator(), from, lastIndex);
      }
      default -> {
        throw new IllegalArgumentException(String.format(
          "An instance of type [%s] can not be converted to a Iterator.",
          value.getClass().getName()
        ));
      }
    }
  }

  private static <T> Iterator<T> subIterator(
    Iterator<T> original, int from, int to
  ) {
    if (from >= to) {
      return Collections.emptyIterator();
    }
    return new Iterator<T>() {
      private long index = 0;
      {{
        while (index < from && original.hasNext()) {
          original.next();
          index++;
        }
      }}
      @Override
      public boolean hasNext() {
        if (index >= to) {
          return false;
        }
        return original.hasNext();
      }

      @Override
      public T next() {
        if (index >= to) {
          throw new NoSuchElementException();
        }
        T item = original.next();
        index++;
        return item;
      }
    };
  }

  @FunctionalInterface
  public interface XmlPreprocessor {
    void preprocess(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException;
  }
  public static <T> T parseConfigFileAt(
    String resourceClassPath,
    Class<T> clazz
  ) {
    return parseConfigFileAt(resourceClassPath, clazz, null);
  }

  public static <T> T parseConfigFileAt(
    String resourceClassPath,
    Class<T> clazz,
    @Nullable XmlPreprocessor preprocessor
  ) {
    var resource = new ClassPathResource(resourceClassPath);
    try (var in = resource.getInputStream()) {
      return xmlMapper.readValue(preprocessXml(preprocessor, in), clazz);
    } catch (IOException e) {
      throw new IllegalStateException(
        "Failed to load a configuration file at [" +
        resourceClassPath + "].", e
      );
    }
  }

  private static InputStream preprocessXml(@Nullable XmlPreprocessor preprocessor, InputStream in) {
    if (preprocessor == null) return in;
    XMLStreamReader reader = null;
    XMLStreamWriter writer = null;
    var out = new StringWriter();
    try {
      reader = XMLInputFactory.newFactory().createXMLStreamReader(in);
      writer = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
      preprocessor.preprocess(reader, writer);
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (reader != null) reader.close();
        if (writer != null) writer.close();
      } catch (XMLStreamException e) {
        // NOP
      }
    }
    var xml = out.toString();
    return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
  }
}
