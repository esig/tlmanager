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

package eu.europa.ec.markt.tlmanager.view.multivalue;

import eu.europa.ec.markt.tlmanager.core.Configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Draw a Popup Menu in a JList.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class ListPopupCreator extends MouseAdapter implements ActionListener {

    private static final ResourceBundle uiKeys = ResourceBundle.getBundle(
            "eu/europa/ec/markt/tlmanager/uiKeysComponents", Configuration.getInstance().getLocale());

    private MultivaluePanel multivaluePanel;
    private JMenuItem duplicate;
    private JPopupMenu popup;

    /**
     * Instantiates a new list popup creator.
     */
    public ListPopupCreator(MultivaluePanel multivaluePanel) {
        this.multivaluePanel = multivaluePanel;
        String dup = uiKeys.getString("MultivaluePanel.duplicate");
        duplicate = new JMenuItem(dup);
        duplicate.addActionListener(this);

        popup = new JPopupMenu();
        popup.add(duplicate);
    }

    private void popupEvent(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        JList list = (JList) e.getSource();

        popup.show(list, x, y);
    }

    /**
     * {@inheritDoc}
     */
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
        if (e.getSource() == duplicate) {
            multivaluePanel.duplicateLanguageEntry();
        }
    }
}