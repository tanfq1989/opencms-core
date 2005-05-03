/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/db/CmsDbContext.java,v $
 * Date   : $Date: 2005/05/03 16:28:05 $
 * Version: $Revision: 1.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2002 - 2005 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.db;

import org.opencms.file.CmsProject;
import org.opencms.file.CmsRequestContext;
import org.opencms.file.CmsUser;
import org.opencms.file.CmsVfsException;
import org.opencms.flex.CmsFlexRequestContextInfo;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.main.CmsException;
import org.opencms.main.I_CmsConstants;
import org.opencms.report.I_CmsReport;
import org.opencms.util.CmsStringUtil;

/**
 * Warps context information to access the OpenCms database.<p> 
 * 
 * @author Alexander Kandzior (a.kandzior@alkacon.com)
 * 
 * @version $Revision: 1.3 $
 * @since 5.5.4
 */
public class CmsDbContext {

    /** The current Flex request context info (if available). */
    protected CmsFlexRequestContextInfo m_flexRequestContextInfo;

    /** The wrapped user request context. */
    protected CmsRequestContext m_requestContext;

    /**
     * Creates a new, empty database context.<p> 
     */
    public CmsDbContext() {

        this(null);
    }

    /**
     * Creates a new database context initialized with the given user request context.<p>
     *  
     * @param context the current users request context
     */
    public CmsDbContext(CmsRequestContext context) {

        m_requestContext = context;

        if (m_requestContext != null) {
            m_flexRequestContextInfo = (CmsFlexRequestContextInfo)m_requestContext
                .getAttribute(I_CmsConstants.C_HEADER_LAST_MODIFIED);
        }
    }

    /**
     * Clears this database context.<p>
     */
    public void clear() {
        
        m_requestContext = null;
        m_flexRequestContextInfo = null;
    }

    /**
     * Returns the current users project.<p>
     * 
     * @return the current users project
     */
    public CmsProject currentProject() {

        return m_requestContext.currentProject();
    }

    /**
     * Returns the current user.<p>
     * 
     * @return the current user
     */
    public CmsUser currentUser() {

        return m_requestContext.currentUser();
    }

    /**
     * Returns the current Flex request context info.<p>
     * 
     * @return the current Flex request context info
     */
    public CmsFlexRequestContextInfo getFlexRequestContextInfo() {

        return m_flexRequestContextInfo;
    }

    /**
     * Returns the request context.<p>
     *
     * @return the request context
     */
    public CmsRequestContext getRequestContext() {

        return m_requestContext;
    }

    /**
     * Retruns true if the database context uses the default implementation.<p>
     * 
     * @return true if the database context uses the default implementation
     */
    public boolean isDefaultDbContext() {

        return true;
    }

    /**
     * Processes the current database context.<p>
     * 
     * @throws CmsException if something goes wrong
     */
    public void pop() throws CmsException {

        if (! isDefaultDbContext()) {
            throw new CmsException("Unable to process non-default database context implementation");
        }
    }

    /**
     * Removes the current site root prefix from the absolute path in the resource name,
     * that is adjusts the resource name for the current site root.<p> 
     * 
     * If no user request context is available, the given resource name is
     * returned unchanged.<p>
     * 
     * @param resourcename the resource name
     * 
     * @return the resource name adjusted for the current site root
     */
    public String removeSiteRoot(String resourcename) {

        if (m_requestContext != null) {
            return m_requestContext.removeSiteRoot(resourcename);
        }

        return resourcename;
    }

    /**
     * Reports an error to the given report (if available) and to the OpenCms log file.<p>
     *  
     * @param report the report to write the error to
     * @param message the message to write to the report / log
     * @param throwable the exception to write to the report / log
     * 
     * @throws CmsException the throwable parameters masked as a CmsException 
     */
    public void report(I_CmsReport report, String message, Throwable throwable) throws CmsException {

        if (report != null) {
            if (message != null) {
                report.println(message, I_CmsReport.C_FORMAT_ERROR);
            }
            if (throwable != null) {
                report.println(throwable);
            }
        }

        if (throwable != null) {
            if (throwable instanceof CmsException) {
                throw (CmsException)throwable;
            }
            if (CmsStringUtil.isEmpty(message)) {
                throw new CmsException("Exception during database operation", throwable);
            } else {
                throw new CmsException("Exception during database operation: " + message, throwable);
            }
        }
    }
    
    /**
     * Reports an error to the given report (if available) and to the OpenCms log file.<p>
     *  
     * @param report the report to write the error to
     * @param message the message to write to the report / log
     * @param throwable the exception to write to the report / log
     * 
     * @throws CmsException the throwable parameters masked as a CmsException
     * @throws CmsVfsException if the CmsMessageContainer is not null
     */
    public void report(I_CmsReport report, CmsMessageContainer message, Throwable throwable) throws CmsVfsException, CmsException {

        if (report != null) {
            if (message != null) {
                report.println(message.key(), I_CmsReport.C_FORMAT_ERROR);
            }
            if (throwable != null) {
                report.println(throwable);
            }
        }

        if (throwable != null) {
            if (throwable instanceof CmsException) {
                throw (CmsException)throwable;
            }
            if (message == null) {
                throw new CmsVfsException(Messages.get().container(Messages.ERR_DB_OPERATION_0), throwable);
            } else {
                throw new CmsVfsException(message, throwable);
            }
        }
    }    
}