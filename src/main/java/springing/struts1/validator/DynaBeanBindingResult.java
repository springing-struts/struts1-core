package springing.struts1.validator;

import org.apache.commons.beanutils.DynaBean;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.validation.AbstractPropertyBindingResult;

public class DynaBeanBindingResult extends AbstractPropertyBindingResult {

  public DynaBeanBindingResult(DynaBean bean, String name) {
    super(name);
    this.bean = bean;
  }
  private final DynaBean bean;

  @Override
  public Object getTarget() {
    return bean;
  }

  @Override
  protected Object getActualFieldValue(String field) {
    return bean.get(field);
  }

  @Override
  public ConfigurablePropertyAccessor getPropertyAccessor() {
    return new DynaBeanPropertyAccessor(bean);
  }
}
