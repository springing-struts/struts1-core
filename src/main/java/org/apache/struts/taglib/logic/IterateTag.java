package org.apache.struts.taglib.logic;

import jakarta.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.core.ForEachSupport;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableAware;

import java.util.regex.Pattern;

import static springing.util.ObjectUtils.asIterator;
import static springing.util.ServletRequestUtils.getAttributeFromScope;

/**
 * Repeat the nested body content of this tag over a specified collection.
 * Repeats the nested body content of this tag once for every element of the
 * specified collection, which must be an `Iterator` a `Collection`, a `Map`
 * (whose values are to be iterated over), or an array. The collection to be
 * iterated over must be specified in one of the following ways:
 * - As a runtime expression specified as the value of the `collection`
 *   attribute.
 * - As a JSP bean specified by the `name` attribute.
 * - As the property, specified by the `property`, of the JSP bean specified by
 *   the `name` attribute.
 * The collection to be iterated over MUST conform to one of the following
 * requirements in order for iteration to be successful:
 * - An array of Java objects or primitives.
 * - An implementation of `java.util.Collection`, including `ArrayList` and
 *   `Vector`.
 * - An implementation of `java.util.Enumeration`.
 * - An implementation of `java.util.Iterator`.
 * - An implementation of `java.util.Map`, including `HashMap`, `Hashtable`,
 *   and `TreeMap`. **NOTE** See below for additional information about
 *   accessing Maps.
 * Normally, each object exposed by the iterate tag is an element of the
 * underlying collection you are iterating over. However, if you iterate over
 * a `Map`, the exposed object is of type `Map.Entry` that has two properties:
 * - `key`: The key under which this item is stored in the underlying Map.
 * - `value`: The value that corresponds to this key.
 * So, if you wish to iterate over the values of a Hashtable, you would
 * implement code like the following:
 * <pre>
 *  <logic:iterate id="element" name="myhashtable">
 *    Next element is <bean:write name="element" property="value"/>
 *  </logic:iterate>
 * </pre>
 * If the collection you are iterating over can contain `null` values, the loop
 * will still be performed but no page scope attribute (named by the `id`
 * attribute) will be created for that loop iteration. You can use the
 * `logic:present` and `logic:notPresent` tags to test for this case.
 */
public class IterateTag extends ForEachSupport implements JspVariableAware {

  public IterateTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  protected void init() {
    reference = JspVariableReference.create();
    collection = null;
    id = null;
    indexId = null;
    length = null;
    offset = null;
    type = null;
  }
  private JspVariableReference reference;
  private @Nullable Object collection;
  private @Nullable String id;
  private @Nullable String indexId;
  private @Nullable String length;
  private @Nullable String offset;
  private @Nullable String type;

  @Override
  public JspVariableReference getReference() {
    return reference;
  }

  @Override
  protected void prepare() throws JspTagException {
    var value = collection != null ? collection : getValue(pageContext);
    var offset = getOffsetAsInt();
    var iterator = asIterator(value, getLengthAsInt(), offset);
    items = new ForEachIterator() {
      private int index = offset == null ? 0 : offset;
      @Override
      public boolean hasNext() throws JspTagException {
        return iterator.hasNext();
      }

      @Override
      public Object next() throws JspTagException {
        var next = iterator.next();
        if (id != null) {
          pageContext.setAttribute(id, next);
        }
        if (indexId != null) {
          pageContext.setAttribute(indexId, index);
        }
        index++;
        return next;
      }
    };
  }

  /**
   * A runtime expression that evaluates to a collection (conforming to the
   * requirements listed above) to be iterated over.
   */
  public void setCollection(Object collection) {
    this.collection = collection;
  }

  /**
   * The name of a page scope JSP bean that will contain the current element
   * of the collection on each iteration, if it is not `null`.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * The name of a page scope JSP bean that will contain the current index of
   * the collection on each iteration.
   */
  public void setIndexId(String indexId) {
    this.indexId = indexId;
  }

  /**
   * The maximum number of entries (from the underlying collection) to be
   * iterated through on this page. This can be either an integer that directly
   * expresses the desired value, or the name of a JSP bean (in any scope) of
   * type `java.lang.Integer` that defines the desired value. If not present,
   * there will be no limit on the number of iterations performed.
   */
  public void setLength(String length) {
    this.length = length;
  }

  private @Nullable Integer getLengthAsInt() {
    return getInt(length);
  }

  /**
   * The zero-relative index of the starting point at which entries from the
   * underlying collection will be iterated through. This can be either an
   * integer that directly expresses the desired value, or the name of a JSP
   * bean (in any scope) of type `java.lang.Integer` that defines the desired
   * value. If not present, zero is assumed (meaning that the collection will
   * be iterated from the beginning.
   */
  public void setOffset(String offset) {
    this.offset = offset;
  }

  private @Nullable Integer getOffsetAsInt() {
    return getInt(offset);
  }

  private @Nullable Integer getInt(@Nullable String value) {
    if (value == null) {
      return null;
    }
    var isLiteral = INTEGER_STRING.matcher(value).matches();
    if (isLiteral) {
      return Integer.parseInt(value);
    }
    return (Integer) getAttributeFromScope(pageContext, length);
  }

  private static final Pattern INTEGER_STRING = Pattern.compile(
      "^(0|[+-]?[1-9][0-9]*)$"
  );

  /**
   * Fully qualified Java class name of the element to be exposed through the
   * JSP bean named from the `id` attribute. If not present, no type
   * conversions will be performed. **NOTE:** The actual elements of the
   * collection must be assignment-compatible with this class, or a request
   * time ClassCastException will occur.
   */
  public void setType(String type) {
    this.type = type;
  }
}
