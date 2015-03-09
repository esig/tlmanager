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

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityType;

/**
 * A model that holds the values for a <code>CertificateProperty</code> component.
 *
 * @version $Revision$ - $Date$
 */
public class CertificatePropertyModel {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CertificatePropertyModel.class);

	private SDIWrapper wrapper;
	private String subjectName;
	private boolean sn, ski, cert;
	private boolean skiAvailable;

	// service sdi values
	private DigitalIdentityType certS = null;
	private DigitalIdentityType skiS = null;
	private DigitalIdentityType snS = null;

	/**
	 * The default constructor for CertificatePropertyModel.
	 */
	public CertificatePropertyModel(SDIWrapper wrapper) {
		this.wrapper = wrapper;

		// extract values from service sdi - only the certificate is of importance !
		DigitalIdentityListType sdiService = wrapper.getSdiService();
		for (DigitalIdentityType did : sdiService.getDigitalId()) {
			if (did.getX509Certificate() != null) {
				certS = did;
				break;
			}
		}

		try {

			final CertificateToken certificate = DSSUtils.loadCertificate(certS.getX509Certificate());
			subjectName  = certificate.getSubjectX500Principal().getName();
			snS = new DigitalIdentityType();
			snS.setX509SubjectName(subjectName);

			skiS = new DigitalIdentityType();
			byte[] skiValue = DSSUtils.getSki(certificate.getCertificate());
			skiS.setX509SKI(skiValue);
		} catch (Exception ex) {
			LOG.warn("Unable to load the certificate! " + ex.getMessage(), ex);
		}

		skiAvailable = ((skiS != null) && (skiS.getX509SKI() != null));  // if there is no skiS -> disable box

		// extract values from history sdi and set controls accordingly
		// the actual values are not kept from the history sdi and will be overwritten
		// with the values that are extracted from the certificate of the service
		DigitalIdentityListType sdiHistory = wrapper.getSdiHistory();
		boolean nothingSoFar = true;
		for (DigitalIdentityType did : sdiHistory.getDigitalId()) {
			if (did != null) {
				if (did.getX509Certificate() != null) {
					cert = true;
				} else if (did.getX509SKI() != null) {
					ski = true;
				} else if (did.getX509SubjectName() != null) {
					sn = true;
				}
			}
		}
		nothingSoFar = !cert && !ski && !sn;

		if (nothingSoFar) {
			sn = true;  // set at least the default value: sn
		}

		alignSDI();
	}

	private void alignSDI() {
		DigitalIdentityListType sdiHistory = wrapper.getSdiHistory();
		sdiHistory.getDigitalId().clear();

		boolean nothingIsSelected = true;
		if (sn) {
			sdiHistory.getDigitalId().add(snS);
			nothingIsSelected = false;
		}
		if (ski) {
			sdiHistory.getDigitalId().add(skiS);
			nothingIsSelected = false;
		}
		if (cert) {
			sdiHistory.getDigitalId().add(certS);
			nothingIsSelected = false;
		}

		if (nothingIsSelected) {
			setSn(true);    // enforce default
		}
	}

	/**
	 * @param wrapper the wrapper to set
	 */
	public void setWrapper(SDIWrapper wrapper) {
		this.wrapper = wrapper;
	}

	/**
	 * @param subjectName the subjectName to set
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	/**
	 * @param sn the sn to set
	 */
	public void setSn(boolean sn) {
		this.sn = sn;
		alignSDI();
	}

	/**
	 * @param ski the ski to set
	 */
	public void setSki(boolean ski) {
		this.ski = ski;
		alignSDI();
	}

	/**
	 * @param cert the cert to set
	 */
	public void setCert(boolean cert) {
		this.cert = cert;
		alignSDI();
	}

	/**
	 * @return the wrapper
	 */
	public SDIWrapper getWrapper() {
		return wrapper;
	}

	/**
	 * @return the subjectName
	 */
	public String getSubjectName() {
		return subjectName;
	}

	/**
	 * @return the sn
	 */
	public boolean isSn() {
		return sn;
	}

	/**
	 * @return the ski
	 */
	public boolean isSki() {
		return ski;
	}

	/**
	 * @return the cert
	 */
	public boolean isCert() {
		return cert;
	}

	/**
	 * @return the skiAvailable
	 */
	public boolean isSkiAvailable() {
		return skiAvailable;
	}

	/**
	 * A small helper class for wrapping two <code>ServiceDigitalIdentityListType</code>
	 *
	 * @version $Revision$ - $Date$
	 */
	public static class SDIWrapper {
		private DigitalIdentityListType sdiService;
		private DigitalIdentityListType sdiHistory;

		/**
		 * The default constructor for SDIWrapper.
		 *
		 * @param sdiService the <code>ServiceDigitalIdentityListType</code> of the service
		 * @param sdiHistory the <code>ServiceDigitalIdentityListType</code> of the history
		 */
		public SDIWrapper(DigitalIdentityListType sdiService, DigitalIdentityListType sdiHistory) {
			this.sdiService = sdiService;
			this.sdiHistory = sdiHistory;
		}

		/**
		 * @return the sdiService
		 */
		public DigitalIdentityListType getSdiService() {
			return sdiService;
		}

		/**
		 * @param sdiService the sdiService to set
		 */
		public void setSdiService(DigitalIdentityListType sdiService) {
			this.sdiService = sdiService;
		}

		/**
		 * @return the sdiHistory
		 */
		public DigitalIdentityListType getSdiHistory() {
			return sdiHistory;
		}

		/**
		 * @param sdiHistory the sdiHistory to set
		 */
		public void setSdiHistory(DigitalIdentityListType sdiHistory) {
			this.sdiHistory = sdiHistory;
		}
	}
}