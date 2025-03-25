package springing.struts1.validator;

import java.lang.annotation.Annotation;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.springframework.beans.*;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

public class DynaBeanPropertyAccessor extends AbstractNestablePropertyAccessor {

  public DynaBeanPropertyAccessor(DynaBean bean) {
    super(bean);
    this.bean = bean;
  }

  public DynaBeanPropertyAccessor(
    DynaBean bean,
    String nestedPath,
    AbstractPropertyAccessor parent
  ) {
    super(bean, nestedPath, parent);
    this.bean = bean;
  }

  private final DynaBean bean;

  @Override
  protected @Nullable PropertyHandler getLocalPropertyHandler(
    String propertyName
  ) {
    var prop = getDynaPropertyOf(propertyName);
    if (prop == null) {
      return null;
    }
    return new DyanaBeanPropertyHandler(bean, propertyName);
  }

  @Override
  protected AbstractNestablePropertyAccessor newNestedPropertyAccessor(
    Object object,
    String nestedPath
  ) {
    if (object instanceof DynaBean dynaBean) {
      return new DynaBeanPropertyAccessor(dynaBean, nestedPath, this);
    }
    throw new IllegalArgumentException(
      "Not a DynaBean: " + object.getClass().getName()
    );
  }

  @Override
  protected NotWritablePropertyException createNotWritablePropertyException(
    String propertyName
  ) {
    return null;
  }

  private boolean hasDynaPropertyOf(String propertyName) {
    return getDynaPropertyOf(propertyName) != null;
  }

  private @Nullable DynaProperty getDynaPropertyOf(String propertyName) {
    return bean.getDynaClass().getDynaProperty(propertyName);
  }

  private DynaProperty shouldGetDynaPropertyOf(String propertyName) {
    var prop = getDynaPropertyOf(propertyName);
    if (prop == null) throw new InvalidPropertyException(
      bean.getClass(),
      propertyName,
      "Unknown property name."
    );
    return prop;
  }

  private static class DyanaBeanPropertyHandler extends PropertyHandler {

    public DyanaBeanPropertyHandler(DynaBean bean, String propertyName) {
      super(getProperty(bean, propertyName).getType(), true, true);
      this.bean = bean;
      property = getProperty(bean, propertyName);
      resolvableType = ResolvableType.forClass(property.getType());
    }

    private static DynaProperty getProperty(
      DynaBean bean,
      String propertyName
    ) {
      var property = bean.getDynaClass().getDynaProperty(propertyName);
      if (property == null) throw new IllegalArgumentException(
        "Unknown property name: " + propertyName
      );
      return property;
    }

    private final DynaBean bean;
    private final DynaProperty property;
    private final ResolvableType resolvableType;

    @Override
    public TypeDescriptor toTypeDescriptor() {
      return new TypeDescriptor(
        resolvableType,
        property.getType(),
        new Annotation[] {}
      );
    }

    @Override
    public ResolvableType getResolvableType() {
      return resolvableType;
    }

    @Override
    public TypeDescriptor nested(int level) {
      //TODO
      return null;
    }

    @Override
    public Object getValue() throws Exception {
      return bean.get(property.getName());
    }

    @Override
    public void setValue(@Nullable Object value) throws Exception {
      bean.set(property.getName(), value);
    }
  }
}
