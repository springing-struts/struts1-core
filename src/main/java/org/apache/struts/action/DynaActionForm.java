package org.apache.struts.action;

import org.apache.commons.beanutils.DefaultDynaClass;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MapBackedDynaBean;
import org.apache.struts.config.FormBeanConfig;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Specialized subclass of `ActionForm` that allows the creation of form beans
 * with dynamic sets of properties, without requiring the developer to create a
 * Java class for each type of form bean.
 * **USAGE NOTE**
 * Since Struts 1.1, the reset method no longer initializes property values to
 * those specified in `form-property` elements in the Struts module
 * configuration file. If you wish to utilize that behavior, the simplest
 * solution is to subclass `DynaActionForm` and call the initialize method
 * inside it.
 */
public class DynaActionForm extends ActionForm implements MapBackedDynaBean {

  /**
   * Initializes the specified form bean.
   */
  public DynaActionForm(FormBeanConfig formBeanConfig) {
    this.config = formBeanConfig;
  }

  private final FormBeanConfig config;
  private final ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();

  @Override
  public DynaClass getDynaClass() {
    if (dynaClass == null) {
      var properties = new ArrayList<DynaProperty>();
      for (var p : config.getFormProperties()) {
        properties.add(new DynaProperty(p.getName(), p.getType()));
      }
      dynaClass = new DefaultDynaClass(
        config.getName(), properties.toArray(new DynaProperty[]{})
      );
    }
    return dynaClass;
  }
  private @Nullable DynaClass dynaClass;

  @Override
  public Map<String, Object> getValues() {
    return values;
  }
}
