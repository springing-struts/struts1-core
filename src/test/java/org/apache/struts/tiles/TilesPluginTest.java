package org.apache.struts.tiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;

import org.apache.struts.TestApp;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.ModuleUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import springing.struts1.configuration.ServletConfigBean;

@WebMvcTest
public class TilesPluginTest {

  @Autowired
  private TestApp app;

  /**
   * <pre>
   *   <definition name=".base"         page="/layout.faces">
   *     <put      name="header"       value="/header.jsp"/>
   *     <put      name="footer"       value="/footer.jsp"/>
   *     <put      name="menu"         value="/blank.jsp"/>
   *     <put      name="body"         value="/blank.jsp"/>
   *   </definition>
   *
   *   <!-- Logged Off Layout -->
   *   <definition name=".loggedoff" extends=".base">
   *     <put      name="menu"         value="/loggedoff.jsp"/>
   *   </definition>
   *
   *   <!-- Logged On Layout -->
   *   <definition name=".loggedon"  extends=".base">
   *     <put      name="menu"         value="/loggedon.jsp"/>
   *   </definition>
   * </pre>
   */
  @Test
  void testItCanBeLoadedFromStrutsConfig() {
    var request = app.createRequest(GET, "/example2/welcome");
    var module = ModuleUtils.getCurrent();
    module.loadPlugins(
      new ActionServlet(
        new ServletConfigBean("action", ActionServlet.class.getName())
      )
    );
    var tilesPlugin = module.getPlugInByType(TilesPlugin.class);
    assertThat(tilesPlugin).isNotNull();
    var tilesDefinitions = tilesPlugin.getTilesDefinitions();
    var logonTiles = tilesDefinitions.getTilesDefinitionByName(".loggedon");
    assertThat(logonTiles).isNotNull();
    assertThat(logonTiles.getPath()).isEqualTo("/layout.faces");
    var attrs = logonTiles.getAttributes();
    assertThat(attrs.get("menu").getValue()).isEqualTo("/loggedon.jsp");
    assertThat(attrs.get("header").getValue()).isEqualTo("/header.jsp");
  }
}
