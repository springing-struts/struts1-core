package org.apache.commons.beanutils;

import org.springframework.lang.Nullable;

/**
 * A DynaClass is a simulation of the functionality of java.lang.Class for
 * classes implementing the DynaBean interface. DynaBean instances that share
 * the same DynaClass all have the same set of available properties, along with
 * any associated data types, read-only states, and write-only states.
 */
public interface DynaClass {
  /**
   * Returns the name of this DynaClass (analogous to the getName() method of
   * `java.lang.Class`).
   */
  String getName();

  /**
   * Returns an array of PropertyDescriptors for the properties currently
   * defined in this DynaClass.
   */
  DynaProperty[] getDynaProperties();

  /**
   * Returns a property descriptor for the specified property, if it exists;
   * otherwise, return null.
   */
  @Nullable
  DynaProperty getDynaProperty(String name);

  /**
   * Instantiate and return a new DynaBean instance, associated with this
   * DynaClass.
   */
  DynaBean newInstance();
}
