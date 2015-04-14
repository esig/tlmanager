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
package eu.europa.ec.markt.tlmanager.view.multivalue.content;

import java.awt.*;

import eu.europa.ec.markt.tlmanager.view.panel.KeyUsageModel;
import eu.europa.ec.markt.tlmanager.view.panel.KeyUsagePanel;

/**
 * TODO
 *
 *
 *
 *
 *
 *
 */
public class KeyUsageContent extends MultiContent<KeyUsageModel> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(KeyUsageContent.class);

    private final KeyUsagePanel panel;

    public KeyUsageContent() {
        panel = new KeyUsagePanel();
        panel.setName(panel.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponent() {
        return panel;
    }

    @Override
    protected KeyUsageModel retrieveComponentValue(boolean clearOnExit) {
        KeyUsageModel model = panel.retrieveCurrentValues();
        if (clearOnExit) {
            panel.updateCurrentValues(new KeyUsageModel());
        }
        return model;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateValue() {
        KeyUsageModel value = getValue(currentKey);
        if (value != null) {
            panel.updateCurrentValues(value);
        } else {
            panel.clearModel();
        }
    }

}
