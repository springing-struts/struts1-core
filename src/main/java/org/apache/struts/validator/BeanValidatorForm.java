package org.apache.struts.validator;

import jakarta.annotation.Nullable;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.WrapDynaBean;

/**
 * Struts validator `ActionForm` backed by either a `DynaBean` or POJO JavaBean.
 * Passing a POJO JavaBean to the constructor will automatically create an
 * associated `WrapDynaBean`. One use for this would be to migrate view objects
 * from an existing system which, for the usual reasons, can't be changed to
 * extend ActionForm.
 * This form is based on the standard struts `ValidatorForm` for use with the
 * Validator framework and validates either using the `name` from the Struts
 * `ActionMapping` or the `ActionMapping`'s path depending on whether
 * `pathValidation` is `true` or `false`.
 * **Note**:
 *  `WrapDynaBean` is NOT serializable. If you use this class with a
 *  `WrapDynaBean` (as described above), you should not store your form in
 *  session scope.
 */
public class BeanValidatorForm extends ValidatorForm implements DynaBean {

  public BeanValidatorForm(Object bean) {
    if (bean instanceof DynaBean dynaBean) {
      this.bean = dynaBean;
    }
    else {
      this.bean = new WrapDynaBean(bean);
    }
  }

  private final DynaBean bean;

  @Override
  public boolean contains(String name, String key) {
    return bean.contains(name, key);
  }

  @Nullable
  @Override
  public Object get(String name) {
    return bean.get(name);
  }

  @Nullable
  @Override
  public Object get(String name, int index) {
    return bean.get(name, index);
  }

  @Nullable
  @Override
  public Object get(String name, String key) {
    return bean.get(name, key);
  }

  @Override
  public void remove(String name, String key) {
    bean.remove(name, key);
  }

  @Override
  public void set(String name, Object value) {
    bean.set(name, value);
  }

  @Override
  public void set(String name, int index, Object value) {
    bean.set(name, index, value);
  }

  @Override
  public void set(String name, String key, Object value) {
    bean.set(name, key, value);
  }
}
