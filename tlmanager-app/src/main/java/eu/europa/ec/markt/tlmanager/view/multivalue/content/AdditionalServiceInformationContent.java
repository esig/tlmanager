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

import eu.europa.ec.markt.tlmanager.view.panel.AdditionalServiceInformationModel;
import eu.europa.ec.markt.tlmanager.view.panel.AdditionalServiceInformationPanel;

import java.awt.*;

/**
 * Management of an <code>AdditionalServiceInformationPanel</code> for a <code>MultiContent</code>.
 * 
 *
 *
 */

public class AdditionalServiceInformationContent extends MultiContent<AdditionalServiceInformationModel> {

    private AdditionalServiceInformationPanel additionalServiceInformationPanel;

    /**
     * Instantiates a new additional service information content.
     */
    public AdditionalServiceInformationContent() {
        additionalServiceInformationPanel = new AdditionalServiceInformationPanel();
    }

    /** {@inheritDoc} */
    @Override
    public Component getComponent() {
        return additionalServiceInformationPanel;
    }

    /** {@inheritDoc} */
    @Override
    protected AdditionalServiceInformationModel retrieveComponentValue(boolean clearOnExit) {
        AdditionalServiceInformationModel model = additionalServiceInformationPanel.retrieveCurrentValues();
        if (clearOnExit) {
            additionalServiceInformationPanel.clearModel();
        }
        if (model.isEmpty()) {
            return null;
        }
        return model;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateValue() {
        AdditionalServiceInformationModel value = getValue(currentKey);
        if (value != null) {
            additionalServiceInformationPanel.updateCurrentValues(value);
        } else {
            additionalServiceInformationPanel.clearModel();
        }
    }
}