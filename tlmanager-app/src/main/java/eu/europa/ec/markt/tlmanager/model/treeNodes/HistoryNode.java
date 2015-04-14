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
package eu.europa.ec.markt.tlmanager.model.treeNodes;

import eu.europa.ec.markt.tlmanager.model.TSLTreeModel;
import eu.europa.ec.markt.tlmanager.view.pages.TreeDataPublisher;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceHistoryInstanceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A custom <code>DefaultMutableTreeNode</code> for representing a <code>ServiceHistoryInstanceType</code>.
 * 
 *
 *
 */

public class HistoryNode extends DefaultMutableTreeNode implements TSLDataNode {

    private ImageIcon icon;
    private String label;

    /**
     * Instantiates a new history node.
     */
    public HistoryNode() {
        super(null);
    }

    /**
     * Instantiates a new history node.
     * 
     * @param userObject the user object
     */
    public HistoryNode(ServiceHistoryInstanceType userObject) {
        super(userObject);
        icon = new ImageIcon(getClass().getResource("/icons/history.png"));
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param   userObject  the Object that constitutes this node's 
     *                          user-specified data
     */
    public void setUserObject(ServiceHistoryInstanceType userObject) {
        super.setUserObject(userObject);
    }

    /** {@inheritDoc} */
    public ServiceHistoryInstanceType getUserObject() {
        return (ServiceHistoryInstanceType) userObject;
    }

    /** {@inheritDoc} */
    @Override
    public List<Object> getChildren() {
        List<Object> children = new ArrayList<Object>();

        if (this.children != null) {
            children.addAll(this.children);
        }
        return children;
    }

    /** {@inheritDoc} */
    @Override
    public void sort() {
        if (children != null && !children.isEmpty()) {
            Collections.sort(children, TSLTreeModel.lexicalNodeComparator);
            
            // trigger sort on children
            for (Object child: children) {
                if (child instanceof TSLDataNode) {
                    ((TSLDataNode) child).sort();
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getAssociatedDataPublisherName() {
        return TreeDataPublisher.SERVICE_STATUS_HISTORY_PAGE;
    }

    /** {@inheritDoc} */
    public Icon getIcon() {
        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        if (label == null) {
            ServiceHistoryInstanceType history = getUserObject();
            label = history.getServiceStatus();
        }

        return label;
    }

    /** {@inheritDoc} */
    @Override
    public void resetLabel() {
        label = null;
    }
}