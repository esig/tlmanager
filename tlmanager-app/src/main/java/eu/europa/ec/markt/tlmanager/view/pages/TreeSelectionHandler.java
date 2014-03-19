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

package eu.europa.ec.markt.tlmanager.view.pages;

import java.util.List;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import eu.europa.ec.markt.tlmanager.model.TSLTreeModel;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLDataNode;

/**
 * Handles Tree Selection Events by choosing the correct content page for the provided objects.
 *
 * @version $Revision$ - $Date$
 */

public class TreeSelectionHandler implements TreeSelectionListener {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TreeSelectionHandler.class);

    private List<TreeDataPublisher> treeDataPublisher;
    private JScrollPane targetPane;

    /**
     * The default constructor for TreeSelectionHandler.
     *
     * @param treeDataPublisher
     * @param targetPane
     */
    public TreeSelectionHandler(List<TreeDataPublisher> treeDataPublisher, JScrollPane targetPane) {
        this.treeDataPublisher = treeDataPublisher;
        this.targetPane = targetPane;
    }

    private TreeDataPublisher findMatching(final String treeDataPublisherName) {
        for (TreeDataPublisher tdp : treeDataPublisher) {
            if (tdp.getName().equals(treeDataPublisherName)) {
                return tdp;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.getNewLeadSelectionPath() != null) {
            Object selectedObj = e.getNewLeadSelectionPath().getLastPathComponent();

            TreeDataPublisher tdp = null;
            if (selectedObj instanceof TSLDataNode) {
                TSLDataNode tslDataNode = (TSLDataNode) selectedObj;
                tdp = findMatching(tslDataNode.getAssociatedDataPublisherName());
                tdp.updateViewFromData(tslDataNode);
                targetPane.setViewportView(tdp);
            } else {
                LOG.warn("No associated TreeDataPublisher found for {}", selectedObj);
            }

            // create an update for the previous node, just to be sure that every change is reflected directly
            JTree tree = (JTree) e.getSource();
            TSLTreeModel model = (TSLTreeModel) tree.getModel();
            model.fireNodeChanged(e.getOldLeadSelectionPath());
        }
    }
}