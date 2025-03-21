package org.apache.struts.tiles;

import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ForwardConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * `RequestProcessor` contains the processing logic that the Struts controller
 * servlet performs as it receives each servlet request from the container.
 * This processor subclasses the Struts RequestProcessor in order to intercept
 * calls to forward or include. When such calls are done, the Tiles processor
 * checks if the specified URI is a definition name. If true, the definition is
 * retrieved and included. If false, the original URI is included or a forward
 * is performed.
 * Actually, catching is done by overloading the following methods:
 * - processForwardConfig(HttpServletRequest, HttpServletResponse, ForwardConfig)
 * - internalModuleRelativeForward(String, HttpServletRequest, HttpServletResponse)
 * - internalModuleRelativeInclude(String, HttpServletRequest, HttpServletResponse)
 */
public class TilesRequestProcessor extends RequestProcessor {

  /**
   * Overloaded method from Struts' RequestProcessor.
   * Forward or redirect to the specified destination by the specified
   * mechanism. This method catches the Struts' actionForward call. It checks
   * if the actionForward is done on a Tiles definition name. If true, process
   * the definition and insert it. If false, call the original parent's method.
   */
  @Override
  protected void processForwardConfig(
    HttpServletRequest request,
    HttpServletResponse response,
    ForwardConfig forward
  ) throws IOException, ServletException {
    var forwardPath = forward.getPath();
    var processedAsTilesRequest = processTilesDefinition(forwardPath, request, response);
    if (processedAsTilesRequest) {
      return;
    }
    super.processForwardConfig(request, response, forward);
  }


  @Override
  protected void internalModuleRelativeForward(
    String forwardPath,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    var processedAsTilesRequest = processTilesDefinition(forwardPath, request, response);
    if (processedAsTilesRequest) {
      return;
    }
    super.internalModuleRelativeForward(forwardPath, request, response);
  }

  @Override
  protected void internalModuleRelativeInclude(
    String includePath,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    var processedAsTilesRequest = processTilesDefinition(includePath, request, response);
    if (processedAsTilesRequest) {
      return;
    }
    super.internalModuleRelativeInclude(includePath, request, response);
  }

  /**
   * Process a Tile definition name.
   * This method tries to process the parameter `definitionName` as
   * a definition name. It returns `true` if a definition has been processed,
   * or `false` otherwise.
   */
  protected boolean processTilesDefinition(
    String definitionName,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException, ServletException {
    var context = getActionContext();
    context.setTilesDefinition(definitionName);
    doForward(context.getTilesDefinition().getPath(), request, response);
    return true;
  }
}
