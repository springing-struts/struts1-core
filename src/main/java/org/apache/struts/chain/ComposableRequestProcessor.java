package org.apache.struts.chain;

import org.apache.struts.action.RequestProcessor;

/**
 * `ComposableRequestProcessor` uses the Chain Of Responsibility design pattern
 * (as implemented by the commons-chain package in Jakarta Commons) to support
 * external configuration of command chains to be used. It is configured via
 * the following context initialization parameters:
 * - **[org.apache.struts.chain.CATALOG_NAME]**
 *   Name of the Catalog in which we will look up the Command to be executed
 *   for each request. If not specified, the default value is struts.
 * - **org.apache.struts.chain.COMMAND_NAME**
 *   Name of the Command which we will execute for each request, to be looked
 *   up in the specified Catalog. If not specified, the default value is
 *   servlet-standard.
 */
public class ComposableRequestProcessor extends RequestProcessor {
}
