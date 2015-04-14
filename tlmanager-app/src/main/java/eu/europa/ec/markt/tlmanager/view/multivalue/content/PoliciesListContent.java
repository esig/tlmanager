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

import eu.europa.ec.markt.tlmanager.view.panel.PoliciesListModel;
import eu.europa.ec.markt.tlmanager.view.panel.PoliciesListPanel;

/**
 * TODO
 *
 *
 *
 *
 *
 *
 */
public class PoliciesListContent extends MultiContent<PoliciesListModel> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PoliciesListContent.class);

    private final PoliciesListPanel panel;

    public PoliciesListContent() {
        panel = new PoliciesListPanel();
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
    protected PoliciesListModel retrieveComponentValue(boolean clearOnExit) {
        PoliciesListModel model = panel.getPoliciesListModel();
        if (clearOnExit) {
            panel.setPoliciesListModel(new PoliciesListModel());
        }
        return model;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateValue() {
        PoliciesListModel value = getValue(currentKey);
        if (value != null) {
            panel.setPoliciesListModel(value);
        } else {
            panel.setPoliciesListModel(new PoliciesListModel());
        }
    }

}
