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

package eu.europa.ec.markt.tlmanager.util;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filters everything out that contains just whitespaces.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class WhitespaceFilterXML extends XMLFilterImpl {

    /**
     * The default constructor for WhitespaceFilterXML.
     */
    public WhitespaceFilterXML() {
        super();
    }

    /** @{inheritDoc */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
    }

    /** @{inheritDoc */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String content = new String(ch, start, length).trim();
        if (!content.equals("")) {
            super.characters(ch, start, length);
        }
    }

    /** @{inheritDoc */
    @Override
    public void parse(String systemId) throws SAXException, IOException {
        super.parse(systemId);
    }

    /** @{inheritDoc */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }
}