package org.apache.struts.action;

/**
 * A class that encapsulates the error messages being reported by the
 * `validate()` method of an `ActionForm`. Validation errors are either global
 * to the entire `ActionForm` bean they are associated with, or they are
 * specific to a particular bean property (and, therefore, a particular input
 * field on the corresponding form).
 * Each individual error is described by an `ActionMessage` object, which
 * contains a message key (to be looked up in an appropriate message resources
 * database), and up to four placeholder arguments used for parametric
 * substitution in the resulting message.
 * **IMPLEMENTATION NOTE**
 * It is assumed that these objects are created and manipulated only within the
 * context of a single thread.
 * Therefore, no synchronization is required for access to internal
 * collections.
 */
public class ActionErrors extends ActionMessages {}
