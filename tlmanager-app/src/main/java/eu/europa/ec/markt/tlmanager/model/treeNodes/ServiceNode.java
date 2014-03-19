/*
 * DSS - Digital Signature Services
 *
 * Copyright (C) 2013 European Commission, Directorate-General Internal Market and Services (DG MARKT), B-1049 Bruxelles/Brussel
 *
 * Developed by: 2013 ARHS Developments S.A. (rue Nicolas Bové 2B, L-1253 Luxembourg) http://www.arhs-developments.com
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * "DSS - Digital Signature Services" is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * DSS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * "DSS - Digital Signature Services".  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.markt.tlmanager.model.treeNodes;

import eu.europa.ec.markt.tlmanager.model.TSLTreeModel;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.pages.TreeDataPublisher;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPServiceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * A custom <code>DefaultMutableTreeNode</code> for representing a <code>TSPServiceType</code>.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class ServiceNode extends DefaultMutableTreeNode implements TSLDataNode {

    private ImageIcon icon;

    /**
     * Instantiates a new service node.
     */
    public ServiceNode() {
        super(null);
    }

    /**
     * Instantiates a new service node.
     * 
     * @param userObject the user object
     */
    public ServiceNode(TSPServiceType userObject) {
        super(userObject);
        icon = new ImageIcon(getClass().getResource("/icons/service.png"));
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param   userObject  the Object that constitutes this node's 
     *                          user-specified data
     */
    public void setUserObject(TSPServiceType userObject) {
        super.setUserObject(userObject);
    }

    /** {@inheritDoc} */
    public TSPServiceType getUserObject() {
        return (TSPServiceType) userObject;
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

    /** {@inheritDoc} */
    @Override
    public String getAssociatedDataPublisherName() {
        return TreeDataPublisher.SERVICE_CURRENT_STATUS_INFORMATION_PAGE;
    }

    /** {@inheritDoc} */
    @Override
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
    }

    // Sort nodes in the way, that HistoryNodes are placed above ExtensionNodes
    // and lexicographically if both nodes are of the same type
    protected static Comparator nodeComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            if (o1 instanceof HistoryNode && o2 instanceof ExtensionNode) {
                return -1;
            } else if (o1 instanceof ExtensionNode && o2 instanceof HistoryNode) {
                return 1;
            } else if (o1 instanceof HistoryNode && o2 instanceof HistoryNode) {
                HistoryNode hn1 = (HistoryNode) o1;
                HistoryNode hn2 = (HistoryNode) o2;
                XMLGregorianCalendar time1 = hn1.getUserObject().getStatusStartingTime();
                XMLGregorianCalendar time2 = hn2.getUserObject().getStatusStartingTime();
                if (time1 == null) {
                    return 1;
                }
                if (time2 == null) {
                    return -1;
                }
                return time1.compare(time2);
            } else {
                return TSLTreeModel.lexicalNodeComparator.compare(o1, o2);
            }
        }
    };

    /** {@inheritDoc} */
    public Icon getIcon() {
        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        // don't cache this label, as it is coming from a MultivaluePanel
        TSPServiceType tspService = getUserObject();
        return Util.getValueForLang(tspService.getServiceInformation().getServiceName(), "en");
    }

    /** {@inheritDoc} */
    @Override
    public void resetLabel() {
        // nop
    }
}