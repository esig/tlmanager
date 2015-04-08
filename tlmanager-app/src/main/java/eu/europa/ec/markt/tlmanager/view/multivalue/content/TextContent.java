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

import eu.europa.ec.markt.tlmanager.util.Util;

import javax.swing.*;
import java.awt.*;

/**
 * Management of a textual input for a <code>MultiContent</code>.
 * 
 *
 *
 */

public class TextContent extends MultiContent<String> {

    private JTextPane textValue;

    /**
     * Instantiates a new text content.
     */
    public TextContent() {
        textValue = new JTextPane();
    }

    /** {@inheritDoc} */
    @Override
    public Component getComponent() {
        return textValue;
    }

    /** {@inheritDoc} */
    @Override
    protected String retrieveComponentValue(boolean clearOnExit) {
        String componentValue = textValue.getText();
        componentValue = Util.replaceUnwantedCharacters(componentValue, true);

        if (clearOnExit) {
            textValue.setText("");
        }
        return componentValue;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateValue() {
        String value = getValue(currentKey);
        if (value != null) {
            textValue.setText(value);
        } else {
            textValue.setText("");
        }
    }
}