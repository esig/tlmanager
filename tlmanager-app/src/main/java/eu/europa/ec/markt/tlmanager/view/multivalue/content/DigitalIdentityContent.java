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

import java.awt.*;

import eu.europa.ec.markt.tlmanager.view.certificate.DigitalIdentityModel;
import eu.europa.ec.markt.tlmanager.view.panel.DigitalIdentityPanel;
import java.security.cert.X509Certificate;

/**
 * TODO
 * <p/>
 * <p/>
 * DISCLAIMER: Project owner DG-MARKT.
 *
 * @author <a href="mailto:dgmarkt.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1016 $ - $Date: 2011-06-17 15:30:45 +0200 (Fri, 17 Jun 2011) $
 */
public class DigitalIdentityContent extends MultiContent<DigitalIdentityModel> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DigitalIdentityContent.class);

    private final DigitalIdentityPanel panel;

    public DigitalIdentityContent() {
        panel = new DigitalIdentityPanel();
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
    protected DigitalIdentityModel retrieveComponentValue(boolean clearOnExit) {
        DigitalIdentityModel model = panel.getDigitalIdentityModel();

        if (clearOnExit) {
            panel.clearOnExit();
        }
        return model;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateValue() {
        LOG.info("Update value for key " + currentKey);
        DigitalIdentityModel value = getValue(currentKey);
        LOG.info("... value is " + value);
        panel.updateCurrentValues(value);

        DigitalIdentityModel model = panel.getDigitalIdentityModel();
        panel.setHistorical(true);

        //Check if there is a Certificate defined in Digital ID
        boolean certificateFound = false;
        java.util.List<String> listOfCollectionKeys = getKeys();
        X509Certificate certificate = null;
        int i = 0;
        while(!certificateFound && listOfCollectionKeys.size()>i) {
            DigitalIdentityModel dIvalue = getValue(listOfCollectionKeys.get(i));
            certificateFound = dIvalue.getCertificate() != null;
            certificate = dIvalue.getCertificate();
            i++;
        }
        panel.setCertificate(certificate);
    }

}
