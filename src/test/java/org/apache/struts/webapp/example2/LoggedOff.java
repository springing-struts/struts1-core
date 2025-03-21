/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.struts.webapp.example2;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Backing bean for the <code>loggedoff.jsp</code> page.</p>
 */

public class LoggedOff {


    // ------------------------------------------------------ Instance Variables


    private static final Log log = LogFactory.getLog(LoggedOff.class);


    // ----------------------------------------------------------------- Actions


    /**
     * <p>Begin the process of registering a new user.</p>
     */
    public String register() {

        /*
        FacesContext context = FacesContext.getCurrentInstance();
        if (log.isDebugEnabled()) {
            log.debug("register(" + context + ")");
        }
        forward(context, "/editRegistration.do?action=Create");
         */
        return (null);

    }


    /**
     * <p>Begin the process of logging on.</p>
     */
    public String logon() {
        /*
        FacesContext context = FacesContext.getCurrentInstance();
        if (log.isDebugEnabled()) {
            log.debug("logon(" + context + ")");
        }
        forward(context, "/editLogon.do");
         */
        return (null);

    }
}
