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
package eu.europa.ec.markt.tlmanager.util;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filters everything out that contains just whitespaces.
 * 
 *
 *
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