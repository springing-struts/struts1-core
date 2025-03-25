package springing.util;

import static java.lang.String.format;
import static springing.util.StringUtils.lowerCamelize;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.beans.FeatureDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import javax.xml.stream.*;
import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

public class ObjectUtils {

  private ObjectUtils() {}

  private static XmlMapper initializeXmlMapper() {
    var module = new JacksonXmlModule();
    module.addDeserializer(
      String.class,
      new StdScalarDeserializer<String>(String.class) {
        @Override
        public String deserialize(
          JsonParser jsonParser,
          DeserializationContext deserializationContext
        ) throws IOException, JacksonException {
          return jsonParser.getValueAsString().trim();
        }
      }
    );
    var mapper = new XmlMapper(module);
    var factory = mapper.getFactory().getXMLInputFactory();
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    return mapper;
  }

  private static final XmlMapper XML_MAPPER = initializeXmlMapper();

  public static <T> T createInstanceOf(String fqn) {
    return createInstanceOf(fqn, null);
  }

  public static <T> T createInstanceOf(Class<T> clazz) {
    return createInstanceOf(clazz, null);
  }

  public static <T> T createInstanceOf(
    String fqn,
    @Nullable Map<String, ?> props
  ) {
    return createInstanceOf(classFor(fqn), props);
  }

  public static <T> T createInstanceOf(
    Class<T> clazz,
    @Nullable Map<String, ?> props
  ) {
    return createInstanceOf(clazz, List.of(), List.of(), props);
  }

  public static <T> T createInstanceOf(
    Class<T> clazz,
    List<Class<?>> constructorArgTypes,
    List<Object> constructorArgs,
    @Nullable Map<String, ?> props
  ) {
    try {
      var constructor = clazz.getDeclaredConstructor(
        constructorArgTypes.toArray(new Class[0])
      );
      constructor.setAccessible(true);
      var instance = constructor.newInstance(
        constructorArgs.toArray(new Object[0])
      );
      if (props != null) {
        setProperties(instance, props);
      }
      return instance;
    } catch (
      InstantiationException
      | IllegalAccessException
      | InvocationTargetException
      | NoSuchMethodException e
    ) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets properties on a given bean.
   *
   * @param bean The bean on which to set the properties.
   * @param props A map of property names to values. The property names are expected to be in any
   *     case, as they will be converted to lower camel case.
   */
  public static void setProperties(Object bean, Map<String, ?> props) {
    var normalizedProps = new HashMap<String, Object>();
    props.forEach((key, value) -> {
      normalizedProps.put(lowerCamelize(key), value);
    });
    if (bean instanceof DynaBean dynaBean) {
      setPropertiesToDynaBean(dynaBean, normalizedProps);
    } else {
      setPropertiesToBean(bean, normalizedProps);
    }
  }

  private static void setPropertiesToDynaBean(
    DynaBean bean,
    Map<String, ?> props
  ) {
    props.forEach(bean::set);
  }

  private static void setPropertiesToBean(Object bean, Map<String, ?> props) {
    var descriptors = Arrays.asList(
      BeanUtils.getPropertyDescriptors(bean.getClass())
    );
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
      var checkboxCleared = descriptors
        .stream()
        .anyMatch(d -> {
          if (!clearCheckboxKey.equals(d.getName())) {
            return false;
          }
          var type = d.getPropertyType();
          return type.equals(Boolean.class) || type.equals(boolean.class);
        });
      if (checkboxCleared) {
        declaredProps.put(clearCheckboxKey, false);
      }
    });
    var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
    wrapper.setPropertyValues(declaredProps);
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
    return retrieveValue(bean, propName, true);
  }

  public static @Nullable Object retrieveValue(
    @Nullable Object bean,
    @Nullable String propName,
    boolean evaluatesNestedProperties
  ) {
    if (bean == null) {
      return null;
    }
    if (propName == null || propName.isEmpty()) {
      return bean;
    }
    var obj = bean;
    if (bean instanceof ActionMessages messages) {
      return messages.get(propName);
    }
    if (!evaluatesNestedProperties) {
      return getProp(obj, propName);
    }
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
      var v = mapModel.get(name);
      return isIndexed ? getAt(v, index) : v;
    }
    if (bean instanceof DynaBean dynaBean) {
      return isIndexed ? dynaBean.get(name, index) : dynaBean.get(name);
    }
    try {
      if (isIndexed) {
        var accessor = getIndexedAccessor(bean, name);
        if (accessor != null) {
          return accessor.invoke(bean, index);
        }
      }
      var accessor = getAccessor(bean, name);
      var propValue = accessor.invoke(bean);
      return isIndexed ? getAt(propValue, index) : propValue;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static Method getAccessor(Object bean, String name) {
    var propDesc = BeanUtils.getPropertyDescriptor(bean.getClass(), name);
    if (propDesc == null) throw new IllegalArgumentException(
      format(
        "Unknown property [%s] of the bean class [%s].",
        name,
        bean.getClass().getName()
      )
    );
    return propDesc.getReadMethod();
  }

  private static @Nullable Method getIndexedAccessor(Object bean, String name) {
    var accessorName = lowerCamelize("get", name);
    try {
      var accessor = bean.getClass().getMethod(accessorName, Integer.TYPE);
      accessor.setAccessible(true);
      return accessor;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  public static @Nullable Integer getSize(@Nullable Object items) {
    if (items == null) {
      return null;
    }
    var clazz = items.getClass();
    if (clazz.isArray()) {
      return Array.getLength(items);
    }
    if (items instanceof Map<?, ?> map) {
      return map.size();
    }
    if (items instanceof Collection<?> collection) {
      return collection.size();
    }
    if (items instanceof Iterable<?> iterable) {
      var iterator = iterable.iterator();
      var count = 0;
      while (iterator.hasNext()) {
        iterator.next();
        count++;
      }
      return count;
    }
    throw new IllegalArgumentException(
      format(
        "It is impossible to count the size of given object of type [%s].",
        clazz.getCanonicalName()
      )
    );
  }

  public static boolean isEmpty(@Nullable Object value) {
    if (value == null) {
      return true;
    }
    if (value instanceof String str) {
      return str.isBlank();
    }
    if (value instanceof Collection<?> collection) {
      return collection.isEmpty();
    }
    if (value instanceof Map<?, ?> map) {
      return map.isEmpty();
    }
    if (value instanceof Iterator<?> itr) {
      return itr.hasNext();
    }
    if (value.getClass().isArray()) {
      return Array.getLength(value) == 0;
    }
    return false;
  }

  public static @Nullable Object getAt(Object indexedValue, int index) {
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
    throw new IllegalArgumentException(
      format(
        "Index access is not allowed for the type [%s].",
        clazz.getCanonicalName()
      )
    );
  }

  public static final Pattern INDEXED_PROPERTY = Pattern.compile(
    "^([_a-zA-Z][_a-zA-Z0-9]*)\\[([0-9]+)]$"
  );

  public static Iterator<?> asIterator(Object value) {
    return asIterator(value, null, null);
  }

  public static Iterator<?> asIterator(
    @Nullable Object value,
    @Nullable Number maxSize,
    @Nullable Number offset
  ) {
    var from = offset == null ? 0 : offset.intValue();
    var to = maxSize == null ? Integer.MAX_VALUE : (from + maxSize.intValue());
    if (from < 0 || from > to) throw new IllegalArgumentException(
      format(
        "MaxSize and offset can not be negative: maxSize [%s], offset [%s]. ",
        maxSize,
        offset
      )
    );
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
        return List.of(value).iterator();
      }
    }
  }

  private static <T> Iterator<T> subIterator(
    Iterator<T> original,
    int from,
    int to
  ) {
    if (from >= to) {
      return Collections.emptyIterator();
    }
    return new Iterator<T>() {
      private long index = 0;

      {
        {
          while (index < from && original.hasNext()) {
            original.next();
            index++;
          }
        }
      }

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
    void preprocess(XMLStreamReader reader, XMLStreamWriter writer)
      throws XMLStreamException;
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
      return XML_MAPPER.readValue(preprocessXml(preprocessor, in), clazz);
    } catch (IOException e) {
      throw new IllegalStateException(
        "Failed to load a configuration file at [" + resourceClassPath + "].",
        e
      );
    }
  }

  private static InputStream preprocessXml(
    @Nullable XmlPreprocessor preprocessor,
    InputStream in
  ) {
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
