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

package eu.europa.ec.markt.tlmanager.view;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLDataNode;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * A Renderer for the labels of the tree.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class TSLTreeCellRenderer implements TreeCellRenderer {
    private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeys",
            Configuration.getInstance().getLocale());

    private static final String UNNAMED = uiKeys.getString("MainFrame.tree.unnamedTreeNode");
    private DefaultTreeCellRenderer delegate = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        Icon icon = null;
        String label = null;
        if (value instanceof TSLDataNode) {
            TSLDataNode node = (TSLDataNode) value;
            icon = node.getIcon();
            label = node.getLabel();
        }

        if (label == null || label.isEmpty()) {
            label = UNNAMED;
        }

        delegate.getTreeCellRendererComponent(tree, label, selected, expanded, leaf, row, hasFocus);
        delegate.setToolTipText(label);
        delegate.setIcon(icon);

        return delegate;
    }
}