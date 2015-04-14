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

import eu.europa.ec.markt.tlmanager.core.Configuration;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Small helper class for coping with duplication of items.
 * 
 *
 *
 */
public class ItemDuplicator {

    private static final String DELIMITER = "_";
    private int startingNumber = 1;
    private DefaultListModel model;
    private Map<String, Integer> lastNumbers;

    /**
     * Instantiates a new item duplicator.
     * 
     * @param model the model
     */
    public ItemDuplicator(final DefaultListModel model) {
        this.model = model;
        lastNumbers = new HashMap<String, Integer>();
    }

    private int determineHighestCounter(final String selectedValue) {
        final String prefix = selectedValue.substring(0, 2);

        int counter = 1;
        for (int i = 0; i < model.getSize(); i++) {
            final String entry = (String) model.get(i);
            if ( !entry.startsWith(prefix) ) {
                continue;
            }
            String[] tokens = entry.split(DELIMITER);
            if ( tokens.length < 2 ) {
                continue;
            }
            final String token = tokens[1];
            int number = Integer.parseInt(token);
            if ( number >= counter ) {
                counter = number + 1;
            }
        }

        return counter;
    }

    /**
     * Duplicates a language entry. It compares the provided selectedValue with the existing values and fishes out the
     * highest existing counter for that entry. The duplicated entry will have a larger counter value.
     * 
     * @param selectedValue the selected value
     * 
     * @return position of the entry in the model
     */
    public int duplicateLanguageEntry(final String selectedValue) {
        startingNumber = determineHighestCounter(selectedValue); // note, that this has an impact in createNewItem() below!

        final List<String> items = new ArrayList<String>();
        for (int i = 0; i < model.getSize(); i++) {
            items.add((String) model.get(i));
        }

        final String newItem = createNewItem(selectedValue);
        items.add(newItem);

        Util.sortItems(items, Configuration.LanguageCodes.getEnglishLanguage());

        model.clear();
        for ( final String item : items ) {
            model.addElement(item);
        }

        int itemLocation = 0;
        for (int i = 0; i < model.getSize(); i++) {
            final String item = (String) model.get(i);
            if (item.equals(newItem)) {
                itemLocation = i;
            }
        }

        return itemLocation;
    }

    private String createNewItem(final String item) {
        final String[] tokens = item.split(DELIMITER);
        final String token = tokens[0];

        Integer lastNumber = startingNumber;
        if (lastNumbers.containsKey(token)) {
            // this item was duplicated before
            lastNumber = lastNumbers.get(token) + 1;
        }
        lastNumbers.put(token, lastNumber);

        return token + DELIMITER + lastNumber;
    }

    /**
     * Reinitialises the used hashamp for last numbers.
     */
    public void reInit() {
        lastNumbers = new HashMap<String, Integer>();
    }

    /**
     * Static method for duplicating an entry. If the given entry already contains the used DELIMITER, the string is
     * returned unchanged.
     * 
     * @param entry the entry
     * @param count the count
     * 
     * @return the duplicated entry
     */
    public static String duplicate(final String entry, final Integer count) {
        if (entry.contains(DELIMITER)) {
            return entry;
        }

        return entry + DELIMITER + count;
    }

    /**
     * Static method for cleaning the delimiter and counter of a provided entry
     * 
     * @param item the item
     * 
     * @return the cleaned string
     */
    public static String cleanItem(String item) {
        final String[] tokens = item.split(DELIMITER);
        return tokens[0];
    }
}