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

import eu.europa.ec.markt.tlmanager.core.QNames;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.pages.TreeDataPublisher;
import eu.europa.ec.markt.tsl.jaxb.tsl.AnyType;
import eu.europa.ec.markt.tsl.jaxb.tsl.OtherTSLPointerType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * A custom <code>DefaultMutableTreeNode</code> for representing a <code>OtherTSLPointerType</code>.
 * 
 *
 *
 */

public class PointerNode extends DefaultMutableTreeNode implements TSLDataNode {

    private ImageIcon icon;
    private String label;

    /**
     * Instantiates a new pointer node.
     */
    public PointerNode() {
        super(null);
    }

    /**
     * Instantiates a new pointer node.
     * 
     * @param userObject the user object
     */
    public PointerNode(OtherTSLPointerType userObject) {
        super(userObject);
        icon = new ImageIcon(getClass().getResource("/icons/pointer.png"));
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param   userObject  the Object that constitutes this node's 
     *                          user-specified data
     */
    public void setUserObject(OtherTSLPointerType userObject) {
        super.setUserObject(userObject);
    }

    /** {@inheritDoc} */
    public OtherTSLPointerType getUserObject() {
        return (OtherTSLPointerType) userObject;
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
        // no sorting here, because there are no children!
    }

    /** {@inheritDoc} */
    @Override
    public String getAssociatedDataPublisherName() {
        return TreeDataPublisher.POINTER_TO_OTHER_TSL_PAGE;
    }

    /** {@inheritDoc} */
    @Override
    public Icon getIcon() {
        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        if (label == null) {
            OtherTSLPointerType otherTSLPointer = getUserObject();
            label = otherTSLPointer.getTSLLocation();

            String schemeTerritory = "";
            List<Serializable> othInfo = otherTSLPointer.getAdditionalInformation()
                    .getTextualInformationOrOtherInformation();
            for (Object obj : othInfo) {
                if (obj instanceof AnyType) {
                    JAXBElement<?> element = Util.extractJAXBElement((AnyType) obj);
                    QName name = element.getName();
                    if (QNames._SchemeTerritory_QNAME.equals(name)) {
                        schemeTerritory = (String) element.getValue() + ": ";
                        break;
                    }
                }
            }

            label = schemeTerritory + label;
        }

        return label;
    }

    /** {@inheritDoc} */
    @Override
    public void resetLabel() {
        label = null;
    }
}