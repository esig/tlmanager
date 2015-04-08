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

import eu.europa.ec.markt.tlmanager.view.panel.PostalModel;
import eu.europa.ec.markt.tlmanager.view.panel.PostalPanel;

import java.awt.*;

/**
 * Management of a <code>PostalPanel</code> for a <code>MultiContent</code>.
 * 
 *
 *
 */

public class PostalContent extends MultiContent<PostalModel> {

    private PostalPanel postalPanel;

    /**
     * Instantiates a new postal content.
     */
    public PostalContent() {
        postalPanel = new PostalPanel();
    }

    /** {@inheritDoc} */
    @Override
    public Component getComponent() {
        return postalPanel;
    }

    /** {@inheritDoc} */
    @Override
    protected PostalModel retrieveComponentValue(boolean clearOnExit) {
        PostalModel model = postalPanel.retrieveCurrentValues();
        if (clearOnExit) {
            postalPanel.clearModel();
        }
        return model;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateValue() {
        Object value = getValue(currentKey);
        if (value != null) {
            postalPanel.updateCurrentValues((PostalModel) value);
        } else {
            postalPanel.clearModel();
        }
    }
}