package org.apache.struts.action;

import static springing.util.ObjectUtils.createInstanceOf;

import java.util.HashMap;
import java.util.List;
import org.apache.commons.beanutils.DefaultDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.config.FormBeanConfig;

/**
 * Implementation of `DynaClass` for `DynaActionForm` classes that allow
 * developers to define ActionForms without having to individually code all the
 * classes.
 * **NOTE**
 *   This class is only used in the internal implementation of dynamic action
 *   form beans. Application developers never need to consult this
 *   documentation.
 */
public class DynaActionFormClass extends DefaultDynaClass {

  /**
   * Return the `DynaActionFormClass` instance for the specified form bean
   * configuration instance.
   */
  public static DynaActionFormClass createDynaActionFormClass(
    FormBeanConfig config
  ) {
    return new DynaActionFormClass(config);
  }

  /**
   * Construct a new `DynaActionFormClass` for the specified form bean
   * configuration. This constructor is private; `DynaActionFormClass`
   * instances will be created as needed via calls to the static
   * `createDynaActionFormClass()` method.
   */
  public DynaActionFormClass(FormBeanConfig config) {
    super(
      config.getName(),
      config
        .getFormProperties()
        .stream()
        .map(it -> new DynaProperty(it.getName(), it.getType()))
        .toArray(DynaProperty[]::new)
    );
    this.config = config;
    if (!config.getDynamic()) throw new IllegalArgumentException(
      String.format(
        "Failed to create a dynabean instance as the type [%s] of the form bean config [%s] is not a DynaBean class.",
        config.getFormClass().getName(),
        getName()
      )
    );
  }

  private final FormBeanConfig config;

  /**
   * Instantiate and return a new `DynaActionForm` instance, associated with
   * this `DynaActionFormClass`. The properties of the returned
   * `DynaActionForm` will have been initialized to the default values
   * specified in the form bean configuration information.
   */
  public DynaBean newInstance() {
    var type = config.getFormClass();
    if (!DynaBean.class.isAssignableFrom(type)) throw new IllegalStateException(
      String.format(
        "The type [%s] of the form [%s] does not inherit DynaBean.",
        type.getName(),
        getName()
      )
    );
    var initialProps = new HashMap<String, Object>();
    for (var prop : config.getFormProperties()) {
      initialProps.put(prop.getName(), prop.getInitial());
    }
    return (DynaBean) createInstanceOf(
      type,
      List.of(FormBeanConfig.class),
      List.of(config),
      initialProps
    );
  }
}
