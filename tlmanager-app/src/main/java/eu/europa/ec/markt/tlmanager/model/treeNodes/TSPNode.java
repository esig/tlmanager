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
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.pages.TreeDataPublisher;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A custom <code>DefaultMutableTreeNode</code> for representing a <code>TSPType</code>.
 * 
 *
 *
 */

public class TSPNode extends DefaultMutableTreeNode implements TSLDataNode {

    private ImageIcon icon;

    /**
     * Instantiates a new tSP node.
     */
    public TSPNode() {
        super(null);
    }

    /**
     * Instantiates a new tSP node.
     * 
     * @param userObject the user object
     */
    public TSPNode(TSPType userObject) {
        super(userObject);
        icon = new ImageIcon(getClass().getResource("/icons/tsp.png"));
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param   userObject  the Object that constitutes this node's 
     *                          user-specified data
     */
    public void setUserObject(TSPType userObject) {
        super.setUserObject(userObject);
    }

    /** {@inheritDoc} */
    public TSPType getUserObject() {
        return (TSPType) userObject;
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
        return TreeDataPublisher.TSP_INFORMATION_PAGE;
    }

    /** {@inheritDoc} */
    public Icon getIcon() {
        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        // don't cache this label, as it is coming from a MultivaluePanel
        TSPType tsp = getUserObject();
        return Util.getValueForLang(tsp.getTSPInformation().getTSPName(), "en");
    }

    /** {@inheritDoc} */
    @Override
    public void resetLabel() {
        // nop
    }
}