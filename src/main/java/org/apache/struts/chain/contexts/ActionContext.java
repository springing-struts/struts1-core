package org.apache.struts.chain.contexts;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.springframework.lang.Nullable;

public interface ActionContext {
  /**
   * Get the action which has been identified to be executed as part of
   * processing this request.
   */
  Action getAction();

  /**
   *  Set the action which has been identified to be executed as part of
   *  processing this request.
   */
  void setAction(Action action);

  /**
   * Get the ActionForm instance which will carry any data submitted as part of
   * this request.
   */
  @Nullable ActionForm getActionForm();

  /**
   * Set the ActionForm instance which will carry any data submitted as part of
   * this request.
   */
  void setActionForm(ActionForm actionForm);

  /**
   * Get the ActionConfig which contains the details for processing this
   * request.
   */
  ActionConfig getActionConfig();

  /**
   * Set the ActionConfig class contains the details for processing this
   * request.
   */
  void setActionConfig(ActionConfig actionConfig);

  /**
   * Get the ForwardConfig which has been identified as the basis for
   * view-processing.
   */
  @Nullable ForwardConfig getForwardConfig();

  /**
   * Set the ForwardConfig which should be used as the basis of the view
   * segment of the overall processing. This is the primary method of
   * "communication" with the "view" sub-chain.
   */
  void setForwardConfig(ForwardConfig forwardConfig);
}
