package org.apache.commons.beanutils;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * A DynaClass is a simulation of the functionality of java.lang.Class for
 * classes implementing the DynaBean interface. DynaBean instances that share
 * the same DynaClass all have the same set of available properties, along with
 * any associated data types, read-only states, and write-only states.
 */
public interface DynaClass {

  /**
   * Returns the name of this DynaClass (analogous to the getName() method of
   * `java.lang.ClassDynaClass` implementation class to support different
   * dynamic classes, with different sets of properties.
   */
  String getName();

  Map<String, DynaProperty<?>> getDynaPropertiesByName();

  /**
   * Returns an array of PropertyDescriptors for the properties currently
   * defined in this DynaClass.
   */
  default DynaProperty<?>[] getDynaProperties() {
    return getDynaPropertiesByName().values().toArray(new DynaProperty[]{});
  }

  /**
   * Returns a property descriptor for the specified property, if it exists;
   * otherwise, return null.
   */
  default @Nullable DynaProperty<?> getDynaProperty(String name) {
    return getDynaPropertiesByName().get(name);
  }
}
