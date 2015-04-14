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
package eu.europa.ec.markt.tlmanager.view.binding;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIType;

import org.jdesktop.beansbinding.Converter;

/**
 * Does the conversion between a <code>NonEmptyMultiLangURIType</code> and a <code>String</code> Note: This converter
 * will assume english as the default language.
 * 
 *
 *
 */

public class NonEmptyMultiLangURIConverter extends Converter<NonEmptyMultiLangURIType, String> {

    /** {@inheritDoc} */
    @Override
    public String convertForward(NonEmptyMultiLangURIType source) {
        /**
         * The business rules of etsi's conformance checker just specify the following: TAKENOVER-URI-2 The lang
         * attribute in the URI element in the TakenOverBy Extension MUST have a value present in the set of Languages
         * within the EU.
         */
        source.setLang(Configuration.LanguageCodes.getEnglishLanguage()); // overwrites

        return source.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public NonEmptyMultiLangURIType convertReverse(String target) {
        NonEmptyMultiLangURIType type = new NonEmptyMultiLangURIType();
        type.setLang(Configuration.LanguageCodes.getEnglishLanguage());
        type.setValue(target);

        return type;
    }
}