package springing.struts1.validator;

import org.apache.commons.beanutils.DynaBean;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.DataBinder;

public class StrutsDataBinder extends DataBinder {

  public StrutsDataBinder(Object target) {
    super(target);
  }

  public StrutsDataBinder(Object target, String objectName) {
    super(target, objectName);
  }

  @Override
  protected AbstractPropertyBindingResult createBeanPropertyBindingResult() {
    if (getTarget() instanceof DynaBean dynaBean) {
      return new DynaBeanBindingResult(dynaBean, getObjectName());
    }
    return super.createBeanPropertyBindingResult();
  }

  @Override
  protected ConfigurablePropertyAccessor getPropertyAccessor() {
    if (getTarget() instanceof DynaBean dynaBean) {
      return new DynaBeanPropertyAccessor(dynaBean);
    }
    return super.getPropertyAccessor();
  }
}
