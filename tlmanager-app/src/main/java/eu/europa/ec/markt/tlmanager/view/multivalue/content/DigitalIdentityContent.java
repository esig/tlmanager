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

import java.awt.Component;

import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.tlmanager.view.certificate.DigitalIdentityModel;
import eu.europa.ec.markt.tlmanager.view.panel.DigitalIdentityPanel;

/**
 * TODO
 *
 *
 *
 *
 *
 *
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
		 CertificateToken certificate = null;
		 int i = 0;
		 while(!certificateFound && (listOfCollectionKeys.size()>i)) {
			 DigitalIdentityModel dIvalue = getValue(listOfCollectionKeys.get(i));
			 certificateFound = dIvalue.getCertificate() != null;
			 certificate = dIvalue.getCertificate();
			 i++;
		 }
		 panel.setCertificate(certificate);
	 }

}
