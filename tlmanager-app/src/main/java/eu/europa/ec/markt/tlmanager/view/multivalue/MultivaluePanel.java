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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.ItemDuplicator;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.common.ContentDialogCloser;
import eu.europa.ec.markt.tlmanager.view.multivalue.content.MultiContent;
import eu.europa.ec.markt.tlmanager.view.panel.ContentModel;

/**
 * A general MultivaluePanel consisting of a key list on the left side and an arbitrary <code>MultiContent</code> on the
 * right side. Depending on a <code>MultiMode</code>, the layout is changed slightly, allowing the key list to be
 * changed or not.
 *
 * @version $Revision$ - $Date$
 */

public class MultivaluePanel extends JPanel implements ContentDialogCloser {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MultivaluePanel.class);

    private MultiContent multiContent;
    private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeysComponents", Configuration.getInstance().getLocale());
    private DefaultListModel keyListModel;
    private List<ContentWatcher> contentWatcherListener;
    private ItemDuplicator itemDuplicator;

    // Data for content
    private String mandatoryValue;
    private String[] multiConfValues;

    private MultivalueCellRenderer multivalueCellRenderer;

    private boolean languageMode = false;
    private boolean isRefreshing = false;

    /**
     * Instantiates a new multivalue panel.
     *
     * @param multiMode       the multi mode
     * @param mandatoryValue  the mandatory value
     * @param multiConfValues the multi conf values
     */
    public MultivaluePanel(MultiMode multiMode, String mandatoryValue, String[] multiConfValues) {
        this.mandatoryValue = mandatoryValue;
        this.multiConfValues = multiConfValues;

        keyListModel = new DefaultListModel();
        multivalueCellRenderer = new MultivalueCellRenderer();
        contentWatcherListener = new ArrayList<ContentWatcher>();

        initComponents();
        setName(getClass().getSimpleName());
        instantiateContent(multiMode);
        keyList.setCellRenderer(multivalueCellRenderer);

        addListeners();

        notifyContentWatcher(multiContent.retrieveContentInformation());
    }

    private void addListeners() {
        keyList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !isRefreshing) {
                    final String selectedValue = (String) keyList.getSelectedValue();
                    multiContent.updateContent(selectedValue);
                }
            }
        });
    }

    private void instantiateContent(MultiMode multiMode) {
        multiContent = multiMode.createMultiContent(multiConfValues);
        if (multiMode.isMultiLanguage()) {
            setupLanguageMode();
        }
        final Component multiContentComponent = multiContent.getComponent();
        multiContentScrollPane.setViewportView(multiContentComponent);
        setName(multiContentComponent.getName() + "Panel");
    }

    /**
     * Refreshes the value of the <code>MultiContent</code> object explicitly.
     */
    public void refresh() {
        multiContent.refresh();

        isRefreshing = true; // prevent ListSelectionListener attached to keyList to be called
        keyListModel.clear(); // clear model...

        List<String> keys = multiContent.getMultiValueModel().getKeys();
        if (!keys.isEmpty()) {
            Util.sortItems(keys, Configuration.LanguageCodes.getEnglishLanguage());
            for (String key : keys) {
                keyListModel.addElement(key);
            }
        } else if (keyListModel.isEmpty()) {
            if (!languageMode) {
                addButtonActionPerformed(null);
            }
        }

        if (languageMode) {
            addLanguages(keys);
        }

        isRefreshing = false;

        // set current selection to the initialValue of the multivalue model
        String initialValueKey = multiContent.getMultiValueModel().getInitialValueKey();
        if (keyListModel.contains(initialValueKey)) {
            for (int i = 0; i < keyListModel.size(); i++) {
                if (keyListModel.get(i).equals(initialValueKey)) {
                    keyList.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            keyList.setSelectedIndex(0);
        }
    }

    /**
     * Duplicates the current language entry.
     */
    public void duplicateLanguageEntry() {
        int loc = itemDuplicator.duplicateLanguageEntry((String) keyList.getSelectedValue());
        keyList.setSelectedIndex(loc);
    }

    private void setupLanguageMode() {
        languageMode = true;
        disableButtons(true);
        multivalueCellRenderer.setMandatoryValue(mandatoryValue);
        keyList.addMouseListener(new ListPopupCreator(this));
        itemDuplicator = new ItemDuplicator(keyListModel);
    }

    private void disableButtons(boolean b) {
        addButton.setVisible(!b);
        removeButton.setVisible(!b);
    }

    private void addLanguages(List<String> alreadyThere) {
        for (String lang : Configuration.getInstance().getLanguageCodes().getCodes()) {
            if (!alreadyThere.contains(lang)) {
                keyListModel.addElement(lang);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        keyListScrollPane = new JScrollPane();
        keyList = new JList();
        multiContentScrollPane = new JScrollPane();
        textValue = new JTextPane();
        addButton = new JButton();
        removeButton = new JButton();
        closeButton = new JButton();

        keyList.setModel(keyListModel);
        keyListScrollPane.setViewportView(keyList);

        multiContentScrollPane.setViewportView(textValue);

        addButton.setText(uiKeys.getString("MultivaluePanel.add")); // NOI18N
        addButton.setName("add"); // NOI18N
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText(uiKeys.getString("MultivaluePanel.remove")); // NOI18N
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        closeButton.setText(uiKeys.getString("MultivaluePanel.close")); // NOI18N
        closeButton.setName("close"); // NOI18N
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(
              layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(keyListScrollPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(addButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(closeButton, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                          .addComponent(multiContentScrollPane, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING,
                    layout.createSequentialGroup().addComponent(keyListScrollPane, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(addButton))
                    .addComponent(multiContentScrollPane, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(removeButton).addComponent(closeButton)).addContainerGap()));
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_addButtonActionPerformed
        LOG.info("Button add called on " + multiContent);
        keyListModel.addElement(multiContent.createNewItem());
        keyList.setSelectedIndex(keyListModel.getSize() - 1); // always set to last (new) item
    }// GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_removeButtonActionPerformed
        String key = (String) keyList.getSelectedValue();
        if (key != null) {
            keyListModel.removeElement(key);
            multiContent.removeItem(key);
            keyList.setSelectedIndex(keyListModel.getSize() - 1); // always set to last (remaining) item
        }
    }// GEN-LAST:event_removeButtonActionPerformed

    private void closeButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_closeButtonActionPerformed
        boolean closed = Util.closeDialog(evt);
        if (closed) {
            dialogWasClosed();
        }
    }// GEN-LAST:event_closeButtonActionPerformed

    /*
     * Note: The getter and setter are important and used by beansbinding. cf. BindingManager
     */

    /**
     * Sets the multivalue model.
     *
     * @param multivalueValueModel the new multivalue model
     */
    public void setMultivalueModel(MultivalueModel multivalueValueModel) {
        multiContent.setMultiValueModel(multivalueValueModel);
    }

    /**
     * Gets the multivalue model.
     *
     * @return the multivalue model
     */
    public MultivalueModel getMultivalueModel() {
        return multiContent.getMultiValueModel();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addButton;
    private JButton closeButton;
    private JList keyList;
    private JScrollPane keyListScrollPane;
    private JScrollPane multiContentScrollPane;
    private JButton removeButton;
    private JTextPane textValue;
    // End of variables declaration//GEN-END:variables

    private class MultivalueCellRenderer extends DefaultListCellRenderer {
        private String mandatoryValue;

        /**
         * {@inheritDoc}
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                String label = value.toString();
                Object obj = multiContent.getValue(label);
                if (obj != null) {
                    if (obj instanceof ContentModel) {
                        ContentModel cm = (ContentModel) obj;
                        label = cm.isEmpty() ? "" + value : "*" + value;
                    } else {
                        String str = obj.toString();
                        if (str != null && !str.isEmpty() && !str.equals(Util.DEFAULT_NO_SELECTION_ENTRY)) {
                            label = "*" + value.toString();
                        }
                    }
                }
                super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);

                if (mandatoryValue != null && value.equals(mandatoryValue)) {
                    setForeground(Color.red);
                }
                return this;
            } else {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }

        /**
         * @param mandatoryValue the mandatoryValue to set
         */
        public void setMandatoryValue(String mandatoryValue) {
            this.mandatoryValue = mandatoryValue;
        }
    }

    /**
     * Provides the current content information.
     *
     * @return the content information
     */
    public String retrieveContentInformation() {
        return multiContent.retrieveContentInformation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dialogWasClosed() {
        multiContent.updateOnExit();
        notifyContentWatcher(multiContent.retrieveContentInformation());
        if (itemDuplicator != null) { // is only used in language mode
            itemDuplicator.reInit();
        }
    }

    // ##################### ContentWatcher ##########################

    /**
     * Adds a <code>ContentWatcher</code>.
     */
    public void addContentWatcher(ContentWatcher listener) {
        if (!contentWatcherListener.contains(listener)) {
            contentWatcherListener.add(listener);
        }
    }

    /**
     * Removes a <code>ContentWatcher</code>.
     *
     * @param listener the watcher
     */
    public void removeContentWatcher(ContentWatcher listener) {
        contentWatcherListener.remove(listener);
    }

    /**
     * Notify all <code>ContentWatcher</code>.
     *
     * @param text the text to tell the listener
     */
    public void notifyContentWatcher(String text) {
        for (ContentWatcher l : contentWatcherListener) {
            l.contentHasChanged(multiContent.getMultiValueModel().isEmpty(), text);
        }
    }
}