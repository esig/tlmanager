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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.tlmanager.core.exception.FillerException;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tsl.jaxb.ecc.CriteriaListType;
import eu.europa.ec.markt.tsl.jaxb.ecc.KeyUsageBitType;
import eu.europa.ec.markt.tsl.jaxb.ecc.KeyUsageType;
import eu.europa.ec.markt.tsl.jaxb.ecc.PoliciesListType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationElementType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationsType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualifierType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualifiersType;
import eu.europa.ec.markt.tsl.jaxb.tsl.AdditionalInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.AdditionalServiceInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.AddressType;
import eu.europa.ec.markt.tsl.jaxb.tsl.AnyType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionsListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.InternationalNamesType;
import eu.europa.ec.markt.tsl.jaxb.tsl.MultiLangNormStringType;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIType;
import eu.europa.ec.markt.tsl.jaxb.tsl.OtherTSLPointerType;
import eu.europa.ec.markt.tsl.jaxb.tsl.PolicyOrLegalnoticeType;
import eu.europa.ec.markt.tsl.jaxb.tsl.PostalAddressListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.PostalAddressType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceHistoryInstanceType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSLSchemeInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPServiceInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPServiceType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TSPType;
import eu.europa.ec.markt.tsl.jaxb.tsl.TrustStatusListType;
import eu.europa.ec.markt.tsl.jaxb.tslx.CertSubjectDNAttributeType;
import eu.europa.ec.markt.tsl.jaxb.tslx.ExtendedKeyUsageType;
import eu.europa.ec.markt.tsl.jaxb.tslx.TakenOverByType;

/**
 * This class serves as a general place to fill objects with content, to which GUI elements are bound to. Several
 * converter/adapter rely on the existence of several objects, which may be present or not because of not 'completely'
 * filled TSL.
 *
 * @version $Revision$ - $Date$
 */

public class ObjectFiller {

    private static eu.europa.ec.markt.tsl.jaxb.tsl.ObjectFactory objectFactoryTSL = new eu.europa.ec.markt.tsl.jaxb.tsl.ObjectFactory();
    private static eu.europa.ec.markt.tsl.jaxb.tslx.ObjectFactory objectFactoryTSLX = new eu.europa.ec.markt.tsl.jaxb.tslx.ObjectFactory();
    private static eu.europa.ec.markt.tsl.jaxb.ecc.ObjectFactory objectFactoryECC = new eu.europa.ec.markt.tsl.jaxb.ecc.ObjectFactory();
    private static eu.europa.ec.markt.tsl.jaxb.xades.ObjectFactory objectFactoryXADES = new eu.europa.ec.markt.tsl.jaxb.xades.ObjectFactory();

    /**
     * Convenience method for wrapping any <code>JAXBElement</code> into an {@code AnyType} object
     *
     * @param <T>     an arbitrary type
     * @param element a {@code JAXBElement} to be wrapped
     * @return an {@code AnyType} that contains the provided object
     */
    public static <T> AnyType addToAnyType(JAXBElement<T> element) {
        AnyType anyType = objectFactoryTSL.createAnyType();
        anyType.getContent().add(element);

        return anyType;
    }

    /**
     * Fills a given {@code TrustStatusListType} with all needed objects.
     *
     * @param tsl the {@code TrustStatusListType}
     * @return the filled {@code TrustStatusListType}
     */
    public static TrustStatusListType fillAll(TrustStatusListType tsl) throws FillerException {
        try {
            if (tsl.getSchemeInformation() != null) {
                fillTSLSchemeInformationType(tsl.getSchemeInformation());
            }
            if (tsl.getTrustServiceProviderList() != null) {
                for (TSPType tsp : tsl.getTrustServiceProviderList().getTrustServiceProvider()) {
                    fillTSPInformationType(tsp.getTSPInformation());
                    if (tsp.getTSPServices() != null) {
                        for (TSPServiceType service : tsp.getTSPServices().getTSPService()) {
                            TSPServiceInformationType serviceInformation = service.getServiceInformation();
                            if (serviceInformation != null) {
                                fillTSPServiceInformationType(serviceInformation);
                                if (serviceInformation.getServiceInformationExtensions() != null) {
                                    fillExtensionsListType(serviceInformation.getServiceInformationExtensions());
                                }
                                if (service.getServiceHistory() != null) {
                                    for (ServiceHistoryInstanceType history : service.getServiceHistory().getServiceHistoryInstance()) {
                                        fillServiceHistoryInstanceType(history);
                                        if (history.getServiceInformationExtensions() != null) {
                                            fillExtensionsListType(history.getServiceInformationExtensions());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (tsl.getSchemeInformation() != null && tsl.getSchemeInformation().getPointersToOtherTSL() != null) {
                for (OtherTSLPointerType pointer : tsl.getSchemeInformation().getPointersToOtherTSL().getOtherTSLPointer()) {
                    fillOtherTSPPointerType(pointer);
                }
            }
        } catch (Exception ex) {
            throw new FillerException("Error occurred during filling objects!", ex);
        }

        return tsl;
    }

    /**
     * Ensure that all necessary fields are there to which a value might be bound
     *
     * @param info the {@code TSLSchemeInformationType} to fill
     * @return the filled object
     */
    public static TSLSchemeInformationType fillTSLSchemeInformationType(TSLSchemeInformationType info) throws FillerException {
        try {
            if (info.getSchemeOperatorName() == null) { // tsl:SchemeOperatorName
                info.setSchemeOperatorName(objectFactoryTSL.createInternationalNamesType());
            }
            if (info.getSchemeOperatorAddress() == null) { // SchemeOperatorAddress type=tsl:AddressType
                AddressType addressType = objectFactoryTSL.createAddressType();
                addressType.setElectronicAddress(objectFactoryTSL.createElectronicAddressType());
                addressType.setPostalAddresses(objectFactoryTSL.createPostalAddressListType());
                info.setSchemeOperatorAddress(addressType);
            } else {
                if (info.getSchemeOperatorAddress().getElectronicAddress() == null) {
                    info.getSchemeOperatorAddress().setElectronicAddress(objectFactoryTSL.createElectronicAddressType());
                }
                if (info.getSchemeOperatorAddress().getPostalAddresses() == null) {
                    info.getSchemeOperatorAddress().setPostalAddresses(objectFactoryTSL.createPostalAddressListType());
                } else {
                    PostalAddressListType postalAddresses = info.getSchemeOperatorAddress().getPostalAddresses();
                    for (PostalAddressType address : postalAddresses.getPostalAddress()) {
                        if (address.getCountryName() == null || address.getCountryName().isEmpty()) {
                            address.setCountryName(Util.DEFAULT_NO_SELECTION_ENTRY);
                        }
                    }
                }
            }

            // tsl:StatusDeterminationApproach - overwrite any potential different values
            info.setStatusDeterminationApproach(Configuration.getInstance().getTSL().getTslStatusDeterminationApproach());

            // tsl:historicalInformationPeriod - overwrite
            info.setHistoricalInformationPeriod(Configuration.getInstance().getHistoricalInformationPeriod());

            if (info.getSchemeName() == null) { // tsl:SchemeName
                info.setSchemeName(objectFactoryTSL.createInternationalNamesType());
            }

            if (info.getSchemeInformationURI() == null) { // tsl:SchemeInformationURI
                info.setSchemeInformationURI(objectFactoryTSL.createNonEmptyMultiLangURIListType());
            }

            if (info.getSchemeTypeCommunityRules() == null) { // tsl:SchemeTypeCommunityRules
                info.setSchemeTypeCommunityRules(objectFactoryTSL.createNonEmptyMultiLangURIListType());
                info.getSchemeTypeCommunityRules().getURI();    // create the arraylist
            }

            if (DSSUtils.isBlank(info.getSchemeTerritory())) { // tsl:SchemeTerritory
                info.setSchemeTerritory(Util.DEFAULT_NO_SELECTION_ENTRY);
            }

            if (info.getPolicyOrLegalNotice() == null) { // tsl:PolicyOrLegalNotice
                // Note: Only the "@XmlElement(name = "TSLLegalNotice")" is of importance.
                PolicyOrLegalnoticeType policyOrLegalnoticeType = objectFactoryTSL.createPolicyOrLegalnoticeType();
                policyOrLegalnoticeType.getTSLLegalNotice().add(objectFactoryTSL.createMultiLangStringType());
                info.setPolicyOrLegalNotice(policyOrLegalnoticeType);
            }

            if (info.getNextUpdate() == null) { // tsl:NextUpdate
                info.setNextUpdate(objectFactoryTSL.createNextUpdateType());
            }

            if (info.getDistributionPoints() == null) { // tsl:DistributionPoints
                info.setDistributionPoints(objectFactoryTSL.createNonEmptyURIListType());
            }
            if (info.getSchemeExtensions() == null) { // tsl:SchemeExtensions
                info.setSchemeExtensions(objectFactoryTSL.createExtensionsListType());
            }
        } catch (Exception ex) {
            throw new FillerException("fillTSLSchemeInformationType", ex);
        }

        return info;
    }

    /**
     * Ensure that all necessary fields are there to which a value might be bound
     *
     * @param pointer the {@code OtherTSLPointerType} to fill
     * @return the filled object
     */
    public static OtherTSLPointerType fillOtherTSPPointerType(OtherTSLPointerType pointer) throws FillerException {
        try {
            if (pointer.getServiceDigitalIdentities() == null) {
                pointer.setServiceDigitalIdentities(objectFactoryTSL.createServiceDigitalIdentityListType());
            }
            if (pointer.getServiceDigitalIdentities().getServiceDigitalIdentity().isEmpty()) {
                pointer.getServiceDigitalIdentities().getServiceDigitalIdentity().add(objectFactoryTSL.createDigitalIdentityListType());
            }

            AdditionalInformationType additionalInformationType = null;
            if (pointer.getAdditionalInformation() == null) {
                additionalInformationType = objectFactoryTSL.createAdditionalInformationType();
                pointer.setAdditionalInformation(additionalInformationType);
            } else {
                additionalInformationType = pointer.getAdditionalInformation();
            }

            if (pointer.getTSLLocation() == null) {
                pointer.setTSLLocation("Pointer to TSL");
            }

            List<Serializable> othObjects = additionalInformationType.getTextualInformationOrOtherInformation();
            boolean son = false, stcr = false, st = false, mt = false, tt = false;

            for (Object obj : othObjects) {
                if (obj instanceof AnyType) {
                    JAXBElement<?> element = Util.extractJAXBElement((AnyType) obj);
                    if (element != null) {
                        QName name = element.getName();
                        if (QNames._SchemeOperatorName_QNAME.equals(name)) {
                            son = true;
                        }
                        if (QNames._SchemeTypeCommunityRules_QNAME.equals(name)) {
                            stcr = true;
                        }
                        if (QNames._SchemeTerritory_QNAME.equals(name)) {
                            st = true;
                        }
                        if (QNames._MimeType_QNAME.equals(name)) {
                            mt = true;
                        }
                        if (QNames._TSLType_QNAME.equals(name)) {
                            tt = true;
                        }
                    }
                }
            }

            if (!son) { // tsl:SchemeOperatorName
                InternationalNamesType internationalNamesType = objectFactoryTSL.createInternationalNamesType();
                JAXBElement<InternationalNamesType> schemeOperatorName = objectFactoryTSL.createSchemeOperatorName(internationalNamesType);
                othObjects.add(addToAnyType(schemeOperatorName));
            }

            if (!stcr) { // tsl:SchemeTypeCommunityRules
                NonEmptyMultiLangURIListType nonEmptyMultiLangURIType = objectFactoryTSL.createNonEmptyMultiLangURIListType();
                JAXBElement<NonEmptyMultiLangURIListType> schemeTypeCommunityRules = objectFactoryTSL.createSchemeTypeCommunityRules(nonEmptyMultiLangURIType);
                othObjects.add(addToAnyType(schemeTypeCommunityRules));
            }

            if (!st) { // tsl:SchemeTerritory
                JAXBElement<String> schemeTerritory = objectFactoryTSL.createSchemeTerritory(Util.DEFAULT_NO_SELECTION_ENTRY);
                othObjects.add(addToAnyType(schemeTerritory));
            }

            if (!mt) { // tslx:MimeType
                JAXBElement<String> mimeType = objectFactoryTSLX.createMimeType(Util.DEFAULT_NO_SELECTION_ENTRY);
                othObjects.add(addToAnyType(mimeType));
            }

            if (!tt) { // tsl:TSLType
                final String tslTypeInverse = Configuration.getInstance().getTSL().getTslTypeInverse();
                JAXBElement<String> TSLType = objectFactoryTSL.createTSLType(tslTypeInverse);
                othObjects.add(addToAnyType(TSLType));
            }

            pointer.setAdditionalInformation(additionalInformationType);
        } catch (Exception ex) {
            throw new FillerException("fillOtherTSPPointerType", ex);
        }

        return pointer;
    }

    /**
     * Ensure that all necessary fields are there to which a value might be bound
     *
     * @param tspInformation the {@code TSPInformationType} to fill
     * @return the filled object
     */
    public static TSPInformationType fillTSPInformationType(TSPInformationType tspInformation) throws FillerException {
        try {
            if (tspInformation.getTSPName() == null) { // TSPName
                InternationalNamesType internationalNamesType = objectFactoryTSL.createInternationalNamesType();
                MultiLangNormStringType multiLangNormStringType = objectFactoryTSL.createMultiLangNormStringType();
                multiLangNormStringType.setLang(Configuration.getInstance().getLanguageCodes().getEnglishLanguage());
                multiLangNormStringType.setValue("Trust Service Provider");
                internationalNamesType.getName().add(multiLangNormStringType);
                tspInformation.setTSPName(internationalNamesType);
            }
            if (tspInformation.getTSPTradeName() == null) { // TSPTradeName
                tspInformation.setTSPTradeName(objectFactoryTSL.createInternationalNamesType());
            }

            if (tspInformation.getTSPAddress() == null) { // TSPAddress
                AddressType addressType = objectFactoryTSL.createAddressType();
                addressType.setElectronicAddress(objectFactoryTSL.createElectronicAddressType());
                addressType.setPostalAddresses(objectFactoryTSL.createPostalAddressListType());
                tspInformation.setTSPAddress(addressType);
            } else {
                if (tspInformation.getTSPAddress().getElectronicAddress() == null) {
                    tspInformation.getTSPAddress().setElectronicAddress(objectFactoryTSL.createElectronicAddressType());
                }
                if (tspInformation.getTSPAddress().getPostalAddresses() == null) {
                    tspInformation.getTSPAddress().setPostalAddresses(objectFactoryTSL.createPostalAddressListType());
                }
            }

            if (tspInformation.getTSPInformationURI() == null) { // TSPInformationURI
                tspInformation.setTSPInformationURI(objectFactoryTSL.createNonEmptyMultiLangURIListType());
            }

            if (tspInformation.getTSPInformationExtensions() == null) {
                tspInformation.setTSPInformationExtensions(objectFactoryTSL.createExtensionsListType());
            }
        } catch (Exception ex) {
            throw new FillerException("fillTSPInformationType", ex);
        }
        return tspInformation;
    }

    /**
     * Ensure that all necessary fields are there to which a value might be bound
     *
     * @param serviceInfo the {@code TSPServiceInformationType} to fill
     * @return the filled object
     */
    public static TSPServiceInformationType fillTSPServiceInformationType(TSPServiceInformationType serviceInfo) throws FillerException {
        try {
            if (serviceInfo.getServiceName() == null) { // ServiceName
                InternationalNamesType internationalNamesType = objectFactoryTSL.createInternationalNamesType();
                MultiLangNormStringType multiLangNormStringType = objectFactoryTSL.createMultiLangNormStringType();
                multiLangNormStringType.setLang(Configuration.getInstance().getLanguageCodes().getEnglishLanguage());
                multiLangNormStringType.setValue("Trust Service");
                internationalNamesType.getName().add(multiLangNormStringType);
                serviceInfo.setServiceName(internationalNamesType);
            }
            if (serviceInfo.getServiceSupplyPoints() == null) {
                serviceInfo.setServiceSupplyPoints(objectFactoryTSL.createServiceSupplyPointsType());
            }
            if (serviceInfo.getSchemeServiceDefinitionURI() == null) { // SchemeServiceDefinitionURI
                serviceInfo.setSchemeServiceDefinitionURI(objectFactoryTSL.createNonEmptyMultiLangURIListType());
            }
            if (serviceInfo.getTSPServiceDefinitionURI() == null) { // TSPServiceDefinitionURI
                serviceInfo.setTSPServiceDefinitionURI(objectFactoryTSL.createNonEmptyMultiLangURIListType());
            }
            if (serviceInfo.getServiceTypeIdentifier() == null || serviceInfo.getServiceTypeIdentifier().isEmpty()) {
                serviceInfo.setServiceTypeIdentifier(Util.DEFAULT_NO_SELECTION_ENTRY);
            }
            if (serviceInfo.getServiceStatus() == null || serviceInfo.getServiceStatus().isEmpty()) {
                serviceInfo.setServiceStatus(Util.DEFAULT_NO_SELECTION_ENTRY);
            }

            if (serviceInfo.getServiceDigitalIdentity() == null) {
                serviceInfo.setServiceDigitalIdentity(objectFactoryTSL.createDigitalIdentityListType());
            }
            // if (serviceInfo.getServiceDigitalIdentity().getDigitalId().isEmpty()) {
            // serviceInfo.getServiceDigitalIdentity().getDigitalId().add(objectFactoryTSL.createDigitalIdentityListType());
            // }
        } catch (Exception ex) {
            throw new FillerException("fillTSPServiceInformationType", ex);
        }

        return serviceInfo;
    }

    /**
     * Ensure that all necessary fields are there to which a value might be bound
     *
     * @param historyInstance the {@code ServiceHistoryInstanceType} to fill
     * @return the filled object
     */
    public static ServiceHistoryInstanceType fillServiceHistoryInstanceType(final ServiceHistoryInstanceType historyInstance) throws FillerException {

        try {

            if (historyInstance.getServiceName() == null) {
                historyInstance.setServiceName(objectFactoryTSL.createInternationalNamesType());
            }
            if (historyInstance.getServiceDigitalIdentity() == null) {
                historyInstance.setServiceDigitalIdentity(objectFactoryTSL.createDigitalIdentityListType());
            }
            if (historyInstance.getServiceStatus() == null) {
                historyInstance.setServiceStatus(Util.DEFAULT_NO_SELECTION_ENTRY);
            }
        } catch (Exception ex) {
            throw new FillerException("fillServiceHistoryInstanceType", ex);
        }
        return historyInstance;
    }

    /**
     * Ensure that all necessary fields are there to which a value might be bound
     *
     * @param extensionsList the {@code ExtensionsListType} to fill
     * @return the filled object
     */
    public static ExtensionsListType fillExtensionsListType(ExtensionsListType extensionsList) throws FillerException {
        try {
            Util.wrapExtensionsIndividually(extensionsList);
            List<ExtensionType> extensions = extensionsList.getExtension();

            boolean tob = false, ecri = false, asi = false, q = false;
            for (ExtensionType extension : extensions) {
                JAXBElement<?> element = Util.extractJAXBElement(extension);
                if (element != null) {
                    QName name = element.getName();
                    if (QNames._TakenOverBy_QNAME.equals(name)) {
                        tob = true;
                        JAXBElement<TakenOverByType> el = (JAXBElement<TakenOverByType>) element;
                        fillTakenOverByType(el.getValue());
                    }
                    if (QNames._ExpiredCertsRevocationInfo_QNAME.equals(name)) {
                        ecri = true;
                    }
                    if (QNames._AdditionalServiceInformation_QNAME.equals(name)) {
                        asi = true;
                        JAXBElement<AdditionalServiceInformationType> el = (JAXBElement<AdditionalServiceInformationType>) element;
                        fillAdditionalServiceInformationType(el.getValue());
                    }
                    if (QNames._Qualifications_QNAME.equals(name)) {
                        JAXBElement<QualificationsType> el = (JAXBElement<QualificationsType>) element;
                        for (QualificationElementType qel : el.getValue().getQualificationElement()) {
                            fillQualificationElement(qel);
                        }
                    }
                }
            }
            if (!tob) {
                ExtensionType extensionType = objectFactoryTSL.createExtensionType();
                JAXBElement<TakenOverByType> element = objectFactoryTSLX.createTakenOverBy(fillTakenOverByType(objectFactoryTSLX.createTakenOverByType()));
                extensionType.getContent().add(element);
                extensions.add(extensionType);
            }
            if (!ecri) {
                ExtensionType extensionType = objectFactoryTSL.createExtensionType();
                JAXBElement<XMLGregorianCalendar> element = objectFactoryTSL.createExpiredCertsRevocationInfo(null);
                extensionType.getContent().add(element);
                extensions.add(extensionType);
            }
            if (!asi) {
                // nothing to do, because AdditionalServiceInformationAdapter is doing the work if there is no asi
            }
        } catch (Exception ex) {
            throw new FillerException("fillExtensionsListType", ex);
        }

        return extensionsList;
    }

    private static TakenOverByType fillTakenOverByType(TakenOverByType takenOverBy) throws FillerException {
        try {
            if (takenOverBy.getURI() == null) {
                takenOverBy.setURI(objectFactoryTSL.createNonEmptyMultiLangURIType());
            }
            if (takenOverBy.getTSPName() == null) {
                takenOverBy.setTSPName(objectFactoryTSL.createInternationalNamesType());
            }
            if (takenOverBy.getSchemeOperatorName() == null) {
                takenOverBy.setSchemeOperatorName(objectFactoryTSL.createInternationalNamesType());
            }
            if (takenOverBy.getSchemeTerritory() == null || takenOverBy.getSchemeTerritory().isEmpty()) {
                takenOverBy.setSchemeTerritory(Util.DEFAULT_NO_SELECTION_ENTRY);
            }
        } catch (Exception ex) {
            throw new FillerException("fillTakenOverByType", ex);
        }

        return takenOverBy;
    }

    private static AdditionalServiceInformationType fillAdditionalServiceInformationType(AdditionalServiceInformationType asi) throws FillerException {
        if (asi.getURI() == null) {
            NonEmptyMultiLangURIType uri = objectFactoryTSL.createNonEmptyMultiLangURIType();
            uri.setValue(Util.DEFAULT_NO_SELECTION_ENTRY);
            asi.setURI(uri);
        }

        return asi;
    }

    /**
     * Ensure that all necessary fields are there to which a value might be bound
     *
     * Note: There are different approaches for parsing data here. E.g. a 'PolicySet' could be stored in a
     * 'CriteriaListType' as well as in the 'otherCriteriaList' as 'AnyType'. However, the following annotation (taken
     * from ts_102231v030102_sie_xsd.xsd) is taken into account here:
     * --------
     * <complexType name="CriteriaListType">
     * <annotation>
     * <documentation>Please first try to use the CriteriaList before doing the OtherCriteria extension
     * point.</documentation>
     * </annotation>
     * --------
     *
     * Note: values could be carried over, otherwise they are lost and
     * have to be reentered in TLManager Example: if there are PolicySets defined in otherCriteria, they could be parsed
     * and set directly below the criteriaList! Furthermore, especially this object seems to be a source for a multitude
     * of different interpretations throughout other MS TSL; data which is not stored in the correct objects (see
     * specification) cannot be parsed and have to be re-entered in TLManager.
     *
     * @param element the {@code QualificationElementType} to fill
     * @return the filled object
     */
    @SuppressWarnings("restriction")
    public static QualificationElementType fillQualificationElement(QualificationElementType element) throws FillerException {
        try {
            CriteriaListType criteriaList = element.getCriteriaList();
            if (criteriaList == null) {
                criteriaList = objectFactoryECC.createCriteriaListType();
                element.setCriteriaList(criteriaList);
            }
            if (criteriaList.getAssert() == null || criteriaList.getAssert().isEmpty()) {
                criteriaList.setAssert(Util.DEFAULT_NO_SELECTION_ENTRY);
            }

            if (criteriaList.getKeyUsage().isEmpty()) {
                KeyUsageType keyUsageType = objectFactoryECC.createKeyUsageType();
                criteriaList.getKeyUsage().add(keyUsageType);
            }
            for (KeyUsageType keyUsage : criteriaList.getKeyUsage()) {
                fillKeyUsageType(keyUsage);
            }

            if (criteriaList.getPolicySet().isEmpty()) {
                PoliciesListType policiesListType = objectFactoryECC.createPoliciesListType();
                criteriaList.getPolicySet().add(policiesListType);
            }

            if (criteriaList.getOtherCriteriaList() == null) {
                criteriaList.setOtherCriteriaList(objectFactoryXADES.createAnyType());
            }

            boolean createEKUT = true, createCSDAT = true;
            List<Object> otherCriteriaContent = criteriaList.getOtherCriteriaList().getContent();
            if (otherCriteriaContent.isEmpty()) {

                ExtendedKeyUsageType extendedKeyUsageType = objectFactoryTSLX.createExtendedKeyUsageType();
                JAXBElement<ExtendedKeyUsageType> extendedKeyUsage = objectFactoryTSLX.createExtendedKeyUsage(extendedKeyUsageType);

                CertSubjectDNAttributeType certSubjectDNAttributeType = objectFactoryTSLX.createCertSubjectDNAttributeType();
                JAXBElement<CertSubjectDNAttributeType> certSubjectDNAttribute = objectFactoryTSLX.createCertSubjectDNAttribute(certSubjectDNAttributeType);
                otherCriteriaContent.add(extendedKeyUsage);
                otherCriteriaContent.add(certSubjectDNAttribute);
            } else {
                for (Object othCrit : otherCriteriaContent) {
                    if (othCrit instanceof JAXBElement<?>) {
                        JAXBElement<?> elem = (JAXBElement<?>) othCrit;

                        if (elem.getName().equals(QNames._ExtendedKeyUsage_QNAME)) {
                            createEKUT = false;
                        } else if (elem.getName().equals(QNames._CertSubjectDNAttribute_QNAME)) {
                            createCSDAT = false;
                        }
                    }
                }
            }

            if (createEKUT) {
                ExtendedKeyUsageType extendedKeyUsageType = objectFactoryTSLX.createExtendedKeyUsageType();
                JAXBElement<ExtendedKeyUsageType> extendedKeyUsage = objectFactoryTSLX.createExtendedKeyUsage(extendedKeyUsageType);
                otherCriteriaContent.add(extendedKeyUsage);
            }
            if (createCSDAT) {
                CertSubjectDNAttributeType certSubjectDNAttributeType = objectFactoryTSLX.createCertSubjectDNAttributeType();
                JAXBElement<CertSubjectDNAttributeType> certSubjectDNAttribute = objectFactoryTSLX.createCertSubjectDNAttribute(certSubjectDNAttributeType);
                otherCriteriaContent.add(certSubjectDNAttribute);
            }

            QualifiersType qualifiers = element.getQualifiers();
            if (qualifiers == null) {
                qualifiers = objectFactoryECC.createQualifiersType();
                element.setQualifiers(qualifiers);
            }
            final short NUMBER_OF_QUALIFIERS = 1;
            for (int i = 0; i < NUMBER_OF_QUALIFIERS; i++) {
                if (qualifiers.getQualifier().size() == i) {
                    QualifierType qualifier = objectFactoryECC.createQualifierType();
                    qualifier.setUri(Util.DEFAULT_NO_SELECTION_ENTRY);
                    qualifiers.getQualifier().add(qualifier);
                }
            }
            // remove any qualifier besides of the first NUMBER_OF_QUALIFIERS (a loaded list may contain these)
            List<QualifierType> qualifierList = qualifiers.getQualifier();
            while (qualifierList.size() > NUMBER_OF_QUALIFIERS) {
                qualifierList = qualifierList.subList(0, NUMBER_OF_QUALIFIERS);
            }
        } catch (Exception ex) {
            throw new FillerException("fillQualificationElement", ex);
        }

        return element;
    }

    private static KeyUsageType fillKeyUsageType(KeyUsageType keyUsageType) throws FillerException {
        try {
            List<String> existingKubits = new ArrayList<String>();
            List<KeyUsageBitType> invalid = new ArrayList<KeyUsageBitType>();
            for (KeyUsageBitType kubit : keyUsageType.getKeyUsageBit()) {
                boolean valid = false;
                for (KeyUsageBits validKubit : KeyUsageBits.values()) {
                    if (validKubit.name().equals(kubit.getName())) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    invalid.add(kubit);
                }
                if (valid && kubit.getName() != null) {
                    existingKubits.add(kubit.getName());
                }
            }
            // if there is anything else in the list besides of the 9 valid ones it will be removed
            keyUsageType.getKeyUsageBit().removeAll(invalid);

            /*
            for (KeyUsageBits kubit : KeyUsageBits.values()) {
                if (!existingKubits.contains(kubit.toString())) {
                    // not yet there ? create one
                    KeyUsageBitType kubitType = objectFactoryECC.createKeyUsageBitType();
                    kubitType.setName(kubit.toString());
                    keyUsageType.getKeyUsageBit().add(kubitType);
                }
            }*/
        } catch (Exception ex) {
            throw new FillerException("fillKeyUsageType", ex);
        }

        return keyUsageType;
    }
}