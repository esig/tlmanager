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

package eu.europa.ec.markt.tlmanager.core.signature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.dss.DigestAlgorithm;
import eu.europa.ec.markt.dss.common.JavaPreferencesDAO;
import eu.europa.ec.markt.dss.common.SignatureTokenType;
import eu.europa.ec.markt.dss.common.UserPreferencesDAO;
import eu.europa.ec.markt.dss.exception.DSSException;
import eu.europa.ec.markt.dss.parameter.DSSReference;
import eu.europa.ec.markt.dss.parameter.DSSTransform;
import eu.europa.ec.markt.dss.parameter.SignatureParameters;
import eu.europa.ec.markt.dss.signature.DSSDocument;
import eu.europa.ec.markt.dss.signature.InMemoryDocument;
import eu.europa.ec.markt.dss.signature.SignatureLevel;
import eu.europa.ec.markt.dss.signature.SignaturePackaging;
import eu.europa.ec.markt.dss.signature.token.DSSPrivateKeyEntry;
import eu.europa.ec.markt.dss.signature.token.MSCAPISignatureToken;
import eu.europa.ec.markt.dss.signature.token.PasswordInputCallback;
import eu.europa.ec.markt.dss.signature.token.Pkcs11SignatureToken;
import eu.europa.ec.markt.dss.signature.token.Pkcs12SignatureToken;
import eu.europa.ec.markt.dss.signature.token.SignatureTokenConnection;
import eu.europa.ec.markt.dss.signature.xades.XAdESService;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.dss.validation102853.CertificateVerifier;
import eu.europa.ec.markt.dss.validation102853.CommonCertificateVerifier;
import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.core.validation.ValidationLogger;
import eu.europa.ec.markt.tlmanager.util.Util;

/**
 * SignatureManager deals with everything related to the creation of a signature for a given tsl.
 *
 * @version $Revision$ - $Date$
 */

public class SignatureManager {
	private static final Logger LOG = LoggerFactory.getLogger(SignatureManager.class);

	private UserPreferencesDAO userPreferencesDAO = new JavaPreferencesDAO();

	private SignatureLevel signatureLevel = SignatureLevel.XAdES_BASELINE_B;
	private SignaturePackaging signaturePackaging = SignaturePackaging.ENVELOPED;
	private DigestAlgorithm digestAlgorithm;

	private ValidationLogger validationLogger;
	private InMemoryDocument document;
	private SignatureTokenConnection signatureTokenConnection;
	private XAdESService xadesService;
	private SignatureTokenType provider = SignatureTokenType.PKCS11;
	private SignatureTokenType lastProvider;
	private File pkcs11Library;
	private File pkcs12File;

	private File target;
	private char[] password;
	private PasswordInputCallback pwCallback;

	private List<DSSPrivateKeyEntry> keys;
	private Certificate selectedCertificate;

	/**
	 * The default constructor for SignatureManager.
	 *
	 * @param validationLogger the {@code ValidationLogger} object
	 */
	public SignatureManager(ValidationLogger validationLogger) {
		this.validationLogger = validationLogger;
		digestAlgorithm = Configuration.getInstance().getDigestAlgorithm();

		final CertificateVerifier certificateVerifier = new CommonCertificateVerifier(true);
		xadesService = new XAdESService(certificateVerifier);
	}

	/**
	 * Initialises the {@code InMemoryDocument} from a provided {@code Document}.
	 */
	public void initInMemoryDocument(Document document) {
		if (document == null) {
			LOG.error(">>> Document is null!");
		}
		try {
			ByteArrayOutputStream outputDoc = new ByteArrayOutputStream();
			Result output = new StreamResult(outputDoc);
			Transformer transformer = Util.createPrettyTransformer(3);
			Source source = new DOMSource(document);
			transformer.transform(source, output);
			this.document = new InMemoryDocument(outputDoc.toByteArray());

			outputDoc.close();
		} catch (TransformerConfigurationException e) {
			LOG.error(">>>" + e.getMessage(), e);
		} catch (TransformerFactoryConfigurationError e) {
			LOG.error(">>>" + e.getMessage(), e);
		} catch (TransformerException e) {
			LOG.error(">>>" + e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(">>>" + e.getMessage(), e);
		}
	}

	private void initializeTokenCon() {
		if (signatureTokenConnection != null) {
			signatureTokenConnection.close();
			signatureTokenConnection = null;
			lastProvider = null;
			selectedCertificate = null;
		}

		if (SignatureTokenType.PKCS11.equals(provider)) {
			signatureTokenConnection = new Pkcs11SignatureToken(pkcs11Library.getAbsolutePath(), pwCallback);
			lastProvider = SignatureTokenType.PKCS11;
		} else if (SignatureTokenType.PKCS12.equals(provider)) {
			signatureTokenConnection = new Pkcs12SignatureToken(password, pkcs12File);
			lastProvider = SignatureTokenType.PKCS12;
		} else if (SignatureTokenType.MSCAPI.equals(provider)) {
			signatureTokenConnection = new MSCAPISignatureToken();
			lastProvider = SignatureTokenType.MSCAPI;
		}
	}

	/**
	 * Retrieves the certificate from the respective source.
	 *
	 * @throws eu.europa.ec.markt.tlmanager.core.exception.SignatureException
	 */
	public void retrieveCertificates() throws DSSException {
		if (provider == null) {
			return;
		}
		if ((signatureTokenConnection == null) || !provider.equals(lastProvider)) { // provider was changed in ui
			initializeTokenCon();
		}
		try {
			keys = signatureTokenConnection.getKeys();
		} catch (DSSException e) {
			signatureTokenConnection = null; // make sure that it is reinitialised next time!
			LOG.error(">>>Unable to get Keys: " + e.getMessage(), e);
			throw new DSSException(e);
		}
	}

	/**
	 * Returns the matching source for the currently selected provider
	 *
	 * @return the matching source file
	 */
	public File getMatchingSource() {
		if (SignatureTokenType.PKCS11.equals(provider)) {
			return getPkcs11Library();
		} else if (SignatureTokenType.PKCS12.equals(provider)) {
			return getPkcs12File();
		} else {
			return null;
		}
	}

	/**
	 * @param provider the signature token provider
	 */
	public void setProvider(SignatureTokenType provider) {
		if (provider != null) {
			userPreferencesDAO.setSignatureTokenType(provider);
		}
		this.provider = provider;
	}

	/**
	 * @return the tokenType
	 */
	public SignatureTokenType getProvider() {
		if (provider == null) {
			provider = userPreferencesDAO.getSignatureTokenType();
		}
		return provider;
	}

	/**
	 * Gets the pkcs11 library.
	 *
	 * @return the pkcs11 library.
	 */
	public File getPkcs11Library() {
		if (pkcs11Library == null) {
			String path = userPreferencesDAO.getPKCS11LibraryPath();
			if ((path != null) && !path.isEmpty()) {
				pkcs11Library = new File(path);
			}
		}
		return pkcs11Library;
	}

	/**
	 * Sets the pkcs11 library.
	 *
	 * @param pkcs11LibraryPath the file
	 */
	public void setPkcs11Library(File pkcs11LibraryPath) {
		if (pkcs11LibraryPath != null) {
			userPreferencesDAO.setPKCS11LibraryPath(pkcs11LibraryPath.getAbsolutePath());
		}
		this.pkcs11Library = pkcs11LibraryPath;
	}

	/**
	 * Gets the pkcs12 library.
	 *
	 * @return the pkcs12 library
	 */
	public File getPkcs12File() {
		if (pkcs12File == null) {
			String path = userPreferencesDAO.getPKCS12FilePath();
			if ((path != null) && !path.isEmpty()) {
				pkcs12File = new File(path);
			}
		}
		return pkcs12File;
	}

	/**
	 * Sets the pkcs12 library.
	 *
	 * @param pkcs12FilePath the file
	 */
	public void setPkcs12File(File pkcs12FilePath) {
		if (pkcs12FilePath != null) {
			userPreferencesDAO.setPKCS12FilePath(pkcs12FilePath.getAbsolutePath());
		}
		this.pkcs12File = pkcs12FilePath;
	}

	/**
	 * Retrieves the validation message that will be displayed in the ui.
	 *
	 * @return the list
	 */
	public List<ValidationLogger.Message> retrieveValidationMessages() {
		return validationLogger.getValidationMessages();
	}

	/**
	 * Checks if the validation logger has any errors.
	 *
	 * @return true, if the validation contains errors
	 */
	public boolean isValidationErroneous() {
		return validationLogger.hasErrors();
	}

	/**
	 * Do the actual signing.
	 *
	 * @throws java.io.IOException
	 */
	public void sign() throws IOException {

		final DSSPrivateKeyEntry pk = determineCurrentPK();

		final SignatureParameters parameters = new SignatureParameters();
		parameters.setDigestAlgorithm(digestAlgorithm);
		parameters.setPrivateKeyEntry(pk);
		/**
		 * 5.7 Signature
		 * 5.7.1 Signed Trusted List
		 * The trusted list shall be signed by the 'Scheme operator name' (clause 5.3.4) to ensure its authenticity and integrity.
		 * The format of the signature shall be XAdES BES or EPES as defined by TS 101 903 [3]. Such electronic signature
		 * implementation shall meet requirements as stated in annex B. The signature algorithm as well as the certified signature
		 * key shall conform to security requirement for a minimum 3 years usable key as specified in Table 14 of
		 * TS 102 176-1 [2].
		 * The TLSO certificate, to be used to verify its signature on the TL, shall be protected with the signature in one of the
		 * ways specified by TS 101 903 [3]. The SigningCertificate signed attribute (or property) available in TS 101 903 [3]
		 * signatures should be used for this purpose.
		 *
		 * <b>The ds:keyInfo shall not contain any associated certificate chain.</b>
		 */
		parameters.clearCertificateChain();
		final CertificateToken certificateToken = parameters.getSigningCertificate();
		parameters.setSigningCertificate(certificateToken);
		parameters.setSignatureLevel(signatureLevel);
		parameters.setSignaturePackaging(signaturePackaging);

		final List<DSSReference> references = new ArrayList<DSSReference>();

		DSSReference dssReference = new DSSReference();
		dssReference.setId("xml_ref_id");
		dssReference.setUri("");
		dssReference.setContents(document);
		dssReference.setDigestMethodAlgorithm(digestAlgorithm);

		final List<DSSTransform> transforms = new ArrayList<DSSTransform>();

		DSSTransform dssTransform = new DSSTransform();
		dssTransform.setAlgorithm(CanonicalizationMethod.ENVELOPED);
		dssTransform.setPerform(true);
		transforms.add(dssTransform);

		dssTransform = new DSSTransform();
		dssTransform.setAlgorithm(CanonicalizationMethod.EXCLUSIVE);
		dssTransform.setPerform(true);
		transforms.add(dssTransform);

		dssReference.setTransforms(transforms);
		references.add(dssReference);

		parameters.setReferences(references);

		OutputStream signStore;
		byte[] dataToSign = xadesService.getDataToSign(document, parameters);
		byte[] signatureValue = signatureTokenConnection.sign(dataToSign, parameters.getDigestAlgorithm(), pk);
		DSSDocument signResult = xadesService.signDocument(document, parameters, signatureValue);

		signStore = new FileOutputStream(target);
		DSSUtils.copy(signResult.openStream(), signStore);
	}

	/**
	 * Extract the list of {@code Certificate} from the current list of {@code PrivateKeyEntry}
	 *
	 * @return a list of certificates
	 */
	public List<CertificateToken> getCertificates() {
		if ((keys == null) || !provider.equals(lastProvider)) {
			retrieveCertificates();
		}
		List<CertificateToken> certificates = new ArrayList<CertificateToken>();
		for (DSSPrivateKeyEntry key : keys) {
			certificates.add(key.getCertificate());
		}

		return certificates;
	}

	private DSSPrivateKeyEntry determineCurrentPK() {
		if ((keys == null) || (selectedCertificate == null)) {
			return null;
		}
		for (DSSPrivateKeyEntry key : keys) {
			if (selectedCertificate.equals(key.getCertificate().getCertificate())) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @param pwCallback the pwCallback to set
	 */
	public void setPwCallback(PasswordInputCallback pwCallback) {
		this.pwCallback = pwCallback;
	}

	/**
	 * @return the selectedCertificate
	 */
	public Certificate getSelectedCertificate() {
		return selectedCertificate;
	}

	/**
	 * @param selectedCertificate the selectedCertificate to set
	 */
	public void setSelectedCertificate(Certificate selectedCertificate) {
		this.selectedCertificate = selectedCertificate;
	}

	/**
	 * @return the document
	 */
	public InMemoryDocument getDocument() {
		return document;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(InMemoryDocument document) {
		this.document = document;
	}

	/**
	 * @return the target
	 */
	public File getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(File target) {
		if (!target.isDirectory()) {
			this.target = target;
		}
	}

	/**
	 * Returns true, if any source is set
	 *
	 * @return true, if any source is set
	 */
	public boolean isAnySource() {
		return (pkcs11Library != null) || (pkcs12File != null);
	}

	/**
	 * @return the digestAlgorithm
	 */
	public DigestAlgorithm getDigestAlgorithm() {
		return digestAlgorithm;
	}

	/**
	 * @param digestAlgorithm the digestAlgorithm to set
	 */
	public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}
}