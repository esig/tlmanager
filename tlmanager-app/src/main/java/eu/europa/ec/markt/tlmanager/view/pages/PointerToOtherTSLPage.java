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

package eu.europa.ec.markt.tlmanager.view.pages;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.core.QNames;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLDataNode;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.binding.*;
import eu.europa.ec.markt.tlmanager.view.common.TitledPanel;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultiMode;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultivalueButton;
import eu.europa.ec.markt.tsl.jaxb.tsl.*;

import javax.swing.*;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.List;

/**
 * Content page for managing all below a <tsl:OtherTSLPointer/>.
 *
 * @version $Revision$ - $Date$
 */

public class PointerToOtherTSLPage extends TreeDataPublisher {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PointerToOtherTSLPage.class);

    private DefaultComboBoxModel schemeTerritoryModel;
    private DefaultComboBoxModel tslTypeModel;
    private DefaultComboBoxModel mimeTypeModel;

    /**
     * Instantiates a new pointer to other tsl page.
     */
    public PointerToOtherTSLPage(JTree jtree) {
        super(jtree);
        String[] territoryItems = Util.addNoSelectionEntry(Configuration.getInstance().getCountryCodes().getCodes());
        schemeTerritoryModel = new DefaultComboBoxModel(territoryItems);
        String[] tslTypeItems = new String[]{Configuration.getInstance().getTSL().getTslTypeInverse()};
        tslTypeModel = new DefaultComboBoxModel(tslTypeItems);

        String[] mimeTypeItems = Util.addNoSelectionEntry(Configuration.getInstance().getMimeTypes());
        mimeTypeModel = new DefaultComboBoxModel(mimeTypeItems);
        initComponents();
        pointerTitle.setTitle(uiKeys.getString("PointerToOtherTSLPage.pointerTitle.title"));
        toggleMode();
        initBinding();

        additionalSetup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupListenersForTreeLabelComponents() {
        schemeTerritory.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (!bindingManager.isBindingInProgress() && e.getStateChange() == ItemEvent.SELECTED && dataNode != null) {
                    dataNode.resetLabel();
                }
            }
        });

        tslLocation.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (dataNode != null) {
                    dataNode.resetLabel();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName() {
        setName(TreeDataPublisher.POINTER_TO_OTHER_TSL_PAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupMandatoryLabels() {
        setMandatoryLabel(digitalIdLabel);
        setMandatoryLabel(tslLocationLabel);
        setMandatoryLabel(schemeOperatorNameLabel);
        if (!Configuration.getInstance().isTlMode()) {
            setMandatoryLabel(schemeTypeCommunityRuleLabel);
        }
        setMandatoryLabel(schemeTerritoryLabel);
        setMandatoryLabel(mimeTypeLabel);
    }

    private void toggleMode() {
        if (Configuration.getInstance().isTlMode()) {
            schemeTypeCommunityRuleTL = new JTextField();
            schemeTypeCommunityRuleTL.setEditable(false);
            schemeTypeCommunityRuleLabel.setLabelFor(schemeTypeCommunityRuleTL);

            schemeTypeCommunityRuleComponent = schemeTypeCommunityRuleTL;
        } else {
            schemeTypeCommunityRuleLOTL = new MultivalueButton(MultiMode.MULTILANG_COMBOBOX, null,
                  Util.addNoSelectionEntry(Configuration.getInstance().getTL().getTslSchemeTypeCommunityRules()));
            schemeTypeCommunityRuleLabel.setLabelFor(schemeTypeCommunityRuleLOTL);

            schemeTypeCommunityRuleComponent = schemeTypeCommunityRuleLOTL;
        }

        schemeTypeCommunityRuleContainer.removeAll();
        schemeTypeCommunityRuleContainer.setLayout(new BorderLayout());
        schemeTypeCommunityRuleContainer.add(schemeTypeCommunityRuleComponent, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pointerTitle = new TitledPanel();
        digitalIdLabel = new JLabel();
        tslLocationLabel = new JLabel();
        tslLocation = new JTextField();
        schemeOperatorNameLabel = new JLabel();
        schemeOperatorName = new MultivalueButton(MultiMode.MULTILANG_TEXT, Configuration.LanguageCodes.getEnglishLanguage(), null);
        schemeTypeCommunityRuleLabel = new JLabel();
        schemeTypeCommunityRuleContainer = new JPanel();
        schemeTerritoryLabel = new JLabel();
        schemeTerritory = new JComboBox();
        mimeTypeLabel = new JLabel();
        mimeType = new JComboBox();
        digitalIdButton = new MultivalueButton(MultiMode.MULTI_SERVICE_ID, null, null);
        jLabel1 = new JLabel();
        tslType = new JComboBox();

        pointerTitle.setName("pointerTitle"); // NOI18N

        digitalIdLabel.setLabelFor(digitalIdButton);
        digitalIdLabel.setText(uiKeys.getString("PointerToOtherTSLPage.digitalIdLabel.text")); // NOI18N

        tslLocationLabel.setLabelFor(tslLocation);
        tslLocationLabel.setText(uiKeys.getString("PointerToOtherTSLPage.tslLocationLabel.text")); // NOI18N

        tslLocation.setName("tslLocation"); // NOI18N

        schemeOperatorNameLabel.setLabelFor(schemeOperatorName);
        schemeOperatorNameLabel.setText(uiKeys.getString("PointerToOtherTSLPage.schemeOperatorNameLabel.text")); // NOI18N

        schemeOperatorName.setName("schemeOperatorName"); // NOI18N

        schemeTypeCommunityRuleLabel.setText(uiKeys.getString("PointerToOtherTSLPage.schemeTypeCommunityRuleLabel.text")); // NOI18N

        schemeTypeCommunityRuleContainer.setMinimumSize(new Dimension(0, 0));
        schemeTypeCommunityRuleContainer.setName("schemeTypeCommunityRuleContainer"); // NOI18N

        GroupLayout schemeTypeCommunityRuleContainerLayout = new GroupLayout(schemeTypeCommunityRuleContainer);
        schemeTypeCommunityRuleContainer.setLayout(schemeTypeCommunityRuleContainerLayout);
        schemeTypeCommunityRuleContainerLayout
              .setHorizontalGroup(schemeTypeCommunityRuleContainerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 396, Short.MAX_VALUE));
        schemeTypeCommunityRuleContainerLayout
              .setVerticalGroup(schemeTypeCommunityRuleContainerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 23, Short.MAX_VALUE));

        schemeTerritoryLabel.setLabelFor(schemeTerritory);
        schemeTerritoryLabel.setText(uiKeys.getString("PointerToOtherTSLPage.schemeTerritoryLabel.text")); // NOI18N

        schemeTerritory.setEditable(!Configuration.getInstance().isEuMode());
        schemeTerritory.setModel(schemeTerritoryModel);
        schemeTerritory.setName("schemeTerritory"); // NOI18N

        mimeTypeLabel.setLabelFor(mimeType);
        mimeTypeLabel.setText(uiKeys.getString("PointerToOtherTSLPage.mimeTypeLabel.text")); // NOI18N

        mimeType.setModel(mimeTypeModel);
        mimeType.setName("mimeType"); // NOI18N

        digitalIdButton.setName("digitalId"); // NOI18N

        jLabel1.setText("TSL Type");

        tslType.setEditable(!Configuration.getInstance().isEuMode());
        tslType.setModel(tslTypeModel);
        tslType.setName("tslType"); // NOI18N

        GroupLayout pointerTitleLayout = new GroupLayout(pointerTitle);
        pointerTitle.setLayout(pointerTitleLayout);
        pointerTitleLayout.setHorizontalGroup(pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              pointerTitleLayout.createSequentialGroup().addContainerGap().addGroup(pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                    pointerTitleLayout.createSequentialGroup().addGroup(
                          pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tslLocationLabel).addComponent(schemeOperatorNameLabel)
                                .addComponent(schemeTypeCommunityRuleLabel).addComponent(digitalIdLabel)).addGroup(
                          pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(pointerTitleLayout.createSequentialGroup().addGap(18, 18, 18)
                                .addComponent(schemeTypeCommunityRuleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 31, Short.MAX_VALUE)).addGroup(pointerTitleLayout.createSequentialGroup().addGap(21, 21, 21).addGroup(
                                pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tslLocation).addGroup(pointerTitleLayout.createSequentialGroup()
                                      .addGroup(pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                                            pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                  .addComponent(digitalIdButton, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                                                  .addComponent(schemeTerritory, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                                  .addComponent(mimeType, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                                  .addComponent(schemeOperatorName, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                                            .addComponent(tslType, GroupLayout.PREFERRED_SIZE, 394, GroupLayout.PREFERRED_SIZE)).addGap(0, 0, Short.MAX_VALUE)))))).addGroup(
                    pointerTitleLayout.createSequentialGroup().addGroup(
                          pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(schemeTerritoryLabel).addComponent(mimeTypeLabel)
                                .addComponent(jLabel1)).addGap(0, 0, Short.MAX_VALUE)))));
        pointerTitleLayout.setVerticalGroup(pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(pointerTitleLayout.createSequentialGroup().addGroup(
              pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(schemeTypeCommunityRuleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGroup(
                    pointerTitleLayout.createSequentialGroup().addGroup(pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(digitalIdLabel)
                          .addComponent(digitalIdButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(
                          pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel1)
                                .addComponent(tslType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(
                          pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(tslLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(tslLocationLabel))
                          .addGap(29, 29, 29).addGroup(pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(schemeOperatorNameLabel)
                          .addComponent(schemeOperatorName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(23, 23, 23)
                          .addComponent(schemeTypeCommunityRuleLabel))).addGap(18, 18, 18).addGroup(
              pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(schemeTerritoryLabel)
                    .addComponent(schemeTerritory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(30, 30, 30).addGroup(
              pointerTitleLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(mimeTypeLabel)
                    .addComponent(mimeType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              layout.createSequentialGroup().addContainerGap().addComponent(pointerTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              layout.createSequentialGroup().addContainerGap().addComponent(pointerTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private MultivalueButton digitalIdButton;
    private JLabel digitalIdLabel;
    private JLabel jLabel1;
    private JComboBox mimeType;
    private JLabel mimeTypeLabel;
    private TitledPanel pointerTitle;
    private MultivalueButton schemeOperatorName;
    private JLabel schemeOperatorNameLabel;
    private JComboBox schemeTerritory;
    private JLabel schemeTerritoryLabel;
    private JPanel schemeTypeCommunityRuleContainer;
    private JLabel schemeTypeCommunityRuleLabel;
    private JTextField tslLocation;
    private JLabel tslLocationLabel;
    private JComboBox tslType;
    // End of variables declaration//GEN-END:variables

    // custom components
    private JComponent schemeTypeCommunityRuleComponent;
    private MultivalueButton schemeTypeCommunityRuleLOTL;
    private JTextField schemeTypeCommunityRuleTL;

    private void initBinding() {
        if (bindingManager == null) {
            bindingManager = new BindingManager(this);
        }

        bindingManager.createBindingForComponent(tslLocation, "TSLLocation", QNames._TSLLocation);

        bindingManager.createBindingForComponent(digitalIdButton.getMultivaluePanel(), "serviceDigitalIdentities", QNames._ServiceDigitalIdentities_QNAME.getLocalPart());
        bindingManager.appendConverter(new ServiceDigitalIdentitiesConverter(), QNames._ServiceDigitalIdentities_QNAME.getLocalPart());

        bindingManager.createBindingForComponent(schemeOperatorName.getMultivaluePanel(), "value", QNames._SchemeOperatorName_QNAME.getLocalPart());
        bindingManager.appendConverter(new InternationalNamesConverter(), QNames._SchemeOperatorName_QNAME.getLocalPart());

        if (Configuration.getInstance().isTlMode()) {
            bindingManager.createBindingForComponent(schemeTypeCommunityRuleTL, "value", QNames._SchemeTypeCommunityRules_QNAME.getLocalPart());
            bindingManager.appendConverter(new NonEmptyURIListToStringConverter(), QNames._SchemeTypeCommunityRules_QNAME.getLocalPart());
        } else {
            bindingManager.createBindingForComponent(schemeTypeCommunityRuleLOTL.getMultivaluePanel(), "value", QNames._SchemeTypeCommunityRules_QNAME.getLocalPart());
            bindingManager.appendConverter(new NonEmptyMultiLangURIListConverter(), QNames._SchemeTypeCommunityRules_QNAME.getLocalPart());
        }

        bindingManager.createBindingForComponent(schemeTerritory, "value", QNames._SchemeTerritory_QNAME.getLocalPart());
        bindingManager.createBindingForComponent(mimeType, "value", QNames._MimeType_QNAME.getLocalPart());
        bindingManager.createBindingForComponent(tslType, "value", QNames._TSLType_QNAME.getLocalPart());

    }

    private Object getAdditionalDataNode(QName qname) {
        OtherTSLPointerType pointer = (OtherTSLPointerType) dataNode.getUserObject();
        List<Serializable> textualInformationOrOtherInformation = pointer.getAdditionalInformation().getTextualInformationOrOtherInformation();
        for (Object obj : textualInformationOrOtherInformation) {
            if (obj instanceof AnyType) {
                AnyType anyType = (AnyType) obj;
                List<Object> content = anyType.getContent();
                JAXBElement<Object> element = null;
                if (content.isEmpty()) {
                    continue;
                }
                Object object = content.get(0);
                if (object != null && object instanceof JAXBElement<?>) {
                    element = (JAXBElement<Object>) object;
                }
                if (element != null && object != null) {
                    // tsl:SchemeOperatorName
                    if (element.getName().getLocalPart().equals(qname.getLocalPart())) {
                        return object;
                    }
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateViewFromData(TSLDataNode dataNode) {
        this.dataNode = dataNode;
        OtherTSLPointerType pointer = (OtherTSLPointerType) dataNode.getUserObject();
        LOG.debug( "Value changed {}", pointer);
        bindingManager.unbindAll();

        bindingManager.amendSourceForBinding(pointer, QNames._ServiceDigitalIdentities_QNAME.getLocalPart());
        bindingManager.amendSourceForBinding(pointer, QNames._TSLLocation);

        {
            JAXBElement<InternationalNamesType> concreteElement = (JAXBElement<InternationalNamesType>) getAdditionalDataNode(QNames._SchemeOperatorName_QNAME);
            bindingManager.amendSourceForBinding(concreteElement, QNames._SchemeOperatorName_QNAME.getLocalPart());
        }
        {
            JAXBElement<NonEmptyMultiLangURIListType> concreteElement = (JAXBElement<NonEmptyMultiLangURIListType>) getAdditionalDataNode(QNames._SchemeTypeCommunityRules_QNAME);
            bindingManager.amendSourceForBinding(concreteElement, QNames._SchemeTypeCommunityRules_QNAME.getLocalPart());
            if (Configuration.getInstance().isTlMode() && concreteElement !=null && concreteElement.getValue() !=null && concreteElement.getValue().getURI() != null && concreteElement.getValue().getURI().isEmpty()) {
                // if in tl mode: take the community rules string from the lotl
                String[] stcr = Configuration.getInstance().getLOTL().getTslSchemeTypeCommunityRules();
                final NonEmptyMultiLangURIType nonEmptyMultiLangURIType = new NonEmptyMultiLangURIType();
                nonEmptyMultiLangURIType.setValue(stcr[0]);
                //TODO: missing default language
                concreteElement.getValue().getURI().add(nonEmptyMultiLangURIType); // only one value expected
            }
        }
        {
            JAXBElement<String> concreteElement = (JAXBElement<String>) getAdditionalDataNode(QNames._SchemeTerritory_QNAME);
            bindingManager.amendSourceForBinding(concreteElement, QNames._SchemeTerritory_QNAME.getLocalPart());
        }
        {
            JAXBElement<String> concreteElement = (JAXBElement<String>) getAdditionalDataNode(QNames._MimeType_QNAME);
            bindingManager.amendSourceForBinding(concreteElement, QNames._MimeType_QNAME.getLocalPart());
        }
        {
            JAXBElement<String> concreteElement = (JAXBElement<String>) getAdditionalDataNode(QNames._TSLType_QNAME);
            bindingManager.amendSourceForBinding(concreteElement, QNames._TSLType_QNAME.getLocalPart());
        }

        bindingManager.bindAll();

        // refresh all the content information on the multivalue buttons
        schemeOperatorName.refreshContentInformation();
        digitalIdButton.refreshContentInformation();
        if (!Configuration.getInstance().isTlMode()) {
            schemeTypeCommunityRuleLOTL.refreshContentInformation();
        }
    }
}