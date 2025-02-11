package org.apache.struts.taglib.nested;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.NestedPathTag;
import springing.struts1.taglib.DelegatingTagBase;

/**
 * Defines a new level of nesting for child tags to reference to.
 * This tag provides a simple method of defining a logical nesting level in the
 * nested hierarchy. It runs no explicit logic, is simply a placeholder. It
 * also means you can remove the need for explicit setting of level properties
 * in child tags.
 * Just as the iterate tag provide a parent to other tags, this does the same
 * but there is no logic for iterating or otherwise.
 * ### Example...
 * <pre>{@code
 *   <nested:write property="myNestedLevel.propertyOne" />
 *   <nested:write property="myNestedLevel.propertyTwo" />
 *   <nested:write property="myNestedLevel.propertyThree" />
 * }</pre>
 * Can instead become...
 * <pre>{@code
 *   <nested:nest property="myNestedLevel">
 *     <nested:write property="propertyOne" />
 *     <nested:write property="propertyTwo" />
 *     <nested:write property="propertyThree" />
 *   </nested:nest>
 * }</pre>
 */
public class NestedPropertyTag extends DelegatingTagBase<NestedPathTag> {

  public NestedPropertyTag(NestedPathTag tag) {
    super(new NestedPathTag());
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    property = null;
  }

  private @Nullable String property;

  /**
   * This specifies the property by which this tag and all child tags will be
   * relative to.
   */
  public void setProperty(String property) {
    this.property = property;
  }
}
