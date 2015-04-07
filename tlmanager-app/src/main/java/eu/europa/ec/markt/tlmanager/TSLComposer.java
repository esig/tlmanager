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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.core.Migration;
import eu.europa.ec.markt.tlmanager.core.ObjectFiller;
import eu.europa.ec.markt.tlmanager.core.QNames;
import eu.europa.ec.markt.tlmanager.core.exception.FillerException;
import eu.europa.ec.markt.tlmanager.core.exception.OpenException;
import eu.europa.ec.markt.tlmanager.core.exception.SaveException;
import eu.europa.ec.markt.tlmanager.core.validation.Validation;
import eu.europa.ec.markt.tlmanager.core.validation.ValidationLogger;
import eu.europa.ec.markt.tlmanager.core.validation.ValidationParameters;
import eu.europa.ec.markt.tlmanager.model.treeNodes.ExtensionNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.HistoryNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.PointerNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.QualificationNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.ServiceNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLDataNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLRootNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSPNode;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.util.WhitespaceFilterXML;
import eu.europa.ec.markt.tlmanager.view.MainFrame;
import eu.europa.ec.markt.tlmanager.view.common.NewServiceStatusPanel.Values;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationElementType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationsType;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionsListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.InternationalNamesType;
import eu.europa.ec.markt.tsl.jaxb.tsl.NextUpdateType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ObjectFactory;
import eu.europa.ec.markt.tsl.jaxb.tsl.OtherTSLPointerType;
import eu.europa.ec.markt.tsl.jaxb.tsl.OtherTSLPointersType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceHistoryInstanceType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceHistoryType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSLSchemeInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPServiceInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPServiceType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPServicesListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TrustServiceProviderListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TrustStatusListType;

/**
 * Central class for managing everything that is related to creation, editing, saving and signing of a Trusted List. It is an {@code Observable} and notifies {@code TSLTreeModel}
 * (with the help of {@code TSLComposerEvent}'s) about changes.
 *
 * @version $Revision$ - $Date$
 */

public class TSLComposer extends Observable {

	private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeys", Configuration.getInstance().getLocale());
	private static final Logger LOG = LoggerFactory.getLogger(TSLComposer.class);
	private TrustStatusListType tsl;
	private File currentFile;
	private boolean doCreateNodesForData = false;
	private boolean signatureRemovedFromLastList = false;
	private boolean fileMigrated = false;
	private Migration.MigrationMessages migrationMessages;
	private boolean treeSorted = false;
	private MainFrame mainFrame;
	private ValidationLogger validationLogger;

	private final BigInteger TSL_VERSION_IDENTIFIER;

	private static ObjectFactory objectFactoryTSL;
	private static eu.europa.ec.markt.tsl.jaxb.ecc.ObjectFactory objectFactoryECC;

	private static final Class[] boundClasses = new Class[]{ObjectFactory.class, eu.europa.ec.markt.tsl.jaxb.tslx.ObjectFactory.class, eu.europa.ec.markt.tsl.jaxb.ecc.ObjectFactory.class, eu.europa.ec.markt.tsl.jaxb.xades.ObjectFactory.class};

	private static JAXBContext jaxbContext;
	private static Unmarshaller unmarshaller;
	private static Marshaller marshaller;

	/*
	 * This Map stores all associations between any created jaxb object that is directly related to a TreeNode and their
	 * respective tree path. It serves as a means to locate objects in the tree from a logged validation message. cf.
	 * Validation and ValidationLogger
	 */
	private Map<Object, TreeNode[]> validationAssociations;

	/**
	 * The default constructor for TSLComposer.
	 *
	 * @param mainFrame an instance of {@code MainFrame}
	 */
	public TSLComposer(MainFrame mainFrame) {
		this.mainFrame = mainFrame;

		try {
			jaxbContext = JAXBContext.newInstance(boundClasses);
			unmarshaller = jaxbContext.createUnmarshaller();

			marshaller = jaxbContext.createMarshaller();
			// marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new TSLNamespacePrefixMapper());
		} catch (JAXBException ex) {
			LOG.error(null, ex);
		}

		objectFactoryTSL = new ObjectFactory();
		objectFactoryECC = new eu.europa.ec.markt.tsl.jaxb.ecc.ObjectFactory();

		validationAssociations = new HashMap<Object, TreeNode[]>();
		TSL_VERSION_IDENTIFIER = new BigInteger(Configuration.getInstance().getTSL().getTslVersionIdentifier());
	}

	/**
	 * If there is any previously loaded/created tsl, this method return true. False otherwise.
	 *
	 * @return true, if there is a list
	 */
	public boolean doesAnyTSLExist() {
		final boolean anyTSLExists = tsl != null;
		return anyTSLExists;
	}

	private void resetEnv() {
		signatureRemovedFromLastList = false; // there is no signature for a new list
		clearValidationAssociations(); // clear any previous associations; they will be recreated in createNodes()
		treeSorted = false;
	}

	/**
	 * Create a new TSL.
	 */
	public void newTSL() {
		tsl = objectFactoryTSL.createTrustStatusListType();

		resetEnv();

		try {
			ObjectFiller.fillAll(tsl);
		} catch (FillerException fe) {
			LOG.warn("An error occurred while filling objects for a new TSL! " + fe.getMessage(), fe);
		}

		// reset currentFile
		currentFile = null;

		setChanged();
		notifyObservers(new TSLComposerEvent(TSLComposerEvent.NEW_TSL, null, getTsl()));
	}

	private void clearValidationAssociations() {
		validationAssociations.clear();
	}

	/**
	 * @return the validationAssociations
	 */
	public Map<Object, TreeNode[]> getValidationAssociations() {
		return validationAssociations;
	}

	/**
	 * Adds an object as key and a {@code TreeNode} array as value to the map associations between jaxb objects and treenodes.
	 *
	 * @param obj  the object (jaxb object)
	 * @param path the path
	 */
	public void addValidationAssociation(Object obj, TreeNode[] path) {
		validationAssociations.put(obj, path);
	}

	/**
	 * @return the current file
	 */
	public File getCurrentFile() {
		return currentFile;
	}

	/**
	 * Opens a specified file and tries to parse a tsl from it.
	 *
	 * @param inputFile the input file
	 * @throws eu.europa.ec.markt.tlmanager.core.exception.OpenException
	 */
	public void openFile(File inputFile) throws OpenException {

		FileInputStream inputStream = null;
		String userMessage = uiKeys.getString("TSLComposer.open.error.error") + " ";
		resetEnv();
		try {

			inputStream = new FileInputStream(inputFile);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser parser = spf.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			WhitespaceFilterXML inFilter = new WhitespaceFilterXML();
			inFilter.setParent(reader);

			InputSource is = new InputSource(inputStream);
			SAXSource source = new SAXSource(inFilter, is);

			// ValidationEventCollector vec = new ValidationEventCollector();
			// unmarshaller.setEventHandler(vec);
			// unmarshaller.setSchema(...)
			JAXBElement<TrustStatusListType> jaxbElement = (JAXBElement<TrustStatusListType>) unmarshaller.unmarshal(source);

			final boolean conversionPossible = isConversionPossible(jaxbElement.getValue());
			if (conversionPossible) {

				final Migration migration = new Migration(jaxbElement);
				final boolean hasChanged = migration.migrate();
				fileMigrated = hasChanged;
				migrationMessages = migration.getMigrationMessages();
			} else {

				fileMigrated = false;
				migrationMessages = null;
				throw new OpenException(uiKeys.getString("TSLComposer.open.error.unableToRead"));
			}

			// ValidationEvent[] events = vec.getEvents();
			// if (events.length != 0) {
			// // any schema validation ?
			// }

			if (!doesModeFit(jaxbElement.getValue())) { // don't proceed
				String mode = " 'tl";
				if (!Configuration.getInstance().isTlMode()) {
					mode = " 'lotl";
				}
				if (Configuration.getInstance().isEuMode()) {
					mode += "/EU'. ";
				} else {
					mode += "/NON-EU'. ";
				}

				String resultMessage = uiKeys.getString("TSLComposer.open.error.wrongMode1") + mode +
						uiKeys.getString("TSLComposer.open.error.wrongMode2") +
						"\n'" + Configuration.getInstance().getTSL().getTslType() + "/" + (Configuration.getInstance().isEuMode() ? "EU" : "NON-EU") + "'";

				throw new OpenException(resultMessage);
			}

			tsl = jaxbElement.getValue();
			if (!isFileMigrated()) {
				currentFile = inputFile;
			} else {
				currentFile = null;
			}

			if (tsl.getSignature() != null) {
				tsl.setSignature(null);
				signatureRemovedFromLastList = true;
			}

			// ensure that the tsl contains everything that is needed to work with it
			ObjectFiller.fillAll(tsl);

			doCreateNodesForData = true;

			setChanged();
			notifyObservers(new TSLComposerEvent(TSLComposerEvent.NEW_TSL, null, tsl));
		} catch (UnmarshalException uex) {
			if ((uex.getMessage() != null) && !uex.getMessage().isEmpty()) {
				userMessage += uex.getMessage();
			} else {
				userMessage += "UnmarshalException: ";
			}
			userMessage += uiKeys.getString("TSLComposer.open.error.unableToRead");
			//            if (uex.getLinkedException() != null) {
			//                userMessage += uiKeys.getString("TSLComposer.open.error.details")+" "
			//                    + uex.getLinkedException().getMessage();
			//            }
			LOG.error(userMessage, uex);
			throw new OpenException(userMessage, uex);
		} catch (Exception ex) {
			userMessage += ex.getMessage();
			if (ex.getCause() != null) {
				userMessage += uiKeys.getString("TSLComposer.open.error.details") + " " + ex.getCause().getMessage();
			}
			LOG.error(userMessage, ex);
			throw new OpenException(userMessage, ex);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				LOG.error(null, ex);
			}
		}
	}

	private boolean isConversionPossible(TrustStatusListType trustStatusListType) {

		final BigInteger tslVersionIdentifier = trustStatusListType.getSchemeInformation().getTSLVersionIdentifier();
		final boolean conversionPossible = tslVersionIdentifier.equals(Migration.TSL_VERSION_SOURCE) || tslVersionIdentifier.equals(Migration.TSL_VERSION_SOURCE_2);
		return conversionPossible;
	}

	/**
	 * Verify that the 'TSLType' of the loaded list fits to the current mode of the application. If not, false is returned. If there is not even a valid schemeInformation object
	 * set, the loaded xml can still be anything and will be set to the current mode.
	 *
	 * @param tsl the {@code TrustStatusListType} to check
	 * @return true, if the list fits to the mode and may be shown
	 */
	private boolean doesModeFit(TrustStatusListType tsl) {
		if (tsl.getSchemeInformation() != null) {
			final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
			final String tslType = schemeInformation.getTSLType().trim();
			final String schemeTerritory = schemeInformation.getSchemeTerritory();

			if (Configuration.getInstance().isEuMode()) {
				final boolean modeFits = tslType.equals(Configuration.getInstance().getTSL().getTslType());
				return modeFits;
			} else {
				// file is EU and application is in EU mode
				String tslTypePattern = Configuration.getInstance().getTSL().getTslType();
				String tslTypeExpected = tslTypePattern.replaceAll("#CC#", schemeTerritory);
				final boolean modeFits = tslType.equals(tslTypeExpected);
				return modeFits;
			}
		}
		return true;
	}

	/**
	 * This state is true, if a new {@code TrustStatusListType} was created (possibly due to loading a whole list with {@link #openFile(java.io.File)}) and all tree nodes have to
	 * be created. Or if some 'jaxb-data' objects were create and appropriate tree nodes have to be created.
	 *
	 * @return true if tree nodes have to be created
	 */
	public boolean isDoCreateNodesForData() {
		return doCreateNodesForData;
	}

	/**
	 * Create nodes recursively depending on what data is available in the initially provided {@code TrustStatusListType}. It will be ensured, that no node is created as a child
	 * of
	 * a node with the same type. The associations between created node's (their paths) and the user objects is maintained in a separate map.
	 *
	 * @param node     the parent node
	 * @param treePart the current section of the tree
	 */
	public void createNodes(DefaultMutableTreeNode node, Object treePart) {
		if (doCreateNodesForData) {
			doCreateNodesForData = false;
		}
		if (treePart instanceof TrustStatusListType) {
			TrustStatusListType trustStatusList = (TrustStatusListType) treePart;
			TrustServiceProviderListType trustServiceProviderList = trustStatusList.getTrustServiceProviderList();
			if (trustServiceProviderList != null) {
				for (TSPType tsp : trustServiceProviderList.getTrustServiceProvider()) {
					createNodes(node, tsp);
				}
			}
			OtherTSLPointersType pointersToOtherTSL = trustStatusList.getSchemeInformation().getPointersToOtherTSL();
			if (pointersToOtherTSL != null) {
				for (OtherTSLPointerType pointer : pointersToOtherTSL.getOtherTSLPointer()) {
					createNodes(node, pointer);
				}
			}
		} else if (treePart instanceof OtherTSLPointerType) {
			OtherTSLPointerType otherTSLPointer = (OtherTSLPointerType) treePart;
			PointerNode newChild = new PointerNode(otherTSLPointer);
			if (!(node instanceof PointerNode)) {

				node.add(newChild);
				addValidationAssociation(otherTSLPointer, newChild.getPath());
			}

		} else if (treePart instanceof TSPType) {
			TSPType tspType = (TSPType) treePart;
			TSPNode newChild = new TSPNode(tspType);
			if (node instanceof TSPNode) {
				newChild = (TSPNode) node;
			} else {
				node.add(newChild);
				addValidationAssociation(tspType, newChild.getPath());
			}
			TSPServicesListType tspServices = tspType.getTSPServices();
			if (tspServices != null) {
				for (TSPServiceType service : tspServices.getTSPService()) {
					createNodes(newChild, service);
				}
			}
		} else if (treePart instanceof TSPServiceType) {
			TSPServiceType tspService = (TSPServiceType) treePart;
			ServiceNode newChild = new ServiceNode(tspService);
			if (node instanceof ServiceNode) {
				newChild = (ServiceNode) node;
			} else {
				node.add(newChild);
				addValidationAssociation(tspService, newChild.getPath());
			}
			// extension
			TSPServiceInformationType serviceInformation = tspService.getServiceInformation();
			if (serviceInformation != null) {
				ExtensionsListType serviceInformationExtensions = serviceInformation.getServiceInformationExtensions();
				if (serviceInformationExtensions != null) {
					createNodes(newChild, serviceInformationExtensions);
				}
			}
			// history
			ServiceHistoryType serviceHistory = tspService.getServiceHistory();
			if (serviceHistory != null) {
				for (ServiceHistoryInstanceType history : serviceHistory.getServiceHistoryInstance()) {
					createNodes(newChild, history);
				}
			}
		} else if (treePart instanceof ExtensionsListType) {
			ExtensionsListType extensionsList = (ExtensionsListType) treePart;
			ExtensionNode newChild = new ExtensionNode(extensionsList);
			if (node instanceof ExtensionNode) {
				newChild = (ExtensionNode) node;
			} else {
				node.add(newChild);
				addValidationAssociation(extensionsList, newChild.getPath());
			}
			// special case: if there is an extensiontype with qname of qualificationelementtype ...
			for (ExtensionType extension : extensionsList.getExtension()) {
				List<Object> content = extension.getContent();
				for (Object obj : content) {
					if (obj instanceof JAXBElement<?>) {
						JAXBElement<?> element = (JAXBElement<?>) obj;
						if (element.getName().equals(QNames._Qualifications_QNAME)) {
							QualificationsType qualifications = (QualificationsType) element.getValue();
							for (QualificationElementType qElement : qualifications.getQualificationElement()) {
								createNodes(newChild, qElement);
							}
						}
					}
				}
			}
		} else if (treePart instanceof ServiceHistoryInstanceType) {
			ServiceHistoryInstanceType serviceHistoryInstance = (ServiceHistoryInstanceType) treePart;
			HistoryNode newChild = new HistoryNode(serviceHistoryInstance);
			if (node instanceof HistoryNode) {
				newChild = (HistoryNode) node;
			} else {
				node.add(newChild);
				addValidationAssociation(serviceHistoryInstance, newChild.getPath());
			}
			ExtensionsListType serviceInformationExtensions = serviceHistoryInstance.getServiceInformationExtensions();
			if (serviceInformationExtensions != null) {
				createNodes(newChild, serviceInformationExtensions);
			}
		} else if (treePart instanceof QualificationElementType) {
			QualificationElementType qualificationElement = (QualificationElementType) treePart;
			QualificationNode newChild = new QualificationNode(qualificationElement);
			node.add(newChild);
			addValidationAssociation(qualificationElement, newChild.getPath());
		}
	}

	/**
	 * Sets the file on which {@link #save()} will operate.
	 *
	 * @param outputFile the file to save to
	 * @throws eu.europa.ec.markt.tlmanager.core.exception.SaveException
	 */
	public void saveToFile(File outputFile) throws SaveException {
		currentFile = outputFile;

		try {
			save();
		} catch (SaveException ex) {
			LOG.error("Saving failed! " + ex.getMessage(), ex);
			throw ex;
		}
	}

	/**
	 * Calls {@link #marshall()} to create the actual {@code Document} and saves it to the filesystem.
	 *
	 * @throws eu.europa.ec.markt.tlmanager.core.exception.SaveException
	 */
	public void save() throws SaveException {

		if (currentFile == null) {

			final String message = uiKeys.getString("TSLComposer.saving.error.error");
			LOG.error(message, new FileNotFoundException());
			throw new SaveException(message);
		}

		Document document;
		try {

			document = marshall();
		} catch (Exception ex) {
			final String message = uiKeys.getString("TSLComposer.saving.error.marshalling") + " " + ex.getMessage();
			LOG.error(message, ex);
			throw new SaveException(message, ex);
		}

		LOG.debug("Saving {}", new Object[]{currentFile.getAbsolutePath()});

		if (treeSorted) {
			Util.sortChildNodes(document.getDocumentElement(), true);
		}

		try {

			Util.prettySave(document, currentFile);
		} catch (Exception ex) {
			final String message = uiKeys.getString("TSLComposer.saving.error.saving") + " " + ex.getMessage();
			LOG.error(message, ex);
			throw new SaveException(message, ex);
		}
	}

	/**
	 * Marshalls the current {@code TrustStatusListType} into a {@code Document}.
	 *
	 * @return the marshalled document
	 * @throws javax.xml.bind.JAXBException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public Document marshall() throws JAXBException, ParserConfigurationException {
		TrustStatusListType trustStatusList = getTsl();

		// TSLTag
		trustStatusList.setTSLTag(Configuration.getInstance().getTSL().getTslTag());

		// TSL version identifier.
		TSLSchemeInformationType schemeInformation = trustStatusList.getSchemeInformation();
		if (schemeInformation == null) { // can't be null ...
			final String defaultTslType = Configuration.getInstance().getTSL().getTslType();
			schemeInformation = objectFactoryTSL.createTSLSchemeInformationType();
			schemeInformation.setTSLType(defaultTslType);
			trustStatusList.setSchemeInformation(schemeInformation);
		}
		schemeInformation.setTSLVersionIdentifier(TSL_VERSION_IDENTIFIER);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();

		JAXBElement<TrustStatusListType> tslElement = objectFactoryTSL.createTrustServiceStatusList(trustStatusList);

		recreateNextUpdateNode(tslElement);

		marshaller.marshal(tslElement, document);
		// marshaller.marshal(tslElement, System.out);

		Util.removeEmptyNodesFromTheBottom(document.getDocumentElement());

		return document;
	}

	private void recreateNextUpdateNode(JAXBElement<TrustStatusListType> tslElement) {
		TSLSchemeInformationType schemeInformation = tslElement.getValue().getSchemeInformation();
		if (schemeInformation.getNextUpdate() == null) {
			NextUpdateType nextUpdateType = objectFactoryTSL.createNextUpdateType();
			schemeInformation.setNextUpdate(nextUpdateType);
		}
	}

	// private boolean isSignatureValid(Document document) throws XMLSignatureException, MarshalException {
	// Element rootElement = document.getDocumentElement();
	//
	// NodeList signatureNodeList = rootElement.getElementsByTagNameNS(
	// XMLSignature.XMLNS, QNames._Signature_QNAME.getLocalPart());
	// if (signatureNodeList != null && signatureNodeList.getLength() > 0) {
	// // expect only one ...
	// Element signatureEl = (Element) signatureNodeList.item(0);
	//
	// DOMValidateContext valContext = new DOMValidateContext(new Util.KeyInfoKeySelector(), signatureEl);
	// XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
	// XMLSignature signature = factory.unmarshalXMLSignature(valContext);
	//
	// boolean isSignatureValid = signature.validate(valContext);
	// if (!isSignatureValid) {
	// // remove signature node
	// signatureNodeList = null;
	// }
	// }
	//
	// return true; // empty signature is always valid
	// }

	/**
	 * Starts the validation
	 *
	 * @return the resulting {@code ValidationLogger}
	 */
	public ValidationLogger startValidation() {
		// setup validation parameters
		ValidationParameters vp = new ValidationParameters();
		vp.setListIsClosed(mainFrame.isListClosed());

		Validation validation = new Validation(vp, getTsl());
		validationLogger = validation.validate();

		return validationLogger;
	}

	/**
	 * @return the validationLogger
	 */
	public ValidationLogger getValidationLogger() {
		return validationLogger;
	}

	/**
	 * @param validationLogger the validationLogger to set
	 */
	public void setValidationLogger(ValidationLogger validationLogger) {
		this.validationLogger = validationLogger;
	}

	/**
	 * @return the signatureRemovedFromLastList
	 */
	public boolean isSignatureRemovedFromLastList() {
		return signatureRemovedFromLastList;
	}

	/**
	 * @return the fileMigrated
	 */
	public boolean isFileMigrated() {
		return fileMigrated;
	}

	public Migration.MigrationMessages getMigrationMessages() {
		return migrationMessages;
	}

	/**
	 * Returns the current TSL. If there is none, a new one is created and filled automatically.
	 *
	 * @return the current tsl
	 */
	public TrustStatusListType getTsl() {
		if (tsl.getSchemeInformation() == null) {
			final String defaultTslType = Configuration.getInstance().getTSL().getTslType();
			TSLSchemeInformationType info = objectFactoryTSL.createTSLSchemeInformationType();
			info.setTSLType(defaultTslType);
			tsl.setSchemeInformation(info);

			try {
				ObjectFiller.fillTSLSchemeInformationType(tsl.getSchemeInformation());
			} catch (FillerException fex) {
				LOG.error(">>>Error during filling Objects: " + fex.getMessage(), fex);
			}
		}

		return tsl;
	}

	/**
	 * Creates (if necessary) and returns a list of {@code OtherTSLPointerType}.
	 *
	 * @return list of {@code OtherTSLPointerType}
	 */
	public List<OtherTSLPointerType> getPointersToOtherTSL() {
		TrustStatusListType currentTsl = getTsl();
		OtherTSLPointersType list = currentTsl.getSchemeInformation().getPointersToOtherTSL();
		if (list == null) {
			list = objectFactoryTSL.createOtherTSLPointersType();
			currentTsl.getSchemeInformation().setPointersToOtherTSL(list);
		}

		return list.getOtherTSLPointer();
	}

	/**
	 * Creates (if necessary) and returns a list of {@code TSPType}.
	 *
	 * @return list of {@code TSPType}
	 */
	public List<TSPType> getTSPList() {
		TrustServiceProviderListType list = getTsl().getTrustServiceProviderList();
		if (list == null) {
			list = objectFactoryTSL.createTrustServiceProviderListType();
			tsl.setTrustServiceProviderList(list);
		}
		List<TSPType> providers = list.getTrustServiceProvider();
		LOG.info("{} TSP provider(s) {}", new Object[]{providers.size(), providers});

		return providers;
	}

	/**
	 * Creates (if necessary) and returns a list of {@code TSPServiceType}.
	 *
	 * @return list of {@code TSPServiceType}
	 */
	public List<TSPServiceType> getServices(TSPType tsp) {
		TSPServicesListType list = tsp.getTSPServices();
		if (list == null) {
			list = objectFactoryTSL.createTSPServicesListType();
			tsp.setTSPServices(list);
		}
		List<TSPServiceType> services = list.getTSPService();
		LOG.info("{} services for provider {}", new Object[]{services.size(), tsp});

		return services;
	}

	private TSPType createTSP() throws FillerException {
		TSPType tsp = objectFactoryTSL.createTSPType();

		TSPInformationType info = objectFactoryTSL.createTSPInformationType();

		tsp.setTSPInformation(ObjectFiller.fillTSPInformationType(info));

		getServices(tsp).add(createService()); // A new TSP must have at least one Service

		return tsp;
	}

	private TSPServiceType createService() throws FillerException {
		TSPServiceType service = objectFactoryTSL.createTSPServiceType();

		// tsl:TSPServiceInformationType
		TSPServiceInformationType info = objectFactoryTSL.createTSPServiceInformationType();
		service.setServiceInformation(ObjectFiller.fillTSPServiceInformationType(info));

		// tsl:ServiceHistoryType
		ServiceHistoryType serviceHistory = objectFactoryTSL.createServiceHistoryType();
		service.setServiceHistory(serviceHistory);

		return service;
	}

	/**
	 * Creates an {@code OtherTSLPointerType} and fire a {@code TSLComposerEvent} to wrap it in an appropriate {@code PointerNode}. This is done in {@code TSLTreeModel}.
	 *
	 * @param parent the parent to add the new object to
	 */
	public void addPointerToOtherTSL(TSLRootNode parent) {
		try {
			OtherTSLPointerType pointer = objectFactoryTSL.createOtherTSLPointerType();

			getPointersToOtherTSL().add(ObjectFiller.fillOtherTSPPointerType(pointer));

			setChanged();
			notifyObservers(new TSLComposerEvent(TSLComposerEvent.ADD_POINTER, parent, pointer));
		} catch (FillerException fex) {
			LOG.error(">>>Unable to add PointerToOtherTSL, because of: " + fex.getMessage(), fex);
		}
	}

	/**
	 * Creates a {@code TSPType} and fire a {@code TSLComposerEvent} to wrap it in an appropriate {@code TSPNode}. This is done in {@code TSLTreeModel}.
	 *
	 * @param parent the parent to add the new object to
	 */
	public void addTSP(TSLRootNode parent) {
		try {
			TSPType tsp = createTSP();
			getTSPList().add(tsp);

			setChanged();
			notifyObservers(new TSLComposerEvent(TSLComposerEvent.ADD_TSP, parent, tsp));
		} catch (FillerException fex) {
			LOG.error(">>>Unable to add TSP, because of: " + fex.getMessage(), fex);
		}
	}

	/**
	 * Creates a {@code TSPServiceType} and fire a {@code TSLComposerEvent} to wrap it in an appropriate {@code ServiceNode}. This is done in {@code TSLTreeModel}.
	 *
	 * @param parent the parent to add the new object to
	 */
	public void addServiceToTSP(TSPNode parent) {
		TSPType tsp = parent.getUserObject();
		try {
			TSPServiceType service = createService();
			getServices(tsp).add(service);

			setChanged();
			notifyObservers(new TSLComposerEvent(TSLComposerEvent.ADD_SERVICE, parent, service));
		} catch (FillerException fex) {
			LOG.error(">>>Unable to add Service, because of: " + fex.getMessage(), fex);
		}
	}

	/**
	 * Creates a {@code ServiceHistoryInstanceType} and fire a {@code TSLComposerEvent} to wrap it in an appropriate {@code HistoryNode}. This is done in {@code TSLTreeModel}. If
	 * the reference service does not have a set service type identifier, the user is informed. Any object that is below the reference service in the object hierarchy is copied
	 * and
	 * appended to the new {@code ServiceHistoryInstanceType}.
	 *
	 * @param parent the parent to add the new object to
	 */
	public void addStatusHistory(ServiceNode parent) {

		TSPServiceType service = parent.getUserObject();
		TSPServiceInformationType serviceInformation = service.getServiceInformation();
		if ((serviceInformation.getServiceTypeIdentifier() == null) || serviceInformation.getServiceTypeIdentifier().equals(Util.DEFAULT_NO_SELECTION_ENTRY) || serviceInformation
				.getServiceDigitalIdentity().getDigitalId().isEmpty()) {
			// inform the user, but don't do anything about it...
			mainFrame.notifyUser(uiKeys.getString("MainFrame.createHistoryDialog.message"), uiKeys.getString("MainFrame.createHistoryDialog.title"));
			return;
		}

		Values newStatusValues = mainFrame.queryForNewServiceStatusValues();
		if (newStatusValues != null) {
			try {
				// do a deep copy of all the extensions
				ExtensionsListType serviceInformationExtensions = serviceInformation.getServiceInformationExtensions();
				Object deepCopySIE = Util.deepCopy(serviceInformationExtensions);
				ExtensionsListType copiedExtensionList = (ExtensionsListType) deepCopySIE;

				// ServiceHistoryInstance
				ServiceHistoryInstanceType history = objectFactoryTSL.createServiceHistoryInstanceType();

				ServiceHistoryType serviceHistory = service.getServiceHistory();
				if (serviceHistory == null) {
					serviceHistory = objectFactoryTSL.createServiceHistoryType();
					service.setServiceHistory(serviceHistory);
				}
				ObjectFiller.fillServiceHistoryInstanceType(history);
				serviceHistory.getServiceHistoryInstance().add(0, history);
				history.setServiceInformationExtensions(copiedExtensionList);
				doCreateNodesForData = true;

				// cannot be null or empty - this is checked above
				history.setServiceTypeIdentifier(serviceInformation.getServiceTypeIdentifier());
				history.setServiceStatus(serviceInformation.getServiceStatus());
				XMLGregorianCalendar statusStartingTime = serviceInformation.getStatusStartingTime();
				if (statusStartingTime != null) {
					statusStartingTime = (XMLGregorianCalendar) statusStartingTime.clone();
				}
				history.setStatusStartingTime(statusStartingTime);
				history.setServiceName((InternationalNamesType) Util.deepCopy(serviceInformation.getServiceName()));

				// do a deep copy of the service digital identity
				DigitalIdentityListType serviceDigitalIdentity = serviceInformation.getServiceDigitalIdentity();
				Object deepCopySDI = Util.deepCopy(serviceDigitalIdentity);
				DigitalIdentityListType copiedDigitalIdentity = (DigitalIdentityListType) deepCopySDI;

				// we used to get rid of everything than the certificate, extract the subject name and create a digitalidentity for it - keep only this for the history.

				history.setServiceDigitalIdentity(copiedDigitalIdentity);

				// now current status and date have been copied from service: set new values
				serviceInformation.setStatusStartingTime(newStatusValues.getDate());
				serviceInformation.setServiceStatus(newStatusValues.getStatus());

				setChanged();
				notifyObservers(new TSLComposerEvent(TSLComposerEvent.ADD_HISTORY, parent, history));
			} catch (FillerException fex) {
				LOG.error(">>>Unable to add StatusHistory, because of: " + fex.getMessage(), fex);
			} catch (Exception ex) {
				LOG.error(">>>Unable to add StatusHistory, because of: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Creates an {@code ExtensionsListType} and fire a {@code TSLComposerEvent} to wrap it in an appropriate {@code ExtensionNode}. This is done in {@code TSLTreeModel}.
	 *
	 * @param parent the parent to add the new object to
	 */
	public void addInformationExtension(ServiceNode parent) {
		TSPServiceType service = parent.getUserObject();
		try {
			ExtensionsListType extensionsList = objectFactoryTSL.createExtensionsListType();
			service.getServiceInformation().setServiceInformationExtensions(ObjectFiller.fillExtensionsListType(extensionsList));

			setChanged();
			notifyObservers(new TSLComposerEvent(TSLComposerEvent.ADD_INFOEXTENSION, parent, extensionsList));
		} catch (FillerException fex) {
			LOG.error(">>>Unable to add InformationExtension, because of: " + fex.getMessage(), fex);
		}
	}

	/**
	 * Creates an {@code OtherTSLPointerType} and fire a {@code TSLComposerEvent} to wrap it in an appropriate {@code PointerNode}. This is done in {@code TSLTreeModel}.
	 *
	 * @param parent the parent to add the new object to
	 */
	public void addInformationExtension(HistoryNode parent) {
		ServiceHistoryInstanceType history = parent.getUserObject();
		try {
			ExtensionsListType extensionsList = objectFactoryTSL.createExtensionsListType();
			history.setServiceInformationExtensions(ObjectFiller.fillExtensionsListType(extensionsList));

			setChanged();
			notifyObservers(new TSLComposerEvent(TSLComposerEvent.ADD_INFOEXTENSION, parent, extensionsList));
		} catch (FillerException fex) {
			LOG.error(">>>Unable to add InformationExtension, because of: " + fex.getMessage(), fex);
		}
	}

	/**
	 * Creates an {@code QualificationsType} and fire a {@code TSLComposerEvent} to wrap it in an appropriate {@code QualificationNode}. This is done in {@code TSLTreeModel}.
	 * Note:
	 * There is no clear separation between extensions and qualifications! Any qualification is actually also another extension. But it has an own node dedicated to it.
	 *
	 * @param parent the parent to add the new object to
	 */
	public void addQualificationExtension(ExtensionNode parent) {
		ExtensionsListType extensionList = parent.getUserObject();
		// determine if there is already an extensiontype with a content of a QualificationElementType
		QualificationsType qualifications = null;
		// ExtensionType parentExtension = null;
		List<ExtensionType> extensions = extensionList.getExtension();

		for (ExtensionType extension : extensions) {
			JAXBElement<?> element = Util.extractJAXBElement(extension);
			if ((element != null) && QNames._Qualifications_QNAME.equals(element.getName())) {
				JAXBElement<QualificationsType> qel = (JAXBElement<QualificationsType>) element;
				qualifications = qel.getValue();
				// parentExtension = extension;
				break;
			}
		}

		if (qualifications == null) {
			qualifications = objectFactoryECC.createQualificationsType();
			JAXBElement<QualificationsType> element = objectFactoryECC.createQualifications(qualifications);
			ExtensionType extensionType = objectFactoryTSL.createExtensionType();
			extensionType.getContent().add(element);
			extensions.add(extensionType);
			// parentExtension = extensionType;
		}

		try {
			// now create the actual QualificationElementType
			QualificationElementType qualificationElement = ObjectFiller.fillQualificationElement(objectFactoryECC.createQualificationElementType());
			qualifications.getQualificationElement().add(qualificationElement);

			setChanged();
			notifyObservers(new TSLComposerEvent(TSLComposerEvent.ADD_QUALIFICATIONEXTENSION, parent, qualificationElement));
		} catch (FillerException fex) {
			LOG.error(">>>Unable to add QualificationExtension, because of: " + fex.getMessage(), fex);
		}
	}

	/**
	 * Removes all user objects from the map of associations between validation results and tree user objects.
	 *
	 * @param first the node to start
	 */
	public void removeAllUserObjectsFromAssociatedMap(DefaultMutableTreeNode first) {
		Enumeration enumeration = first.breadthFirstEnumeration();
		List<Object> userObjects = new ArrayList<Object>();

		while (enumeration.hasMoreElements()) {
			Object nextElement = enumeration.nextElement();
			DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) nextElement;
			userObjects.add(dmtn.getUserObject());
		}

		for (Object uo : userObjects) {
			validationAssociations.remove(uo);
		}

		if ((validationLogger != null) && validationLogger.removeMessagesForObjects(userObjects)) {
			mainFrame.updateLogDialog(validationLogger.getMessages());
		}
	}

	/**
	 * Removes a node from the object hierarchy. Note: null checks for parent objects are omitted, because objects which constitute the path to the 'existing' children to be
	 * deleted cannot be null.
	 *
	 * @param node the selected object
	 */
	public void removeNode(TSLDataNode node) {
		if (!mainFrame.queryUser(uiKeys.getString("MainFrame.removeNodeDialog.message"), uiKeys.getString("MainFrame.removeNodeDialog.title"))) {
			return;
		}
		Object childrenObject = node.getUserObject();
		TSLDataNode parentNode = (TSLDataNode) node.getParent();
		Object parentObject = parentNode.getUserObject();

		if (childrenObject instanceof TrustStatusListType) {
		} else if (childrenObject instanceof OtherTSLPointerType) {
			TrustStatusListType parent = (TrustStatusListType) parentObject;
			List<OtherTSLPointerType> list = parent.getSchemeInformation().getPointersToOtherTSL().getOtherTSLPointer();
			list.remove(childrenObject);

		} else if (childrenObject instanceof TSPType) {
			TrustStatusListType parent = (TrustStatusListType) parentObject;
			List<TSPType> list = parent.getTrustServiceProviderList().getTrustServiceProvider();
			list.remove(childrenObject);

		} else if (childrenObject instanceof TSPServiceType) {
			TSPType parent = (TSPType) parentObject;
			List<TSPServiceType> list = parent.getTSPServices().getTSPService();
			list.remove(childrenObject);

		} else if (childrenObject instanceof ExtensionsListType) {
			if (parentObject instanceof TSPServiceType) {
				TSPServiceType parent = (TSPServiceType) parentObject;
				parent.getServiceInformation().setServiceInformationExtensions(null);

			} else if (parentObject instanceof ServiceHistoryInstanceType) {
				ServiceHistoryInstanceType parent = (ServiceHistoryInstanceType) parentObject;
				parent.setServiceInformationExtensions(null);
			}

		} else if (childrenObject instanceof ServiceHistoryInstanceType) {
			TSPServiceType parent = (TSPServiceType) parentObject;
			List<ServiceHistoryInstanceType> list = parent.getServiceHistory().getServiceHistoryInstance();
			list.remove(childrenObject);

		} else {
			if (childrenObject instanceof QualificationElementType) {
				ExtensionsListType parent = (ExtensionsListType) parentObject;
				boolean lastQualifierRemoved = false;
				QualificationElementType element = (QualificationElementType) childrenObject;
				List<ExtensionType> extractMatching = Util.extractMatching(parent, QNames._Qualifications_QNAME.getLocalPart(), false);
				for (ExtensionType extension : extractMatching) {
					for (Object obj : extension.getContent()) {
						if (obj instanceof JAXBElement<?>) {
							JAXBElement<QualificationsType> jaxbElement = (JAXBElement<QualificationsType>) obj;
							QualificationsType qualifications = jaxbElement.getValue();
							qualifications.getQualificationElement().remove(element);
							lastQualifierRemoved = qualifications.getQualificationElement().isEmpty();
							break;
						}
					}
				}
				if (lastQualifierRemoved) { // remove unneeded objects
					parent.getExtension().removeAll(extractMatching);
				}
			}
		}

		setChanged();
		notifyObservers(new TSLComposerEvent(TSLComposerEvent.REMOVE_NODE, (MutableTreeNode) node, null));
	}

	/**
	 * Sorts the children of the given node
	 *
	 * @param node the selected object
	 */
	public void sortChildren(TSLDataNode node) {
		treeSorted = true;
		node.sort();

		setChanged();
		notifyObservers(new TSLComposerEvent(TSLComposerEvent.SORT_NODE, (MutableTreeNode) node, null));
	}
}