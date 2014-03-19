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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.europa.ec.markt.tlmanager.core.QNames;
import eu.europa.ec.markt.tsl.jaxb.tsl.AnyType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionsListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.InternationalNamesType;
import eu.europa.ec.markt.tsl.jaxb.tsl.MultiLangNormStringType;

/**
 * Collection of utility functions.
 *
 * @version $Revision$ - $Date$
 */

public class Util {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Util.class);

    public static final String DEFAULT_NO_SELECTION_ENTRY = "-";
    private static String OS = null;

    /**
     * Determines, if the Operating System is Windows.
     *
     * @return true, if operating system is windows
     */
    public static boolean isWindows() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        if (OS != null) {
            return OS.startsWith("Windows");
        }
        return false;
    }

    /**
     * Convenience wrapper for {@link #addNoSelectionEntry(String[], boolean, String)}
     *
     * @param items the items
     * @return the string[]
     */
    public static String[] addNoSelectionEntry(String[] items) {
        return addNoSelectionEntry(items, true, null);
    }

    /**
     * Adds a specific entry at the beginning of a given String array.
     *
     * @param items            the items
     * @param atStart          the at start
     * @param noSelectionEntry the no selection entry
     * @return the string[]
     */
    public static String[] addNoSelectionEntry(String[] items, boolean atStart, String noSelectionEntry) {
        if (noSelectionEntry == null) {
            noSelectionEntry = DEFAULT_NO_SELECTION_ENTRY;
        }
        String[] extendedItems = new String[items.length + 1];
        int i = 0;
        if (atStart) {
            i = 1;
        }
        for (String it : items) {
            if (atStart) {
                extendedItems[0] = noSelectionEntry;
                extendedItems[i++] = it;
            } else {
                extendedItems[i++] = it;
            }
        }
        if (!atStart) {
            extendedItems[items.length] = noSelectionEntry;
        }
        return extendedItems;
    }

    /**
     * Creates a <code>Transformer</code> with a configuration that affects the output of the transformed xml.
     *
     * @param indent the amount of indentation
     * @return the transformer
     * @throws javax.xml.transform.TransformerConfigurationException
     */
    public static Transformer createPrettyTransformer(int indent) throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));

        return transformer;
    }

    /**
     * Saves a given <code>Document</code> in the provided file, after applying several things for a pretty output.
     *
     * @param doc    the document to save
     * @param file   the file to save into
     * @param indent the amount of indentation
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.transform.TransformerException
     */
    public static void prettySave(Document doc, File file, int indent) throws FileNotFoundException, TransformerException {
        Source source = new DOMSource(doc);
        FileOutputStream outputStream = new FileOutputStream(file);

        StreamResult xmlOutput = new StreamResult(outputStream);
        Transformer transformer = createPrettyTransformer(indent);

        transformer.transform(source, xmlOutput);

        try {
            outputStream.close();
        } catch (IOException ioe) {
            LOG.warn("Unable to close OutputStream for File: " + file.getAbsolutePath() + " - " + ioe.getMessage());
        }
    }

    /**
     * Wraps {@link #prettySave(org.w3c.dom.Document, java.io.File, int)} and presets an indentation value.
     *
     * @param doc  the document to save
     * @param file the file to save into
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.transform.TransformerException
     */
    public static void prettySave(Document doc, File file) throws FileNotFoundException, TransformerException {
        prettySave(doc, file, 3);
    }

    /**
     * Filter the content of each attribute of type xml:lang, so that only the proper language code is stored. Example:
     * <... xml:lang="de_1"> -> <... xml:lang="de">
     *
     * @param item a node with an appropriate attribute
     */
    private static void handleLangAttribute(Node item) {
        String cleanedAttribute = ItemDuplicator.cleanItem(item.getNodeValue());
        item.setNodeValue(cleanedAttribute);
    }

    private static boolean removeEmptyAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        boolean nodeHasAttribute = false;
        if (attributes != null && attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                if (item.getLocalName().equals("lang")) {
                    handleLangAttribute(item);
                }
                if (item.getNodeValue().equals(Util.DEFAULT_NO_SELECTION_ENTRY)) {
                    attributes.removeNamedItem(item.getNodeName());
                } else {
                    nodeHasAttribute = true;
                }

            }
        }
// Bob --> useless
//        if (!nodeHasAttribute) {
//            attributes = null;
//        }
        return nodeHasAttribute;
    }

    private static void removeUndesiredContent(Node node) {
        if (node.getLocalName() != null) {
            String localName = node.getLocalName();

            // According to jira issue DSS-58, the following is not done anymore
            //            if (localName.equals(QNames._CriteriaList)) {
            //                // check KeyUsageBits
            //                NodeList criteriaList = node.getChildNodes();
            //                for (int i = 0; i < criteriaList.getLength(); i++) {
            //                    Node criteria = criteriaList.item(i);
            //                    if (criteria.getLocalName() != null && criteria.getLocalName().equals(QNames._KeyUsage)) {
            //                        NodeList keyUsageBits = criteria.getChildNodes();
            //                        for (int j = 0; j < keyUsageBits.getLength(); j++) {
            //                            Node keyUsageBit = keyUsageBits.item(j);
            //                            if (keyUsageBit.getTextContent().equals("false")) {
            //                                keyUsageBit.setTextContent("");
            //                            }
            //                        }
            //                    }
            //                }
            //            }
        }
    }

    /**
     * Recursively removes empty nodes. Beware that a node with an attribute and no content, may not be considered
     * empty.
     *
     * @param node the node
     */
    public static void removeEmptyNodesFromTheBottom(Node node) {
        if (node != null) {

            final String localName1 = node.getLocalName();
            if (node.getNamespaceURI() != null && localName1 != null && node.getNamespaceURI().equals(QNames._Signature_QNAME.getNamespaceURI()) && localName1
                  .equals(QNames._Signature_QNAME.getLocalPart())) {
                return; // never touch ds:Signature !
            }

            NodeList nodeList = node.getChildNodes();

            removeEmptyAttributes(node);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);

                // remove child nodes first
                removeEmptyNodesFromTheBottom(childNode);

                removeUndesiredContent(childNode);

                String textContent = childNode.getTextContent();

                final boolean noChildNodes = childNode.getChildNodes().getLength() == 0;
                final boolean textContentIsEmpty = textContent.isEmpty() || textContent.equals(Util.DEFAULT_NO_SELECTION_ENTRY);
                if (!textContentIsEmpty) {

                    if (textContent.startsWith(Util.DEFAULT_NO_SELECTION_ENTRY)) {

                        final String localName = childNode.getLocalName();
                        final String namespaceURI = childNode.getNamespaceURI();
                        if (QNames._URI.equals(localName) && QNames.TSL_NAMESPACE.equals(namespaceURI)) {

                            if (!nodeIsException(childNode)) {
                                childNode.getParentNode().removeChild(childNode);
                                i--;
                            }
                        }
                    }
                } if (textContentIsEmpty && noChildNodes) {
                    if (!nodeIsException(childNode)) {
                        childNode.getParentNode().removeChild(childNode);
                        i--;
                    }
                }
            }
        }
    }

    private static boolean nodeIsException(Node node) {
        // special checks for specific nodes
        if (node.getLocalName() != null) {
            String localName = node.getLocalName();

            if (localName.equals(QNames._NextUpdate_QNAME.getLocalPart())) {
                return true;
            } else if (localName.equals("Qualifiers")) {
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node item = childNodes.item(i);
                    if (nodeIsException(item)) {
                        // as soon there is one 'exceptional' child, this one is exceptional too
                        return true;
                    }
                }
            } else if (localName.equals("Qualifier")) {
                return removeEmptyAttributes(node);
            }
        }

        return false;
    }

    /**
     * Sorts all children of the provided node via respective comparator's.
     *
     * @param parent     the node to start from
     * @param descending true, if in lexicographical order
     */
    public static void sortChildNodes(Node parent, boolean descending) {

        List<Node> nodes = new ArrayList<Node>();
        NodeList childNodeList = parent.getChildNodes();
        if (childNodeList.getLength() > 0) {
            for (int i = 0; i < childNodeList.getLength(); i++) {
                Node node = childNodeList.item(i);
                sortChildNodes(node, descending);
                nodes.add(node);
            }
            Comparator comp = new Comparator<Object>() {
                @Override
                public int compare(Object arg0, Object arg1) {
                    if (arg0 instanceof Element && arg1 instanceof Element) {
                        Element e1 = (Element) arg0;
                        Element e2 = (Element) arg1;
                        String lNameE1 = e1.getLocalName();
                        String lNameE2 = e2.getLocalName();
                        String name = "";
                        if (lNameE1.equals(QNames._TSPService_QNAME.getLocalPart()) && lNameE2.equals(QNames._TSPService_QNAME.getLocalPart())) {
                            name = QNames._ServiceName;
                            return getTextContent(e2, name).compareTo(getTextContent(e1, name));
/*
                        } else if (lNameE1.equals(QNames._ServiceHistoryInstance_QNAME.getLocalPart()) && lNameE2.equals(QNames._ServiceHistoryInstance_QNAME.getLocalPart())) {
                            name = QNames._StatusStartingTime;
                            // The latest date shall be placed below others: descending order
                            // if there's no date at all, place this servicehistoryinstance at the bottom
                            String time1 = getTextContent(e1, name);
                            String time2 = getTextContent(e2, name);

                            if (time1.isEmpty()) {
                                return -1;
                            }
                            if (time2.isEmpty()) {
                                return 1;
                            }

                            String utcformat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                            SimpleDateFormat zulu = new SimpleDateFormat(utcformat);

                            try {
                                Date date1 = zulu.parse(time1);
                                Date date2 = zulu.parse(time2);

                                return -date2.compareTo(date1);
                            } catch (ParseException e) {
                                LOG.warn("Unable to parse one of the following dates with (" +
                                      utcformat + "): " + time1 + " or " + time2);
                            }
*/
                        } else if (lNameE1.equals(QNames._TrustServiceProvider_QNAME.getLocalPart()) && lNameE2.equals(QNames._TrustServiceProvider_QNAME.getLocalPart())) {
                            name = QNames._TSPName;
                            return getTextContent(e2, name).compareTo(getTextContent(e1, name));
                        } else if (lNameE1.equals(QNames._OtherTSLPointer_QNAME.getLocalPart()) && lNameE2.equals(QNames._OtherTSLPointer_QNAME.getLocalPart())) {
                            name = QNames._SchemeTerritory_QNAME.getLocalPart();
                            String tslLoc = QNames._TSLLocation;

                            String label1 = getTextContent(e1, name);
                            String label2 = getTextContent(e2, name);
                            String extension = "pdf";
                            if (label1.substring(0, 2).equals(label2.substring(0, 2))) {
                                // if one extension is 'pdf', put it at the bottom
                                // this way, all machine processable versions should be at the top (xml, or zip, or whatever)
                                String tslLoc1 = getTextContent(e1, tslLoc);
                                String tslLoc2 = getTextContent(e2, tslLoc);
                                if (tslLoc1.endsWith(extension) && !tslLoc2.endsWith(extension)) {
                                    return 1;
                                } else if (!tslLoc1.endsWith(extension) && tslLoc2.endsWith(extension)) {
                                    return -1;
                                }
                            }
                            return label2.compareTo(label1);
                        }
                        return 0;
                    } else {
                        return ((Node) arg0).getNodeName().compareTo(((Node) arg1).getNodeName());
                    }

                }
            };
            if (descending) {
                // if descending is true, get the reverse ordered comparator
                Collections.sort(nodes, Collections.reverseOrder(comp));
            } else {
                Collections.sort(nodes, comp);
            }

            for (Iterator<Node> iter = nodes.iterator(); iter.hasNext(); ) {
                Node element = (Node) iter.next();
                parent.appendChild(element);
            }
        }
    }

    private static String getTextContent(Node parent, String localName) {
        String text = "";
        NodeList children = parent.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (localName.equals(child.getLocalName())) {
                    text = child.getTextContent();
                } else if (child.hasChildNodes()) {
                    text = getTextContent(child, localName);
                }

                if (!text.isEmpty()) {
                    break;
                }
            }
        }

        return text;
    }

    /**
     * Gets the value for lang.
     *
     * @param i18nNames the i18n names
     * @param langCode  the lang code
     * @return the value for lang
     */
    public static String getValueForLang(InternationalNamesType i18nNames, String langCode) {
        for (MultiLangNormStringType s : i18nNames.getName()) {
            if (s.getLang().equals(langCode)) {
                return s.getValue();
            }
        }
        return null;
    }

    /**
     * Gets the initial counter item.
     *
     * @return the initial counter item
     */
    public static String getInitialCounterItem() {
        return getCounterItem(0);
    }

    /**
     * Gets the counter item.
     *
     * @param i the i
     * @return the counter item
     */
    public static String getCounterItem(int i) {
        int count = i + 1;
        return "Entry " + count;
    }

    /**
     * Extracts all <code>ExtensionType</code> from a provided <code>ExtensionsListType</code> which have a
     * <code>QName</code> (local part) that matches the provided name.
     *
     * @param extensions     the <code>ExtensionsListType</code> to search through
     * @param name_localPart the String to match
     * @param inverse        if true, everything except those matching the provided name_localPart is in the returned list
     * @return all matching <code>ExtensionType</code>
     */
    public static List<ExtensionType> extractMatching(ExtensionsListType extensions, String name_localPart, boolean inverse) {
        return extractMatching(extensions.getExtension(), name_localPart, inverse);
    }

    /**
     * Extracts all <code>ExtensionType</code> from a provided list of <code>ExtensionType</code> which have a
     * <code>QName</code> (local part) that matches the provided name.
     *
     * @param extensions     the <code>ExtensionsListType</code> to search through
     * @param name_localPart the String to match
     * @param inverse        if true, everything except those matching the provided name_localPart is in the returned list
     * @return all matching <code>ExtensionType</code>
     */
    public static List<ExtensionType> extractMatching(List<ExtensionType> extensions, String name_localPart, boolean inverse) {
        List<ExtensionType> list = new ArrayList<ExtensionType>();

        for (ExtensionType extension : extensions) {
            String extractedName = Util.extractName(extension);
            if (inverse) {
                if (extractedName != null && !extractedName.equals(name_localPart)) {
                    list.add(extension);
                }
            } else {
                if (extractedName != null && extractedName.equals(name_localPart)) {
                    list.add(extension);
                }
            }
        }
        return list;
    }

    /**
     * Extracts the local part of the <code>QName</code> of a <code>JAXBElement</code> that is contained in a provided
     * <code>ExtensionType</code>.
     *
     * @param extension the <code>ExtensionType</code>
     * @return a String with the local part
     */
    public static String extractName(ExtensionType extension) {
        String name = null;

        JAXBElement<?> extractedJAXBElement = extractJAXBElement(extension);
        if (extractedJAXBElement != null) {
            name = extractedJAXBElement.getName().getLocalPart();
        }

        return name;
    }

    /**
     * Ensures that each <code>ExtensionType</code> has only one <code>JAXBElement</code>.
     * This means, anything like this:
     * <tsl:ServiceInformationExtensions>
     *    <tsl:Extension Critical="true">
     *       <ecc:Qualifications> ...
     *       <tsl:AdditionalServiceInformation> ...
     *    </tsl:Extension>
     * </tsl:ServiceInformationExtensions>
     *
     * will be changed into the following:
     * <tsl:ServiceInformationExtensions>
     *    <tsl:Extension Critical="true">
     *       <tsl:AdditionalServiceInformation> ...
     *    </tsl:Extension>
     *    <tsl:Extension Critical="false">
     *       <ecc:Qualifications> ...
     *    </tsl:Extension>
     * </tsl:ServiceInformationExtensions>
     *
     * @param extList the extension list to verify
     * @return true, if at least one extension was shifted
     */
    public static boolean wrapExtensionsIndividually(ExtensionsListType extList) {
        List<ExtensionType> shiftedExtensions = new ArrayList<ExtensionType>();
        boolean shiftedSomething = false;

        for (ExtensionType extension : extList.getExtension()) {
            List<Object> content = extension.getContent();

            if (content.size() > 1) {
                // each 'content' has to be wrapped individually in an own ExtensionType
                for (Object obj : content) {
                    if (obj instanceof JAXBElement<?>) {
                        ExtensionType ext = new ExtensionType();
                        ext.setCritical(extension.isCritical()); // set each critical state to the one of the parent extension
                        ext.getContent().add(obj);
                        shiftedExtensions.add(ext);
                        shiftedSomething = true;
                    }
                }
            } else {    // 'normal' case
                shiftedExtensions.add(extension);
            }
        }

        extList.getExtension().clear();
        extList.getExtension().addAll(shiftedExtensions);

        return shiftedSomething;
    }

    /**
     * Extracts the
     * <ul>
     * first
     * </ul>
     * <code>JAXBElement</code> from an <code>AnyType</code>. Note: although normally it's not the case that there is
     * more than one JAXBElement in an any type, it may be the case for otherCriteriaList of
     * <code>CriteriaListType</code>.
     *
     * @param anyType the object to search through
     * @return the first JAXBElement
     */
    public static JAXBElement<?> extractJAXBElement(AnyType anyType) {
        List<Object> content = anyType.getContent();

        if (content.size() > 1) {
            LOG.warn("More than one JAXBElement found in the same AnyType!");
        }

        for (Object obj : content) {
            if (obj instanceof JAXBElement<?>) {
                return (JAXBElement<?>) obj;
            }
        }

        return null;
    }

    /**
     * Filter all occurrences of '\n' out of the provided string.
     *
     * @param str the string to filter
     * @return a cleaned version of the provided string
     */
    public static String filterEndlines(String str) {
        str = str.replace("\n", "");
        return str.trim();
    }

    /**
     * Replaces line feed and carriage return characters.
     *
     * @param str the string with unwanted characters
     * @return the 'cleaned' string
     */
    public static String replaceUnwantedCharacters(String str, boolean crToo) {
        if (crToo) {
            str = str.replaceAll("\\r", "");
        }
        return str.replaceAll("\\n", " ");
    }

    /**
     * Deep copies a given object by serialisation.
     *
     * @param oldObj the old obj
     * @return the object
     * @throws Exception the exception
     */
    public static Object deepCopy(Object oldObj) throws Exception {
        if (oldObj == null) {
            return null;
        }
        ObjectInputStream ois = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        try {
            oos.writeObject(oldObj);
            oos.flush();
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bin);
            return ois.readObject();
        } catch (Exception e) {
            LOG.error("Exception in ObjectCloner = " + e.getMessage());
            throw (e);
        } finally {
            oos.close();
            if (ois != null) {
                ois.close();
            }
        }
    }

    /**
     * Converts a given <code>Date</code> to a new <code>XMLGregorianCalendar</code>.
     *
     * @param date the date
     * @return the new <code>XMLGregorianCalendar</code> or null
     */
    public static XMLGregorianCalendar createXMGregorianCalendar(Date date) {
        if (date != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            XMLGregorianCalendar gc = null;

            try {
                gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
                gc.setFractionalSecond(null);
                gc = gc.normalize();
            } catch (DatatypeConfigurationException ex) {
                LOG.warn("Unable to properly convert a Date to a XMLGregorianCalendarConverter", ex.getMessage());
            }

            return gc;
        }

        return null;
    }

    /**
     * Goes up in the panel hierarchy to find the correct parent dialog and sets it to invisible.
     *
     * @param evt the <code>ActionEvent</code>
     * @return true if the dialog was found and closed
     */
    public static boolean closeDialog(ActionEvent evt) {
        boolean closed = false;
        Container c = ((JButton) (evt.getSource())).getParent();
        while ((c.getParent() != null) && (!(c instanceof JDialog))) {
            c = c.getParent();
        }

        if (c instanceof JDialog) {
            JDialog d = (JDialog) c;
            d.setVisible(false);
            closed = true;
        }

        return closed;
    }

    /**
     * Sort the given items in a natural order. If an additional String is provided, it is put at the top of the items.
     *
     * @param items    the items
     * @param putOnTop the put on top
     */
    public static void sortItems(List<String> items, String putOnTop) {
        boolean topItemIn = items.contains(putOnTop);
        Collections.sort(items);
        if (topItemIn) {
            items.remove(putOnTop);
            items.add(0, putOnTop);
        }
    }

    /**
     * Checks wether a given url is accessible (results in HTTP 200).
     *
     * @param url the url to check
     * @return true, if the url is accessible
     */
    public static boolean checkURLExists(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");
            int responseCode = con.getResponseCode();

            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception ex) {
            LOG.warn(">>> Exception occurred while checking url: " + url + " with " + ex.getMessage());

            return false;
        }
    }

    /**
     * Retrieves all components in the hierarchy of a given container.
     *
     * @param c the container to start from
     * @return all components in that container
     */
    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }

        return compList;
    }

    /**
     * Compares two <code>XMLGregorianCalendar</code> with each other.
     *
     * @param first first calendar
     * @param scnd  second calendar
     * @return true, if the first is earlier than the second one
     */
    public static boolean isFirstDateEarlierOrEqualThanSecond(XMLGregorianCalendar first, XMLGregorianCalendar scnd) {
        first.setSecond(scnd.getSecond());
        int result = first.toGregorianCalendar().compareTo(scnd.toGregorianCalendar());

        if (result == -1 || result == 0) {
            return true;
        }

        return false;
    }
}