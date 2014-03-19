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

package eu.europa.ec.markt.tlmanager.view.multivalue.content;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.Util;

import javax.swing.*;
import java.awt.*;

/**
 * Management of a selection for a <code>MultiContent</code>.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class ComboBoxContent extends MultiContent<Object> implements SuggestedContentValues {

    private JComboBox multiConfValuesBox;
    private DefaultComboBoxModel multiConfValuesModel;
    private final static String EMPTY = Util.DEFAULT_NO_SELECTION_ENTRY;

    /**
     * Instantiates a new combo box content.
     */
    public ComboBoxContent() {

    }

    @Override
    public void setMultiConfValues(String[] multiConfValues) {
        multiConfValuesModel = new DefaultComboBoxModel(multiConfValues);
        multiConfValuesBox = new JComboBox();
        multiConfValuesBox.setModel(multiConfValuesModel);
        if (!Configuration.getInstance().isEuMode()) {
            multiConfValuesBox.setEditable(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Component getComponent() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(multiConfValuesBox, BorderLayout.NORTH);

        return container;
    }

    @Override
    protected Object retrieveComponentValue(boolean clearOnExit) {
        Object selectedItem = multiConfValuesBox.getSelectedItem();
        if (clearOnExit) {
            multiConfValuesBox.setSelectedItem(EMPTY);
        }

        return selectedItem;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateValue() {
        Object value = getValue(currentKey);
        if (value != null && !value.equals(EMPTY)) {
            multiConfValuesBox.setSelectedItem(value);
        } else {
            multiConfValuesBox.setSelectedItem(EMPTY); // clear
        }
    }
}