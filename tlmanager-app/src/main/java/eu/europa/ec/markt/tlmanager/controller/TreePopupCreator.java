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
package eu.europa.ec.markt.tlmanager.controller;

import eu.europa.ec.markt.tlmanager.TSLComposer;
import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.model.treeNodes.ExtensionNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.HistoryNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.PointerNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.ServiceNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLDataNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLRootNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSPNode;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceHistoryInstanceType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPServiceType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Draw a Popup Menu in the JTree. Depending on the mode (TL or LOTL), only a portion of the menu is shown.
 * 
 *
 *
 */

public class TreePopupCreator extends MouseAdapter implements ActionListener {

    private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeys",
            Configuration.getInstance().getLocale());

    private TSLComposer composer;

    private JMenuItem addTsp;
    private JMenuItem addPointer;
    private JMenuItem addService;
    private JMenuItem addServiceInformationExtension;
    private JMenuItem addServiceStatusHistory;
    private JMenuItem addQualificationExtension;

    private JMenuItem removeNode;
    private JMenuItem sortTree;

    private TreePath treePath;

    /**
     * 
     * Instantiates a new tree popup creator.
     * 
     * @param composer the composer
     */
    public TreePopupCreator(TSLComposer composer) {
        this.composer = composer;

        addTsp = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.add.tsp"));
        addTsp.addActionListener(this);
        addTsp.setName("itemAddTSP");

        addPointer = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.add.pointer"));
        addPointer.addActionListener(this);
        addPointer.setName("itemAddPointer");

        addService = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.add.service"));
        addService.addActionListener(this);
        addService.setName("itemAddService");

        addServiceStatusHistory = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.add.history"));
        addServiceStatusHistory.addActionListener(this);
        addServiceStatusHistory.setName("itemAddServiceStatusHistory");

        addServiceInformationExtension = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.add.extension"));
        addServiceInformationExtension.addActionListener(this);
        addServiceInformationExtension.setName("itemAddServiceInformationExtension");

        addQualificationExtension = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.add.qualification"));
        addQualificationExtension.addActionListener(this);
        addQualificationExtension.setName("itemAddQualificationExtension");

        removeNode = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.remove.node"));
        removeNode.addActionListener(this);
        removeNode.setName("itemRemoveNode");

        sortTree = new JMenuItem(uiKeys.getString("MainFrame.tree.popup.sort.tree"));
        sortTree.addActionListener(this);
        sortTree.setName("sortTree");
    }

    private void popupEvent(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        JTree tree = (JTree) e.getSource();
        TreePath path = tree.getPathForLocation(x, y);
        if (path == null) {
            return;
        }
        tree.setSelectionPath(path);

        Object obj = path.getLastPathComponent();
        JPopupMenu popup = new JPopupMenu();
        if (obj instanceof TSLRootNode) {
            if (Configuration.getInstance().isTlMode()) {
                popup.add(addTsp);
            }
            popup.add(addPointer);
            popup.add(sortTree);
        } else if (Configuration.getInstance().isTlMode()) {
            if (obj instanceof TSPNode) {
                popup.add(addService);
            } else if (obj instanceof ServiceNode) {
                popup.add(addServiceStatusHistory);
                ServiceNode serviceNode = (ServiceNode) obj;
                TSPServiceType service = serviceNode.getUserObject();
                if (service.getServiceInformation().getServiceInformationExtensions() != null) {
                    addServiceInformationExtension.setEnabled(false);
                } else {
                    addServiceInformationExtension.setEnabled(true);
                }
                popup.add(addServiceInformationExtension);
            } else if (obj instanceof HistoryNode) {
                HistoryNode historyNode = (HistoryNode) obj;
                ServiceHistoryInstanceType history = historyNode.getUserObject();
                if (history.getServiceInformationExtensions() != null) {
                    addServiceInformationExtension.setEnabled(false);
                } else {
                    addServiceInformationExtension.setEnabled(true);
                }
                popup.add(addServiceInformationExtension);
            } else if (obj instanceof ExtensionNode) {
                popup.add(addQualificationExtension);
            }
        }
        if (!(obj instanceof TSLRootNode)) {
            if (!(obj instanceof PointerNode)) {
                popup.addSeparator();
            }
            popup.add(removeNode);
        }

        popup.show(tree, x, y);
        treePath = path;
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupEvent(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupEvent(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        TSLDataNode selectedObject = (TSLDataNode) treePath.getLastPathComponent();

        if (e.getSource() == addTsp) {
            composer.addTSP((TSLRootNode) selectedObject);

        } else if (e.getSource() == addService) {
            composer.addServiceToTSP((TSPNode) selectedObject);

        } else if (e.getSource() == addPointer) {
            composer.addPointerToOtherTSL((TSLRootNode) selectedObject);

        } else if (e.getSource() == addServiceStatusHistory) {
            composer.addStatusHistory((ServiceNode) selectedObject);

        } else if (e.getSource() == addServiceInformationExtension) {
            if (selectedObject instanceof ServiceNode) {
                composer.addInformationExtension((ServiceNode) selectedObject);
            } else if (selectedObject instanceof HistoryNode) {
                composer.addInformationExtension((HistoryNode) selectedObject);
            }

        } else if (e.getSource() == addQualificationExtension) {
            if (selectedObject instanceof ExtensionNode) {
                composer.addQualificationExtension((ExtensionNode) selectedObject);
            }

        } else if (e.getSource() == removeNode) {
            composer.removeNode(selectedObject);
        } else if (e.getSource() == sortTree) {
            composer.sortChildren(selectedObject);
        }
    }
}