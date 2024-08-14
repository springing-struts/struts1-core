package org.apache.struts.action;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MapBackedDynaBean;
import org.apache.struts.config.FormBeanConfig;

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
  public String getName() {
    return config.getName();
  }

  @Override
  public Map<String, DynaProperty<?>> getPropertiesByPropertyName() {
    return config.getFormPropertiesByName();
  }

  @Override
  public Map<String, Object> getValuesByPropertyName() {
    return values;
  }
}
