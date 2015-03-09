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

package eu.europa.ec.markt.tlmanager.view.certificate;

import java.util.Arrays;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityType;

/**
 * TODO
 * <p/>
 * <p/>
 * DISCLAIMER: Project owner DG-MARKT.
 *
 * @author <a href="mailto:dgmarkt.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1016 $ - $Date: 2011-06-17 15:30:45 +0200 (Fri, 17 Jun 2011) $
 */
public class DigitalIdentityModel {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DigitalIdentityModel.class);

	private DigitalIdentityType digitalIdentity;
	private boolean isHistorical;

	public boolean isHistorical() {
		return isHistorical;
	}

	public DigitalIdentityModel() {
		digitalIdentity = new DigitalIdentityType();
		isHistorical = false;
	}

	public DigitalIdentityModel(DigitalIdentityType digitalIdentity) {
		this.digitalIdentity = digitalIdentity;
		isHistorical = false;
	}

	public DigitalIdentityModel(DigitalIdentityType digitalIdentity, boolean isHistoric) {
		this.digitalIdentity = digitalIdentity;
		this.isHistorical = isHistoric;
	}

	public CertificateToken getCertificate() {
		if (digitalIdentity.getX509Certificate() != null) {
			return DSSUtils.loadCertificate(digitalIdentity.getX509Certificate());
		} else {
			return null;
		}
	}

	public void setCertificate(CertificateToken certificate) {
		digitalIdentity.setX509SKI(null);
		digitalIdentity.setX509SubjectName(null);

		if (certificate != null) {
			digitalIdentity.setX509Certificate(certificate.getEncoded());
		} else {
			digitalIdentity.setX509Certificate(null);
		}
	}

	public byte[] getSKI() {
		return digitalIdentity.getX509SKI();
	}

	public void setSKI(byte[] ski) {
		digitalIdentity.setX509SKI(ski);
		digitalIdentity.setX509SubjectName(null);
		digitalIdentity.setX509Certificate(null);
	}

	public String getSubjectName() {
		return digitalIdentity.getX509SubjectName();
	}

	public void setSubjectName(String subjectName) {
		digitalIdentity.setX509SubjectName(subjectName);
		digitalIdentity.setX509SKI(null);
		digitalIdentity.setX509Certificate(null);
	}

	public void updateDigitalIdentity() {
		LOG.info("updateDigitalIdentity");

	}

	public DigitalIdentityType getDigitalIdentity() {
		return digitalIdentity;
	}

	@Override
	public String toString() {
		return "DigitalIdentityType{" +
				"x509Certificate=" + Arrays.toString(digitalIdentity.getX509Certificate()) +
				", x509SubjectName='" + digitalIdentity.getX509SubjectName() + '\'' +
				", x509SKI=" + Arrays.toString(digitalIdentity.getX509SKI()) +
				'}';
	}
}
