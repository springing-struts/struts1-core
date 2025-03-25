package org.apache.struts.tiles.taglib;

import static java.lang.String.format;

import jakarta.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.tiles.config.TilesDefinition;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableReference;

/**
 * Insert a tiles/component/template with the possibility to pass parameters
 * (called attribute). A tile can be seen as a procedure that can take
 * parameters or attributes. `tiles:insert` allows to define these attributes
 * and pass them to the inserted jsp page, called template. Attributes are
 * defined using nested tag `tiles:put` or `tiles:putList`
 * You must specify one of this tag attribute:
 * - template:
 *   for inserting a tiles/component/template page,
 * - component:
 *   for inserting a tiles/component/template page, (same as template)
 * - page:
 *   for inserting a JSP page, (same as template)
 * - definition:
 *   for inserting a definition from definitions factory
 * - attribute:
 *   surrounding tiles' attribute name whose value is used. If attribute is
 *   associated to 'direct' flag (see put), and flag is true, write attribute
 *   value (no insertion).
 * - name:
 *   to let 'insert' determine the type of entities to insert. In this later
 *   case, search is done in this order:
 *     definitions, tiles/components/templates, pages.
 * In fact, Page, component and template, are equivalent as a tile, component
 * or template are jsp page.
 * Example:
 * <pre>{@code
 * <tiles:insert page="/basic/myLayout.jsp" flush="true">
 *   <tiles:put name="title" value="My first page" />
 *   <tiles:put name="header" value="/common/header.jsp" />
 *   <tiles:put name="footer" value="/common/footer.jsp" />
 *   <tiles:put name="menu" value="/basic/menu.jsp" />
 *   <tiles:put name="body" value="/basic/helloBody.jsp" />
 * </tiles:insert>
 * }</pre>
 */
public class InsertTag extends ImportSupport {

  public InsertTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    tilesTemplatePath = null;
    tilesDefinitionName = null;
    attribute = null;
    ref = JspVariableReference.create();
    flush = false;
    ignore = false;
  }

  private @Nullable String tilesTemplatePath;
  private @Nullable String tilesDefinitionName;
  private @Nullable String attribute;
  private JspVariableReference ref;
  private boolean flush;
  private boolean ignore;

  @Override
  public int doStartTag() throws JspException {
    url = (attribute != null)
      ? getUrlFromTilesAttribute()
      : getUrlFromTilesDefinition();
    if (url != null) {
      return super.doStartTag();
    }
    if (ignore) {
      return SKIP_BODY;
    }
    throw new JspException(
      format(
        "Failed to determine the resource url to include." +
        " attribute:[%s], template/component/page:[%s], definition:[%s], beanName:[%s].",
        attribute,
        tilesTemplatePath,
        tilesDefinitionName,
        ref.getName()
      )
    );
  }

  private @Nullable String getUrlFromTilesDefinition() {
    var tilesDefinition = prepareTilesDefinition();
    if (tilesDefinition == null) {
      return null;
    }
    return tilesDefinition.getPath();
  }

  private @Nullable String getUrlFromTilesAttribute() {
    var tilesDefinition = ServletActionContext.current().getTilesDefinition();
    var tilesAttribute = tilesDefinition.getAttributes().get(attribute);
    return tilesAttribute == null ? null : tilesAttribute.getValue();
  }

  private @Nullable TilesDefinition prepareTilesDefinition() {
    var context = ServletActionContext.current();
    if (tilesDefinitionName != null) {
      context.setTilesDefinition(tilesDefinitionName);
      return context.getTilesDefinition();
    }
    if (tilesTemplatePath != null) {
      var definition = new TilesDefinition(tilesTemplatePath, null);
      definition.setPath(tilesTemplatePath);
      context.setTilesDefinition(definition);
      return definition;
    }
    if (ref.getName() != null) {
      var definition = (TilesDefinition) ref.resolve(getPageContext());
      if (definition == null) return null;
      context.setTilesDefinition(definition);
      return definition;
    }
    throw new IllegalArgumentException(
      "A <tiles:insert> tag requires one, and only one, of the following attributes:" +
      " attribute, template, component, page, definition, or name."
    );
  }

  private PageContext getPageContext() {
    return PageContext.toJavaxNamespace(pageContext);
  }

  @Override
  public int doEndTag() throws JspException {
    if (url == null && ignore) {
      return EVAL_PAGE;
    }
    return super.doEndTag();
  }

  /**
   * A string representing the URI of a tile or template (a JSP page).
   * 'page', 'component' and 'template' are synonyms: they have exactly the
   * same behavior.
   */
  public void setTemplate(String template) {
    tilesTemplatePath = template;
  }

  /**
   * Path (relative or absolute to webapps) of the component to insert.
   * 'page', 'component' and 'template' are synonyms: they have exactly the
   * same behavior.
   */
  public void setComponent(String component) {
    tilesTemplatePath = component;
  }

  /**
   * Path (relative or absolute to webapps) of the page to insert.
   * 'page', 'component' and 'template' are synonyms: they have exactly the
   * same behavior.
   */
  public void setPage(String page) {
    tilesTemplatePath = page;
  }

  /**
   * Name of the definition to insert. Definition are defined in a centralized
   * file. For now, only definition from factory can be inserted with this
   * attribute. To insert a definition defined with tag `tiles:definition`,
   * use `beanName=""`.
   */
  public void setDefinition(String definitionName) {
    tilesDefinitionName = definitionName;
  }

  /**
   * Name of an attribute in current tile/component context. Value of this
   * attribute is passed to 'name' (see attribute 'name').
   */
  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  /**
   * Name of an entity to insert. Search is done in this order:
   * definition, attribute, [tile/component/template/page].
   */
  public void setName(String name) {
    throw new UnsupportedOperationException();
  }

  /**
   * Name of the bean used as value. Bean is retrieved from specified context,
   * if any. Otherwise, method pageContext.findAttribute is used.
   * If beanProperty is also specified, retrieve value from the corresponding
   * bean property.
   * If found bean (or property value) is instance of one of Attribute class
   * (Direct, Instance, ...), insertion is done according to the class type.
   * Otherwise, the toString method is called on the bean, and returned String
   * is used as name to insert (see 'name' attribute).
   */
  public void setBeanName(String beanName) {
    ref.setName(beanName);
  }

  /**
   * Bean property name. If specified, value is retrieve from this property.
   * Support nested/indexed properties.
   */
  public void setBeanProperty(String beanProperty) {
    ref.setProperty(beanProperty);
  }

  /**
   * Scope into which bean is searched. If not specified, method
   * `pageContext.findAttribute()` is used. Scope can be any JSP scope,
   * 'component', or 'template'. In these two later cases, bean is search in
   * tile/component/template context.
   */
  public void setBeanScope(String beanScope) {
    ref.setScope(beanScope);
  }

  /**
   * True or false. If true, current page out stream is flushed before
   * insertion.
   */
  public void setFlush(boolean flush) {
    this.flush = flush;
  }

  /**
   * If this attribute is set to true, and the attribute specified by the name
   * does not exist, simply return without writing anything. The default value
   * is false, which will cause a runtime exception to be thrown.
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * If the user is in the specified role, the tag is taken into account;
   * otherwise, the tag is ignored (skipped).
   */
  public void setRole(String role) {
    throw new UnsupportedOperationException();
  }

  /**
   * Url of a controller called immediately before page is inserted. Url
   * usually denote a Struts action. Controller (action) is used to prepare
   * data to be rendered by inserted Tile.
   * See also controllerClass. Only one of controllerUrl or controllerClass
   * should be used.
   */
  public void setControllerUrl(String controllerUrl) {
    throw new UnsupportedOperationException();
  }

  /**
   * Class of a controller called immediately before page is inserted.
   * Controller is used to prepare data to be rendered by inserted Tile.
   * See also `controllerUrl`
   * Class must implement or extend one of the following:
   * - `org.apache.struts.tiles.Controller`
   * - `org.apache.struts.tiles.ControllerSupport`
   * - `org.apache.struts.action.Action`
   *    (wrapper `org.apache.struts.action.ActionController` is used)
   * See also controllerUrl. Only one of controllerUrl or controllerClass
   * should be used.
   */
  public void setControllerClass(String controllerClass) {
    throw new UnsupportedOperationException();
  }
}
