package org.apache.struts.tiles.taglib;

import static java.util.Objects.requireNonNull;

import jakarta.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.tiles.config.TilesDefinition;
import org.springframework.lang.Nullable;
import springing.struts1.taglib.JspVariableReference;

/**
 * Create a tile/component/template definition as a bean. Newly created bean
 * will be saved under specified "id", in the requested "scope". Definition tag
 * has same syntax as `insert`.
 */
public class DefinitionTag extends TagSupport {

  public DefinitionTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    ref = JspVariableReference.create();
    inherits = null;
  }

  @Override
  public int doStartTag() throws JspException {
    var name = ref.getName();
    if (name == null) throw new IllegalArgumentException(
      "An [id] attribute is required for the <tiles:definition> tag."
    );
    var tilesDefinition = getTilesDefinition(name);
    ServletActionContext.current().setTilesDefinition(tilesDefinition);
    ref.assign(requireNonNull(pageContext), tilesDefinition);
    return EVAL_BODY_INCLUDE;
  }

  private TilesDefinition getTilesDefinition(String name) {
    var definition = new TilesDefinition(name, inherits);
    if (template != null) {
      definition.setPath(template);
    }
    return definition;
  }

  private JspVariableReference ref;
  private @Nullable String template;
  private @Nullable String inherits;

  /**
   * [Required] Specifies the name under which the newly created definition
   * bean will be saved.
   */
  public void setId(String id) {
    ref.setName(id);
  }

  /**
   * Specifies the variable scope into which the newly defined bean will be
   * created. If not specified, the bean will be created in page scope.
   */
  public void setScope(String scope) {
    ref.setScope(scope);
  }

  /**
   * A string representing the URI of a tile/component/template (a JSP page).
   */
  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * URL of the template / component to insert. Same as "template".
   */
  public void setPage(String page) {
    template = page;
  }

  /**
   * Role to check before inserting this definition. If role is not defined
   * for current user, definition is not inserted. Checking is done at insert
   * time, not during definition process.
   */
  public void setRole(String role) {
    throw new UnsupportedOperationException();
  }

  /**
   * Name of a parent definition that is used to initialize this new definition.
   * Parent definition is searched in definitions factory.
   */
  public void setExtends(String inherits) {
    this.inherits = inherits;
  }
}
