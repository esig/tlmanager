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

package eu.europa.ec.markt.tlmanager.view.common;

import eu.europa.ec.markt.tlmanager.core.Configuration;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * A custom panel with an own <code>TitledBorder</code>. The color of the label can be changed.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class TitledPanel extends JPanel {

    private Color MANDATORY = Color.red;
    private Color NORMAL = Color.black;
    private TitledBorder titledBorder;

    /**
     * Instantiates a new titled panel.
     */
    public TitledPanel() {
        this("Titled Panel");
        MANDATORY = Configuration.MANDATORY_COLOR;
        NORMAL = Configuration.NORMAL_COLOR;
    }

    /**
     * Instantiates a new titled panel. Use an own title.
     * 
     * @param title the title
     */
    public TitledPanel(String title) {
        super();
        titledBorder = new TitledBorder(title);
        setBorder(titledBorder);
    }

    /**
     * Sets a title for the <code>TitledBorder</code>
     * 
     * @param title the title
     */
    public void setTitle(String title) {
        titledBorder.setTitle(title);
    }

    /**
     * Changes the color of the title depending on the given mandatory flag.
     * 
     * @param mandatory if true, the mandatory color is set
     */
    public void setTitleMandatory(boolean mandatory) {
        ((TitledBorder) getBorder()).setTitleColor(mandatory ? MANDATORY : NORMAL);
        repaint();
    }

    /**
     * Changes the color of the title border according to the selection state of the checkboxes that are contained in
     * this panel.
     * 
     * @param isNotMandatory if true, there is no need to express the mandatory state
     */
    public void changeMandatoryState(boolean isNotMandatory) {
        setTitleMandatory(!isNotMandatory);
    }
}