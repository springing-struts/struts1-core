package org.apache.struts.webapp.examples;


import org.apache.struts.action.*;


/**
 * Custom ActionMapping to demonstrate usage.
 *
 * @version $Rev$ $Date$
 */

public final class CustomActionMapping extends ActionMapping {


  // --------------------------------------------------- Instance Variables


  /**
   * An example String property.
   */
  private String example = "";


  // ----------------------------------------------------------- Properties


  /**
   * Return the example String.
   */
  public String getExample() {

    return (this.example);

  }


  /**
   * Set the example String.
   *
   * @param example The new example String.
   */
  public void setExample(String example) {

    this.example = example;

  }

}
