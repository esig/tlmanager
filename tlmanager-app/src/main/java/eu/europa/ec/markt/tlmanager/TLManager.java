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

package eu.europa.ec.markt.tlmanager;

import java.security.Security;

import javax.swing.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import eu.europa.ec.markt.tlmanager.view.MainFrame;

/**
 * Entry point of TLManager. Instantiates a {@code MainFrame}.
 *
 * @version $Revision$ - $Date$
 */

public class TLManager {

    /**
     * The main method.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        Security.addProvider(new BouncyCastleProvider());

        initSwingPreferences();

        MainFrame mf = new MainFrame();
        mf.setVisible(true);
    }

    private static void initSwingPreferences() {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
    }
}