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
import eu.europa.ec.markt.tsl.jaxb.tsl.TrustStatusListType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * A custom <code>DefaultMutableTreeNode</code> for representing a <code>TrustStatusListType</code>.
 * 
 *
 *
 */

public class TSLRootNode extends DefaultMutableTreeNode implements TSLDataNode {

    private ImageIcon icon;
    private String label;
    private static final String LABEL = "TSL";

    /**
     * Instantiates a new tSL root node.
     */
    public TSLRootNode() {
        super(null);
    }

    /**
     * Instantiates a new tSL root node.
     * 
     * @param userObject the user object
     */
    public TSLRootNode(TrustStatusListType userObject) {
        super(userObject);
        icon = new ImageIcon(getClass().getResource("/icons/tsl.png"));
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param   userObject  the Object that constitutes this node's 
     *                          user-specified data
     */
    public void setUserObject(TrustStatusListType userObject) {
        super.setUserObject(userObject);
    }

    /** {@inheritDoc} */
    @Override
    public TrustStatusListType getUserObject() {
        return (TrustStatusListType) userObject;
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
            Collections.sort(children, nodeComparator);
            
            // trigger sort on children
            for (Object child: children) {
                if (child instanceof TSLDataNode) {
                    ((TSLDataNode) child).sort();
                }
            }
        }
    }

    // Sort nodes in the way, that PointerNodes are placed above TSPNodes
    // and lexicographically if both nodes are of the same type
    protected static Comparator nodeComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            if (o1 instanceof PointerNode && o2 instanceof TSPNode) {
                return -1;
            } else if (o1 instanceof TSPNode && o2 instanceof PointerNode) {
                return 1;
            } else if (o1 instanceof PointerNode && o2 instanceof PointerNode) {
                PointerNode pn1 = (PointerNode) o1;
                PointerNode pn2 = (PointerNode) o2;
                String label1 = pn1.getLabel();
                String label2 = pn2.getLabel();
                String extension = "pdf";
                if (label1.substring(0, 2).equals(label2.substring(0, 2))) {
                    // if one extension is 'pdf', put it at the top
                    if (label1.endsWith(extension) && !label2.endsWith(extension) ) {
                        return -1;
                    } else if (!label1.endsWith(extension) && label2.endsWith(extension)) {
                        return 1;
                    }
                }
                return TSLTreeModel.lexicalNodeComparator.compare(o1, o2);
            } else {
                return TSLTreeModel.lexicalNodeComparator.compare(o1, o2);
            }
        }
    };

    /** {@inheritDoc} */
    @Override
    public String getAssociatedDataPublisherName() {
        return TreeDataPublisher.TSL_INFORMATION_PAGE;
    }

    /** {@inheritDoc} */
    @Override
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
    }

    /** {@inheritDoc} */
    public Icon getIcon() {
        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        if (label == null) {
            label = LABEL;
        }

        return label;
    }

    /** {@inheritDoc} */
    @Override
    public void resetLabel() {
        label = null;
    }
}