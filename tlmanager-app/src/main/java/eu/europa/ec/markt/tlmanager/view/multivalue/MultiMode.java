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

package eu.europa.ec.markt.tlmanager.view.multivalue;

import eu.europa.ec.markt.tlmanager.view.multivalue.content.AdditionalServiceInformationContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.AnyUriContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.ComboBoxContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.DigitalIdentityContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.KeyUsageContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.MultiContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.PoliciesListContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.PolicyIdentifierContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.PostalContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.ServiceDigitalIdentityContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.SuggestedContentValues;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.TSPInformationExtensionContent;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.TextContent;

/**
 * Enumeration to distinguish between the different modes of a <code>MultivalueTextArea</code>
 *
 * @version $Revision$ - $Date$
 */

public enum MultiMode {
    MULTILANG_TEXT(TextContent.class, true), MULTILANG_POSTAL(PostalContent.class, true), MULTI_ANYURI(
          AnyUriContent.class), MULTI_MULTILANGANYURI(AnyUriContent.class, true), MULTI_TEXT(TextContent.class), MULTI_COMBOBOX(
          ComboBoxContent.class), MULTILANG_COMBOBOX(ComboBoxContent.class, true), MULTI_ASI(
          AdditionalServiceInformationContent.class), MULTI_TSPINEX(TSPInformationExtensionContent.class), MULTI_CERT(
          DigitalIdentityContent.class), MULTI_KEYUSAGE(KeyUsageContent.class), MULTI_POLICIES(
          PoliciesListContent.class), MULTI_POLICYIDENTIFIER(PolicyIdentifierContent.class), MULTI_SERVICE_ID(
          ServiceDigitalIdentityContent.class), MULTI_DIGITALID(DigitalIdentityContent.class);

    private final Class<? extends MultiContent> multiContentClass;
    private final boolean multiLanguage;

    private MultiMode(Class<? extends MultiContent> multiContentClass) {
        this(multiContentClass, false);
    }

    private MultiMode(Class<? extends MultiContent> multiContentClass, boolean multiLanguage) {
        this.multiContentClass = multiContentClass;
        this.multiLanguage = multiLanguage;
    }

    public MultiContent createMultiContent(String... multiConfValues) {
        try {
            final MultiContent multiContent = multiContentClass.newInstance();
            if (multiContent instanceof SuggestedContentValues && multiConfValues != null) {
                SuggestedContentValues content = (SuggestedContentValues) multiContent;
                content.setMultiConfValues(multiConfValues);
            }
            return multiContent;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isMultiLanguage() {
        return multiLanguage;
    }

}
