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

import eu.europa.ec.markt.tlmanager.view.pages.TreeDataPublisher;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationElementType;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A custom <code>DefaultMutableTreeNode</code> for representing a <code>QualificationElementType</code>.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class QualificationNode extends DefaultMutableTreeNode implements TSLDataNode {

    private ImageIcon icon;
    private String label;
    private static final String LABEL = "Qualifications";

    /**
     * Instantiates a new qualification node.
     */
    public QualificationNode() {
        super(null);
    }

    /**
     * Instantiates a new qualification node.
     * 
     * @param userObject the user object
     */
    public QualificationNode(QualificationElementType userObject) {
        super(userObject);
        icon = new ImageIcon(getClass().getResource("/icons/qualification.png"));
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param   userObject  the Object that constitutes this node's 
     *                          user-specified data
     */
    public void setUserObject(QualificationElementType userObject) {
        super.setUserObject(userObject);
    }

    /** {@inheritDoc} */
    public QualificationElementType getUserObject() {
        return (QualificationElementType) userObject;
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
        return TreeDataPublisher.QUALIFICATION_EXTENSION_PAGE;
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