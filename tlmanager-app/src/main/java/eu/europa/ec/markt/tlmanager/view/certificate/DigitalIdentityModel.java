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
package eu.europa.ec.markt.tlmanager.view.certificate;

import java.util.Arrays;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.tsl.jaxb.tsl.AnyType;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityType;

/**
 * TODO
 *
 *
 *
 *
 *
 *
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
		digitalIdentity.setOther(null);

		if (certificate != null) {
			digitalIdentity.setX509Certificate(certificate.getEncoded());
		} else {
			digitalIdentity.setX509Certificate(null);
		}
	}

	public byte[] getSKI() {
		return digitalIdentity.getX509SKI();
	}

	public AnyType getOTHER() {
		return digitalIdentity.getOther();
	}

	public void setOTHER(AnyType other) {
		digitalIdentity.setOther(other);
		digitalIdentity.setX509SKI(null);
		digitalIdentity.setX509SubjectName(null);
		digitalIdentity.setX509Certificate(null);
	}

	public void setSKI(byte[] ski) {
		digitalIdentity.setX509SKI(ski);
		digitalIdentity.setX509SubjectName(null);
		digitalIdentity.setX509Certificate(null);
		digitalIdentity.setOther(null);
	}

	public String getSubjectName() {
		return digitalIdentity.getX509SubjectName();
	}

	public void setSubjectName(String subjectName) {
		digitalIdentity.setX509SubjectName(subjectName);
		digitalIdentity.setX509SKI(null);
		digitalIdentity.setX509Certificate(null);
		digitalIdentity.setOther(null);
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
				", Other=" + digitalIdentity.getOther() +
				'}';
	}
}
