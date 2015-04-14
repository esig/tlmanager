/**
 * TL Manager
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "TL Manager" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.ec.markt.tlmanager;

import javax.swing.tree.MutableTreeNode;

/**
 * Event for notification of <code>TSLTreeModel</code> about structural changes in the <code>TrustStatusListType</code>
 * of the <code>TSLComposer</code>.
 * 
 *
 *
 */

public class TSLComposerEvent {

    public static final String NEW_TSL = "NEWTSL";
    public static final String ADD_POINTER = "ADDPOINTER";
    public static final String ADD_TSP = "ADDTSP";
    public static final String ADD_SERVICE = "ADDSERVICE";
    public static final String ADD_HISTORY = "ADDHISTORY";
    public static final String ADD_INFOEXTENSION = "ADDINFOEXTENSION";
    public static final String ADD_QUALIFICATIONEXTENSION = "ADDQUALIFICATIONEXTENSION";
    public static final String REMOVE_NODE = "REMOVENODE";
    public static final String SORT_NODE = "SORTNODE";

    private String eventCode;
    private MutableTreeNode parentNode;
    private Object data;

    /**
     * Instantiates a new tSL composer event.
     * 
     * @param eventCode the event code
     * @param parentNode the parent node
     * @param data the data
     */
    public TSLComposerEvent(String eventCode, MutableTreeNode parentNode, Object data) {
        this.eventCode = eventCode;
        this.parentNode = parentNode;
        this.data = data;
    }

    /**
     * Gets the event code.
     * 
     * @return the event code
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * Sets the event code.
     * 
     * @param eventCode the new event code
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * Gets the parent node.
     * 
     * @return the parent node
     */
    public MutableTreeNode getParentNode() {
        return parentNode;
    }

    /**
     * Sets the parent node.
     * 
     * @param parentNode the new parent node
     */
    public void setParentNode(MutableTreeNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * Gets the data.
     * 
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the data.
     * 
     * @param data the new data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /** @{inheritDoc */
    @Override
    public String toString() {
        return "Event[code=" + eventCode + "]";
    }
}