package org.apache.struts.validator;

/**
 * Struts "Lazy" ActionForm which wraps a `LazyDynaBean`.
 *  There isn't really that much to this implementation as most of the "lazy"
 * behaviour is in `LazyDynaBean` and wrapping the `LazyDynaBean` is handled in
 * the parent `BeanValidatorForm`. The only thing it really does is populate
 * indexed properties which are a `List` type with a `LazyDynaBean` in the
 * `get(name, index)` method.
 *  Lazy DynaBeans provide several types of lazy behaviour:
 *  - **lazy property addition**
 *      properties which do not exist are automatically added.
 *  - **lazy List facilities**
 *      automatically grows a `List` or `Array` to accommodate the index value
 *      being set.
 * - **lazy List creation**
 *     automatic creation of a `List` or `Array` for ndexed properties, if it
 *     doesn't exist.
 * - **lazy Map creation**
 *     automatic* creation of a `Map` for mapped properties, if it doesn't
 *     exist.
 *  Using this lazy `ActionForm` means that you don't have to define the
 * ActionForm's properties in the `struts-config.xml`. However, a word of
 * warning, everything in the Request gets populated into this `ActionForm`
 * circumventing the normal firewall function of Struts forms. Therefore, you
 * should only take out of this form properties you expect to be there rather
 * than blindly populating all the properties into the business tier.
 *  Having said that it is not necessary to pre-define properties in the
 * `struts-config.xml`, it is useful to sometimes do so for mapped or indexed
 * properties. For example, if you want to use a different `Map` implementation
 * from the default `HashMap` or an array for indexed properties, rather than
 * the default `List` type:
 * <pre>
 *   <form-bean
 *     name="myForm"
 *     type="org.apache.struts.validator.LazyValidatorForm">
 *     <form-property
 *       name="myMap"
 *       type="java.util.TreeMap"
 *     />
 *     <form-property
 *       name="myBeans"
 *       type="org.apache.commons.beanutils.LazyDynaBean[]"
 *     />
 *   </form-bean>
 * </pre>
 *  Another reason for defining indexed properties in the `struts-config.xml`
 *  is that if you are validating indexed properties using the Validator and
 *  none are submitted then the indexed property will be `null` which causes
 *  validator to fail.
 *   Pre-defining them in the `struts-config.xml` will result in a zero-length
 *  indexed property (array or List) being instantiated, avoiding an issue with
 *  validator in that circumstance.
 *   This implementation validates using the ActionForm name. If you require a
 *   version that validates according to the path then it can be easily created
 *   in the following manner:
 * <pre>
 *   public class MyLazyForm extends LazyValidatorForm {
 *     public MyLazyForm () {
 *       super();
 *       setPathValidation(true);
 *     }
 *   }
 * </pre>
 *  Rather than using this class, another alternative is to either use a
 * `LazyDynaBean` or custom version of `LazyDynaBean` directly. Struts now
 * automatically wraps objects which are not `ActionForms` in a
 * `BeanValidatorForm`. For example:
 * <pre>
 *   <form-bean
 *     name="myForm"
 *     type="org.apache.commons.beanutils.LazyDynaBean">
 *     <form-property
 *       name="myBeans"
 *       type="org.apache.commons.beanutils.LazyDynaBean[]"
 *     />
 *   </form-bean>
 * </pre>
 */
public class LazyValidatorForm extends BeanValidatorForm {

  public LazyValidatorForm(Object bean) {
    //TODO
    super(bean);
  }
}
