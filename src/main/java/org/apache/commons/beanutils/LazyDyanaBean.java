package org.apache.commons.beanutils;

/**
 * DynaBean which automatically adds properties to the DynaClass and provides
 * Lazy List and Lazy Map features.
 * DynaBeans deal with three types of properties - simple, indexed and mapped
 * and have the following get() and set() methods for each of these types:
 * - **Simple property methods**
 *   `get(name)` and `set(name, value)`
 * - **Indexed property methods**
 *   `get(name, index)` and `set(name, index, value)`
 * - **Mapped property methods**
 *   `get(name, key)` and `set(name, key, value)`
 * ### Getting Property Values
 * Calling any of the get() methods, for a property which doesn't exist,
 * returns null in this implementation.
 * ### Setting Simple Properties
 * The LazyDynaBean will automatically add a property to the DynaClass if it
 * doesn't exist when the `set(name, value)` method is called.
 * <pre>
 * DynaBean myBean = new LazyDynaBean();
 * myBean.set("myProperty", "myValue");
 * </pre>
 * ###Setting Indexed Properties
 * If the property doesn't exist, the LazyDynaBean will automatically add a
 * property with an ArrayList type to the DynaClass when the
 * `set(name, index, value)` method is called. It will also instantiate a new
 * ArrayList and automatically grow the List so that it is big enough to
 * accommodate the index being set. ArrayList is the default indexed property
 * that LazyDynaBean uses but this can be easily changed by overriding the
 * `defaultIndexedProperty(name)` method.
 * <pre>
 * DynaBean myBean = new LazyDynaBean();
 * myBean.set("myIndexedProperty", 0, "myValue1");
 * myBean.set("myIndexedProperty", 1, "myValue2");
 * </pre>
 * If the indexed property does exist in the DynaClass but is set to null in
 * the LazyDynaBean, then it will instantiate a new List or Array as specified
 * by the property's type in the DynaClass and automatically grow the List or
 * Array so that it is big enough to accommodate the index being set.
 * <pre>
 * DynaBean myBean = new LazyDynaBean();
 * MutableDynaClass myClass = (MutableDynaClass)myBean.getDynaClass();
 * myClass.add("myIndexedProperty", int[].class);
 * myBean.set("myIndexedProperty", 0, new Integer(10));
 * myBean.set("myIndexedProperty", 1, new Integer(20));
 * </pre>
 * ### Setting Mapped Properties
 * If the property doesn't exist, the LazyDynaBean will automatically add a
 * property with a HashMap type to the DynaClass and instantiate a new HashMap
 * in the DynaBean when the set(name, key, value) method is called. HashMap is
 * the default mapped property that LazyDynaBean uses but this can be easily
 * changed by overriding the defaultMappedProperty(name) method.
 * <pre>
 * DynaBean myBean = new LazyDynaBean();
 * myBean.set("myMappedProperty", "myKey", "myValue");
 * </pre>
 * If the mapped property does exist in the DynaClass but is set to null in the
 * LazyDynaBean, then it will instantiate a new Map as specified by the
 * property's type in the DynaClass.
 * <pre>
 * DynaBean myBean = new LazyDynaBean();
 * MutableDynaClass myClass = (MutableDynaClass)myBean.getDynaClass();
 * myClass.add("myMappedProperty", TreeMap.class);
 * myBean.set("myMappedProperty", "myKey", "myValue");
 * </pre>
 * ### Restricted DynaClass
 * MutableDynaClass have a facility to restrict the DynaClass so that its
 * properties cannot be modified. If the MutableDynaClass is restricted then
 * calling any of the set() methods for a property which doesn't exist will
 * result in a IllegalArgumentException being thrown.
 */
public class LazyDyanaBean extends DefaultDynaBean {

  public LazyDyanaBean() {
    this(LazyDyanaBean.class.getSimpleName());
  }

  public LazyDyanaBean(String name) {
    super(name, true);
  }
}
