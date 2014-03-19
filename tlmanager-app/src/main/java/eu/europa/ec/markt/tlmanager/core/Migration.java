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

package eu.europa.ec.markt.tlmanager.core;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationElementType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationsType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualifierType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualifiersType;
import eu.europa.ec.markt.tsl.jaxb.tsl.AdditionalServiceInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.AddressType;
import eu.europa.ec.markt.tsl.jaxb.tsl.AnyType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionsListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.InternationalNamesType;
import eu.europa.ec.markt.tsl.jaxb.tsl.MultiLangNormStringType;
import eu.europa.ec.markt.tsl.jaxb.tsl.MultiLangStringType;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIType;
import eu.europa.ec.markt.tsl.jaxb.tsl.OtherTSLPointerType;
import eu.europa.ec.markt.tsl.jaxb.tsl.OtherTSLPointersType;
import eu.europa.ec.markt.tsl.jaxb.tsl.PolicyOrLegalnoticeType;
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
 * Convert the jaxbElement in the new version. Changes URI to the new ones when possible <p/> DISCLAIMER: Project owner DG-MARKT.
 *
 * @author <a href="mailto:dgmarkt.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1016 $ - $Date: 2011-06-17 15:30:45 +0200 (Fri, 17 Jun 2011) $
 */
public class Migration {

    private static final Logger LOG = LoggerFactory.getLogger(Migration.class);

    public static final BigInteger TSL_VERSION_SOURCE = BigInteger.valueOf(3);
    public static final BigInteger TSL_VERSION_SOURCE_2 = BigInteger.valueOf(4);
    public static final BigInteger TSL_VERSION_TARGET = BigInteger.valueOf(4);
    public static final String DEFAULT_LANG = "en";
    private final MigrationMessages migrationMessages = new MigrationMessages();
    private final TrustStatusListType tsl;
    // Indicates if the migration have changed something.
    private boolean hasChanged = false;

    public static class MigrationMessages {

        private final List<String> messages = new ArrayList<String>();

        public MigrationMessages addMessage(String message) {

            messages.add(message);
            return this;
        }

        public List<String> getMessages() {

            return Collections.unmodifiableList(messages);
        }
    }

    /**
     * Constructor for the migration
     *
     * @param jaxbElement the tsl document to be migrated
     */
    public Migration(JAXBElement<TrustStatusListType> jaxbElement) {
        this.tsl = jaxbElement.getValue();
    }

    /**
     * Migrates the current document to the new version. Document will be changed.
     */
    public boolean migrate() {

        updateTSLVersionIdentifier();
        updateTSLType();
        updateSchemaName();
        updatePolicyOrLegalNotice();
        updateStatusDeterminationApproach();
        updateTSP();
        updatePointers();
        updateSchemeTypeCommunityRules();

        return hasChanged;
    }

    private void updateTSLVersionIdentifier() {

        final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
        if (schemeInformation != null) {
            final BigInteger tslVersionIdentifier = schemeInformation.getTSLVersionIdentifier();
            if (!tslVersionIdentifier.equals(TSL_VERSION_TARGET)) {

                schemeInformation.setTSLVersionIdentifier(TSL_VERSION_TARGET);
                LOG.info("MIGRATED: '{}' --> '{}'", tslVersionIdentifier, TSL_VERSION_TARGET);
                hasChanged = true;
            }
        }
    }

    private void updateTSLType() {

        final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
        if (schemeInformation != null) {

            final String tslType = schemeInformation.getTSLType();
            String newTslType = Configuration.getInstance().getTSL().getTslType();
            if ("http://uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/TSLType/generic".equals(tslType)) {

                newTslType = "http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUgeneric";
            } else if ("http://uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/TSLType/schemes".equals(tslType)) {

                newTslType = "http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUlistofthelists";
            }
            if (!tslType.equals(newTslType)) {

                schemeInformation.setTSLType(newTslType);
                LOG.info("MIGRATED: '{}' --> '{}'", tslType, newTslType);
                hasChanged = true;
            }
        }
    }

    private void updateSchemaName() {

        final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
        if (schemeInformation == null) {
            return;
        }
        final InternationalNamesType schemeNameType = schemeInformation.getSchemeName();
        if (schemeNameType == null) {
            return;
        }
        final List<MultiLangNormStringType> schemeNameList = schemeNameType.getName();
        for (final MultiLangNormStringType schemeName : schemeNameList) {

            final String value = schemeName.getValue();
            final String[] split = value.split(":");
            if (split.length < 2) {

                migrationMessages.addMessage("Scheme name not concordant. Should be 'CC:text', there is no ':'.");
                return;
            }
            final Configuration.CountryCodes countryCodes = Configuration.getInstance().getCountryCodes();
            if (!countryCodes.getCodesList().contains(split[0])) {

                migrationMessages.addMessage("Scheme name country code is unknown: '" + split[0] + "'");
                return;
            }
            final String lang = schemeName.getLang();
            if ("en".equals(lang)) {

                final String newValue = split[0] + ":Supervision/Accreditation Status List of certification services from Certification Service Providers, which are supervised/accredited by the referenced Scheme Operator’s Member State for compliance with the relevant provisions laid down in Directive 1999/93/EC of the European Parliament and of the Council of 13 December 1999 on a Community framework for electronic signatures.";
                if (!value.equals(newValue)) {

                    schemeName.setValue(newValue);
                    LOG.info("MIGRATED: '{}' --> '{}'", value, newValue);
                    hasChanged = true;
                }
            }
        }
    }

    private void updatePolicyOrLegalNotice() {

        final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
        if (schemeInformation == null) {
            return;
        }
        final PolicyOrLegalnoticeType policyOrLegalNotice = schemeInformation.getPolicyOrLegalNotice();
        if (policyOrLegalNotice != null) {

            final List<MultiLangStringType> tslLegalNotice = policyOrLegalNotice.getTSLLegalNotice();
            for (final MultiLangStringType multiLangStringType : tslLegalNotice) {

                if ("en".equals(multiLangStringType.getLang())) {

                    final String value = multiLangStringType.getValue();
                    final String newValue = value.replaceFirst("the Directive", "Directive");
                    if (!value.equals(newValue)) {

                        multiLangStringType.setValue(newValue);
                        LOG.info("MIGRATED: '{}' --> '{}'", value, newValue);
                        hasChanged = true;
                    }
                }
            }
        }
    }

    private void updateStatusDeterminationApproach() {

        final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
        if (schemeInformation != null) {

            final String statusDeterminationApproach = schemeInformation.getStatusDeterminationApproach();
            if ("http://uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/StatusDetn/appropriate"
                  .equals(statusDeterminationApproach) || "http://uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/StatusDetn/list".equals(statusDeterminationApproach)) {

                final String newStatusDeterminationApproach = "http://uri.etsi.org/TrstSvc/TrustedList/TSLType/StatusDetn/EUappropriate";
                schemeInformation.setStatusDeterminationApproach(newStatusDeterminationApproach);
                LOG.info("MIGRATED: '{}' --> '{}'", statusDeterminationApproach, newStatusDeterminationApproach);
                hasChanged = true;
            }
        }
    }

    private void updateTSP() {

        final TrustServiceProviderListType trustServiceProviderList = tsl.getTrustServiceProviderList();
        if (trustServiceProviderList == null) {
            return;
        }
        final List<TSPType> trustServiceProvider = trustServiceProviderList.getTrustServiceProvider();
        for (final TSPType tspType : trustServiceProvider) {

            final TSPInformationType tspInformation = tspType.getTSPInformation();
            if (tspInformation != null) {

                final AddressType tspAddress = tspInformation.getTSPAddress();
                if (tspAddress != null && tspAddress.getElectronicAddress() != null && tspAddress.getElectronicAddress().getURI() != null) {

                    final List<NonEmptyMultiLangURIType> addresses = tspAddress.getElectronicAddress().getURI();
                    for (final NonEmptyMultiLangURIType nonEmptyMultiLangURIType : addresses) {

                        if (DSSUtils.isEmpty(nonEmptyMultiLangURIType.getLang())) {

                            nonEmptyMultiLangURIType.setLang(DEFAULT_LANG);
                            LOG.info("MIGRATED: '{}' --> '{}'", "", DEFAULT_LANG);
                            hasChanged = true;
                        }
                    }
                }
            }

            final TSPServicesListType tspServices = tspType.getTSPServices();
            if (tspServices == null) {
                continue;
            }
            final List<TSPServiceType> tspService = tspServices.getTSPService();
            for (TSPServiceType tspServiceType : tspService) {

                final TSPServiceInformationType serviceInformation = tspServiceType.getServiceInformation();
                if (serviceInformation != null) {

                    updateTspCurrentStatus(serviceInformation);
                    updateTspExtensions(serviceInformation);
                }
                updateTspServiceHistory(tspServiceType);
            }
        }
    }

    private void updateTspCurrentStatus(final TSPServiceInformationType serviceInformation) {

        final String serviceStatus = serviceInformation.getServiceStatus();
        final String newServiceStatus = serviceStatus.replaceAll("eSigDir-1999-93-EC-TrustedList", "TrustedList");
        if (!serviceStatus.equals(newServiceStatus)) {

            serviceInformation.setServiceStatus(newServiceStatus);
            LOG.info("MIGRATED: '{}' --> '{}'", serviceStatus, newServiceStatus);
            hasChanged = true;
        }
    }

    private void updateTspExtensions(final TSPServiceInformationType serviceInformation) {

        final ExtensionsListType serviceInformationExtensions = serviceInformation.getServiceInformationExtensions();
        updateTspExtensions(serviceInformationExtensions);
    }

    private void updateTspExtensions(final ExtensionsListType serviceInformationExtensions) {

        // search for qualifiers
        if (serviceInformationExtensions == null) {
            return;
        }
        final List<ExtensionType> extension = serviceInformationExtensions.getExtension();
        for (final ExtensionType extensionType : extension) {

            final List<Object> extensionContentList = extensionType.getContent();
            for (Object extensionContent : extensionContentList) {

                if (extensionContent instanceof JAXBElement) {

                    JAXBElement<?> element = (JAXBElement<?>) extensionContent;
                    final Object elementValue = element.getValue();
                    updateQualifiers(elementValue);
                    updateAdditionalInformationExtension(elementValue);

                }
            }
        }
    }

    private void updateQualifiers(Object elementValue) {

        if (elementValue instanceof QualificationsType) {

            final QualificationsType qualificationsType = (QualificationsType) elementValue;
            final List<QualificationElementType> qualificationElementList = qualificationsType.getQualificationElement();
            for (final QualificationElementType qualificationElementType : qualificationElementList) {

                final QualifiersType qualifiers = qualificationElementType.getQualifiers();
                if (qualifiers != null) {

                    final List<QualifierType> qualifierList = qualifiers.getQualifier();
                    for (final QualifierType qualifier : qualifierList) {

                        final String qualifierUri = qualifier.getUri();
                        final String newQualifierUri = qualifierUri.replaceAll("eSigDir-1999-93-EC-TrustedList", "TrustedList");
                        if (!qualifierUri.equals(newQualifierUri)) {

                            qualifier.setUri(newQualifierUri);
                            LOG.info("MIGRATED: '{}' --> '{}'", qualifierUri, newQualifierUri);
                            hasChanged = true;
                        }
                    }
                }
            }
        }
    }

    private void updateAdditionalInformationExtension(final Object elementValue) {

        if (elementValue instanceof AdditionalServiceInformationType) {

            final AdditionalServiceInformationType additionalServiceInformationType = (AdditionalServiceInformationType) elementValue;
            final NonEmptyMultiLangURIType uri = additionalServiceInformationType.getURI();
            if (uri != null) {

                final String uriValue = uri.getValue();
                final String newUriValue = uriValue.replaceAll("eSigDir-1999-93-EC-TrustedList", "TrustedList");
                if (!uriValue.equals(newUriValue)) {

                    uri.setValue(newUriValue);
                    LOG.info("MIGRATED: '{}' --> '{}'", uriValue, newUriValue);
                    hasChanged = true;
                }
            }
        }
    }

    private void updateTspServiceHistory(final TSPServiceType tspServiceType) {

        final ServiceHistoryType serviceHistory = tspServiceType.getServiceHistory();
        if (serviceHistory == null) {
            return;
        }
        final List<ServiceHistoryInstanceType> serviceHistoryInstance = serviceHistory.getServiceHistoryInstance();
        for (final ServiceHistoryInstanceType serviceHistoryInstanceType : serviceHistoryInstance) {

            final String serviceStatus = serviceHistoryInstanceType.getServiceStatus();
            final String newServiceStatus = serviceStatus.replaceAll("eSigDir-1999-93-EC-TrustedList", "TrustedList");
            if (!serviceStatus.equals(newServiceStatus)) {

                serviceHistoryInstanceType.setServiceStatus(newServiceStatus);
                LOG.info("MIGRATED: '{}' --> '{}'", serviceStatus, newServiceStatus);
                hasChanged = true;
            }
            final ExtensionsListType serviceInformationExtensions = serviceHistoryInstanceType.getServiceInformationExtensions();
            updateTspExtensions(serviceInformationExtensions);
        }
    }

    private void updatePointers() {

        final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
        if (schemeInformation == null) {
            return;
        }
        final OtherTSLPointersType pointersToOtherTSL = schemeInformation.getPointersToOtherTSL();
        if (pointersToOtherTSL == null) {
            return;
        }
        final List<OtherTSLPointerType> otherTSLPointer = pointersToOtherTSL.getOtherTSLPointer();
        for (final OtherTSLPointerType otherTSLPointerType : otherTSLPointer) {

            updateOtherTSLPointerType(otherTSLPointerType);
        }
    }

    private void updateOtherTSLPointerType(OtherTSLPointerType otherTSLPointerType) {

        final JAXBElement<String> tslType = (JAXBElement<String>) getAdditionalDataNode(otherTSLPointerType, QNames._TSLType_QNAME);
        if (tslType == null) {
            return;
        }
        String newTslTypeValue = Configuration.getInstance().getTSL().getTslTypeInverse();
        final String tslTypeValue = tslType.getValue();
        if ("http://uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/TSLType/generic".equals(tslTypeValue)) {
            newTslTypeValue = "http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUgeneric";
        } else if ("http://uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/TSLType/schemes".equals(tslTypeValue)) {
            newTslTypeValue = "http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUlistofthelists";
        }
        if (!tslTypeValue.equals(newTslTypeValue)) {

            tslType.setValue(newTslTypeValue);
            LOG.info("MIGRATED: '{}' --> '{}'", tslTypeValue, newTslTypeValue);
            hasChanged = true;
        }

        final JAXBElement<NonEmptyMultiLangURIListType> schemeTypeCommunityRules = (JAXBElement<NonEmptyMultiLangURIListType>) getAdditionalDataNode(otherTSLPointerType,
              QNames._SchemeTypeCommunityRules_QNAME);
        if (schemeTypeCommunityRules != null) {

            updateSchemeTypeCommunityRules(schemeTypeCommunityRules.getValue());
        }

    }

    private JAXBElement<?> getAdditionalDataNode(final OtherTSLPointerType pointer, final QName qname) {

        final List<Serializable> textualInformationOrOtherInformation = pointer.getAdditionalInformation().getTextualInformationOrOtherInformation();
        for (final Object obj : textualInformationOrOtherInformation) {

            if (obj instanceof AnyType) {

                final AnyType anyType = (AnyType) obj;
                final List<Object> content = anyType.getContent();
                if (content.isEmpty()) {
                    continue;
                }
                JAXBElement<Object> element = null;
                final Object object = content.get(0);
                if (object == null) {
                    continue;
                }
                if (object instanceof JAXBElement<?>) {
                    element = (JAXBElement<Object>) object;
                }
                if (element != null) {

                    if (element.getName().getLocalPart().equals(qname.getLocalPart())) {
                        return (JAXBElement<String>) object;
                    }
                }
            }
        }
        return null;
    }

    private void updateSchemeTypeCommunityRules() {
        final TSLSchemeInformationType schemeInformation = tsl.getSchemeInformation();
        if (schemeInformation == null) {
            return;
        }
        final NonEmptyMultiLangURIListType schemeTypeCommunityRules = schemeInformation.getSchemeTypeCommunityRules();
        updateSchemeTypeCommunityRules(schemeTypeCommunityRules);
    }

    private void updateSchemeTypeCommunityRules(final NonEmptyMultiLangURIListType schemeTypeCommunityRules) {

        if (schemeTypeCommunityRules == null) {
            return;
        }
        final String SCHEME_RULES = "/schemerules/";
        final List<NonEmptyMultiLangURIType> uriList = schemeTypeCommunityRules.getURI();
        final List<NonEmptyMultiLangURIType> newUriList = new ArrayList<NonEmptyMultiLangURIType>();
        for (final NonEmptyMultiLangURIType uri : uriList) {

            final NonEmptyMultiLangURIType newUri = uri;
            if (DSSUtils.isEmpty(newUri.getLang())) {
                newUri.setLang(DEFAULT_LANG);
                LOG.info("MIGRATED: '{}' --> '{}'", "", DEFAULT_LANG);
                hasChanged = true;
            }
            final String uriValue = uri.getValue();
            if (!uriValue.contains("ttp://uri.etsi.org/TrstSvc/TrustedList/schemerules/")) {
                if (uriValue.contains("/common")) {

                    newUri.setValue("http://uri.etsi.org/TrstSvc/TrustedList/schemerules/EUcommon");
                    LOG.info("MIGRATED: '{}' --> '{}'", uriValue, newUri.getValue());
                    hasChanged = true;
                } else if (uriValue.contains("/CompiledList")) {

                    newUri.setValue("http://uri.etsi.org/TrstSvc/TrustedList/schemerules/EUlistofthelists");
                    LOG.info("MIGRATED: '{}' --> '{}'", uriValue, newUri.getValue());
                    hasChanged = true;
                } else if (uriValue.contains(SCHEME_RULES)) {
                    // final Pattern pattern = Pattern.compile("^http://uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/schemerules/([A-Z][A-Z])$");
                    // final Matcher matcher = pattern.matcher(uriValue);
                    // if (matcher.matches()) {
                    //
                    // final String country = matcher.group(1);
                    // newUri.setValue("http://uri.etsi.org/TrstSvc/TrustedList/schemerules/" + country);
                    // LOG.info("MIGRATED: '{}' --> '{}'", uriValue, newUri.getValue());
                    // hasChanged = true;
                    // }
                    final int begin = uriValue.indexOf(SCHEME_RULES) + SCHEME_RULES.length();
                    final String country = uriValue.substring(begin, begin + 2);
                    newUri.setValue("http://uri.etsi.org/TrstSvc/TrustedList/schemerules/" + country);
                    LOG.info("MIGRATED: '{}' --> '{}'", uriValue, newUri.getValue());
                    hasChanged = true;
                }
            }
            newUriList.add(newUri);
        }
        if (hasChanged) {

            // TODO: (Bob: 2014 Feb 19) Not needed, to be checked
            schemeTypeCommunityRules.getURI().clear();
            schemeTypeCommunityRules.getURI().addAll(newUriList);
        }
    }

    public MigrationMessages getMigrationMessages() {
        return migrationMessages;
    }

}
