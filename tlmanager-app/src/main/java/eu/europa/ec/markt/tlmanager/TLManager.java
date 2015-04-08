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
package eu.europa.ec.markt.tlmanager;

import java.security.Security;

import javax.swing.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import eu.europa.ec.markt.tlmanager.view.MainFrame;

/**
 * Entry point of TLManager. Instantiates a {@code MainFrame}.
 *
 *
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