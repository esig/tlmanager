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
package eu.europa.ec.markt.tlmanager.view.pages;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.xml.bind.JAXBElement;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.core.QNames;
import eu.europa.ec.markt.tlmanager.model.KeyUsageTypeAdapter;
import eu.europa.ec.markt.tlmanager.model.treeNodes.ExtensionNode;
import eu.europa.ec.markt.tlmanager.model.treeNodes.TSLDataNode;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.binding.BindingManager;
import eu.europa.ec.markt.tlmanager.view.binding.KeyUsageTypeConverter;
import eu.europa.ec.markt.tlmanager.view.binding.ObjectIdentifierConverter;
import eu.europa.ec.markt.tlmanager.view.binding.PoliciesListConverter;
import eu.europa.ec.markt.tlmanager.view.binding.QualifiersTypeConverter;
import eu.europa.ec.markt.tlmanager.view.common.TitledPanel;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultiMode;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultivalueButton;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultivalueModel;
import eu.europa.ec.markt.tsl.jaxb.ecc.CriteriaListType;
import eu.europa.ec.markt.tsl.jaxb.ecc.KeyUsageBitType;
import eu.europa.ec.markt.tsl.jaxb.ecc.KeyUsageType;
import eu.europa.ec.markt.tsl.jaxb.ecc.QualificationElementType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionType;
import eu.europa.ec.markt.tsl.jaxb.tslx.CertSubjectDNAttributeType;
import eu.europa.ec.markt.tsl.jaxb.tslx.ExtendedKeyUsageType;
import eu.europa.ec.markt.tsl.jaxb.xades.AnyType;

/**
 * Content page for managing all below a <ecc:QualificationElement/>.
 *
 *
 */

public class QualificationExtensionPage extends TreeDataPublisher {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(QualificationExtensionPage.class);

    private DefaultComboBoxModel assertAttributeModel;

    private CriteriaListType criteriaList;

    /**
     * Instantiates a new qualification extension page.
     */
    public QualificationExtensionPage(JTree jtree) {
        super(jtree);
        String[] assertAttributes = Util.addNoSelectionEntry(Configuration.getInstance().getAssertAttributes());

        assertAttributeModel = new DefaultComboBoxModel(assertAttributes);

        initComponents();
        initBinding();
        sharedValuesTitle.setTitle(uiKeys.getString("QualificationExtensionPage.sharedValuesTitle.title"));
        qualificationTitle.setTitle(uiKeys.getString("QualificationExtensionPage.qualificationTitle.title"));
        criteriaListTitle.setTitle(uiKeys.getString("QualificationExtensionPage.criteriaListTitle.title"));
        otherCriteriaTitle.setTitle(uiKeys.getString("QualificationExtensionPage.otherCriteriaTitle.title"));

        additionalSetup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName() {
        setName(TreeDataPublisher.QUALIFICATION_EXTENSION_PAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupMandatoryLabels() {
        setMandatoryLabel(qualifierLabel);
        setMandatoryLabel(assertAttributeLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void changeMandatoryComponents(Component component, boolean failure) {
        super.changeMandatoryComponents(component, failure);

        criteriaListTitle.changeMandatoryState(isAnyValueSet());
    }

    private boolean isAnyValueSet() {
        final MultivalueModel multivalueModel = keyUsageButton.getMultivaluePanel().getMultivalueModel();
        if (multivalueModel instanceof KeyUsageTypeAdapter) {
            final KeyUsageTypeAdapter keyUsageTypeAdapter = (KeyUsageTypeAdapter) multivalueModel;
            final List<KeyUsageType> keyUsageTypeList = keyUsageTypeAdapter.getKeyUsageTypeList();
            if (keyUsageTypeList != null) {
                for (final KeyUsageType keyUsageType : keyUsageTypeList) {
                    for (final KeyUsageBitType keyUsageBitType : keyUsageType.getKeyUsageBit()) {
                        if (keyUsageBitType.isValue()) {
                            return true;
                        }
                    }
                }
            }
        }
        if (!policyIdentifier.isEmpty() || !extendedKeyUsage.isEmpty() || !certSubjectDNA.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        qualificationTitle = new TitledPanel();
        qualifierLabel = new JLabel();
        criteriaListTitle = new TitledPanel();
        policyIdentifierLabel = new JLabel();
        policyIdentifier = new MultivalueButton(MultiMode.MULTI_POLICIES, null, null);
        otherCriteriaTitle = new TitledPanel();
        extendedKeyUsageLabel = new JLabel();
        certSubjectDNALabel = new JLabel();
        certSubjectDNA = new MultivalueButton(MultiMode.MULTI_TEXT, null, null);
        extendedKeyUsage = new MultivalueButton(MultiMode.MULTI_TEXT, null, null);
        assertAttributeLabel = new JLabel();
        assertAttribute = new JComboBox();
        qualifierLabel1 = new JLabel();
        keyUsageButton = new MultivalueButton(MultiMode.MULTI_KEYUSAGE, null, null);
        qualifierButton = new MultivalueButton(MultiMode.MULTI_COMBOBOX, null, Util.addNoSelectionEntry(Configuration.getInstance().getQualifiers()));
        sharedValuesTitle = new TitledPanel();
        critical = new JCheckBox();

        qualifierLabel.setLabelFor(qualifierButton);
        qualifierLabel.setText(uiKeys.getString("QualificationExtensionPage.qualifier1Label.text")); // NOI18N

        policyIdentifierLabel.setLabelFor(policyIdentifier);
        policyIdentifierLabel.setText(uiKeys.getString("QualificationExtensionPage.policyIdentifierLabel.text")); // NOI18N

        extendedKeyUsageLabel.setLabelFor(extendedKeyUsage);
        extendedKeyUsageLabel.setText(uiKeys.getString("QualificationExtensionPage.extendedKeyUsageLabel.text")); // NOI18N

        certSubjectDNALabel.setLabelFor(certSubjectDNA);
        certSubjectDNALabel.setText(uiKeys.getString("QualificationExtensionPage.certSubjectDNALabel.text")); // NOI18N

        GroupLayout otherCriteriaTitleLayout = new GroupLayout(otherCriteriaTitle);
        otherCriteriaTitle.setLayout(otherCriteriaTitleLayout);
        otherCriteriaTitleLayout.setHorizontalGroup(otherCriteriaTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              otherCriteriaTitleLayout.createSequentialGroup().addContainerGap().addComponent(extendedKeyUsageLabel).addGap(26, 26, 26)
                    .addComponent(extendedKeyUsage, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(certSubjectDNALabel).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(certSubjectDNA, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        otherCriteriaTitleLayout.setVerticalGroup(otherCriteriaTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              otherCriteriaTitleLayout.createSequentialGroup().addGroup(otherCriteriaTitleLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(certSubjectDNA, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(certSubjectDNALabel)
                    .addComponent(extendedKeyUsageLabel).addComponent(extendedKeyUsage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        assertAttributeLabel.setLabelFor(assertAttribute);
        assertAttributeLabel.setText(uiKeys.getString("QualificationExtensionPage.assertAttributeLabel.text")); // NOI18N

        assertAttribute.setModel(assertAttributeModel);

        qualifierLabel1.setLabelFor(qualifierButton);
        qualifierLabel1.setText(uiKeys.getString("QualificationExtensionPage.keyUsageTitle.title")); // NOI18N

        GroupLayout criteriaListTitleLayout = new GroupLayout(criteriaListTitle);
        criteriaListTitle.setLayout(criteriaListTitleLayout);
        criteriaListTitleLayout.setHorizontalGroup(criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              criteriaListTitleLayout.createSequentialGroup().addGroup(criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                    criteriaListTitleLayout.createSequentialGroup().addGap(21, 21, 21).addComponent(policyIdentifierLabel).addGap(18, 18, 18)
                          .addComponent(policyIdentifier, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGroup(
                    criteriaListTitleLayout.createSequentialGroup().addContainerGap()
                          .addComponent(otherCriteriaTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGroup(
                    criteriaListTitleLayout.createSequentialGroup().addContainerGap()
                          .addGroup(criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(assertAttributeLabel).addComponent(qualifierLabel1))
                          .addGap(18, 18, 18).addGroup(criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                          .addComponent(keyUsageButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                          .addComponent(assertAttribute, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        criteriaListTitleLayout.setVerticalGroup(criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(criteriaListTitleLayout.createSequentialGroup()
              .addGroup(criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(assertAttribute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(assertAttributeLabel))
              .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                    criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(qualifierLabel1)
                          .addComponent(keyUsageButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(18, 18, Short.MAX_VALUE).addGroup(
                    criteriaListTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(policyIdentifierLabel, GroupLayout.Alignment.TRAILING)
                          .addComponent(policyIdentifier, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
              .addGap(18, 18, 18).addComponent(otherCriteriaTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap()));

        GroupLayout qualificationTitleLayout = new GroupLayout(qualificationTitle);
        qualificationTitle.setLayout(qualificationTitleLayout);
        qualificationTitleLayout.setHorizontalGroup(qualificationTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              qualificationTitleLayout.createSequentialGroup().addContainerGap().addGroup(qualificationTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(criteriaListTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
                          qualificationTitleLayout.createSequentialGroup().addComponent(qualifierLabel).addGap(18, 18, 18)
                                .addComponent(qualifierButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap()));
        qualificationTitleLayout.setVerticalGroup(qualificationTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              qualificationTitleLayout.createSequentialGroup().addGroup(qualificationTitleLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(qualifierLabel)
                    .addComponent(qualifierButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(38, 38, 38)
                    .addComponent(criteriaListTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        critical.setText(uiKeys.getString("QualificationExtensionPage.critical.text")); // NOI18N

        GroupLayout sharedValuesTitleLayout = new GroupLayout(sharedValuesTitle);
        sharedValuesTitle.setLayout(sharedValuesTitleLayout);
        sharedValuesTitleLayout.setHorizontalGroup(sharedValuesTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addGroup(sharedValuesTitleLayout.createSequentialGroup().addContainerGap().addComponent(critical).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        sharedValuesTitleLayout.setVerticalGroup(sharedValuesTitleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addGroup(sharedValuesTitleLayout.createSequentialGroup().addContainerGap().addComponent(critical).addContainerGap(17, Short.MAX_VALUE)));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(
              layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(sharedValuesTitle, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(qualificationTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
              layout.createSequentialGroup().addContainerGap().addComponent(sharedValuesTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(qualificationTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox assertAttribute;
    private JLabel assertAttributeLabel;
    private MultivalueButton certSubjectDNA;
    private JLabel certSubjectDNALabel;
    private TitledPanel criteriaListTitle;
    private JCheckBox critical;
    private MultivalueButton extendedKeyUsage;
    private JLabel extendedKeyUsageLabel;
    private MultivalueButton keyUsageButton;
    private TitledPanel otherCriteriaTitle;
    private MultivalueButton policyIdentifier;
    private JLabel policyIdentifierLabel;
    private TitledPanel qualificationTitle;
    private MultivalueButton qualifierButton;
    private JLabel qualifierLabel;
    private JLabel qualifierLabel1;
    private TitledPanel sharedValuesTitle;
    // End of variables declaration//GEN-END:variables
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */

    private void initBinding() {
        if (bindingManager == null) {
            bindingManager = new BindingManager(this);
        }

        bindingManager.createBindingForComponent(critical, "critical", QNames._QualificationsCritical);

        bindingManager.createBindingForComponent(keyUsageButton.getMultivaluePanel(), "keyUsage", QNames._QualificationsKeyUsage, new KeyUsageTypeConverter());

        bindingManager.createBindingForComponent(qualifierButton.getMultivaluePanel(), "qualifiers", QNames._QualificationsQualifiers, new QualifiersTypeConverter());

        bindingManager.createBindingForComponent(assertAttribute, "assert", QNames._QualificationsAssert);

        bindingManager.createBindingForComponent(policyIdentifier.getMultivaluePanel(), "policySet", QNames._QualificationsPoliciesList, new PoliciesListConverter());

        bindingManager
              .createBindingForComponent(extendedKeyUsage.getMultivaluePanel(), "keyPurposeId", QNames._ExtendedKeyUsage_QNAME.getLocalPart(), new ObjectIdentifierConverter());

        bindingManager
              .createBindingForComponent(certSubjectDNA.getMultivaluePanel(), "attributeOID", QNames._CertSubjectDNAttribute_QNAME.getLocalPart(), new ObjectIdentifierConverter());

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateViewFromData(TSLDataNode dataNode) {
        this.dataNode = dataNode;
        QualificationElementType qualificationElement = (QualificationElementType) dataNode.getUserObject();

        LOG.debug( "Value changed {}", qualificationElement);

        ExtensionNode extensionNode = (ExtensionNode) dataNode.getParent();
        ExtensionType qualificationExtension = extensionNode.getQualificationExtension();

        if (qualificationExtension == null) {
            LOG.error(">>>No associated ExtensionType found for the current QualificationElementType!");
        }

        CriteriaListType criteriaList = qualificationElement.getCriteriaList();
        this.criteriaList = criteriaList;

        AnyType otherCriteriaList = criteriaList.getOtherCriteriaList();
        List<Object> content = otherCriteriaList.getContent();

        ExtendedKeyUsageType ekut = null;
        CertSubjectDNAttributeType csdat = null;

        for (Object obj : content) {
            if (obj instanceof JAXBElement<?>) {
                JAXBElement<?> element = (JAXBElement<?>) obj;
                if (element.getName().equals(QNames._ExtendedKeyUsage_QNAME)) {
                    ekut = (ExtendedKeyUsageType) element.getValue();
                } else if (element.getName().equals(QNames._CertSubjectDNAttribute_QNAME)) {
                    csdat = (CertSubjectDNAttributeType) element.getValue();
                }
            }
        }

        bindingManager.unbindAll();

        bindingManager.amendSourceForBinding(qualificationExtension, QNames._QualificationsCritical);
        bindingManager.amendSourceForBinding(criteriaList, QNames._QualificationsKeyUsage);
        bindingManager.amendSourceForBinding(qualificationElement, QNames._QualificationsQualifiers);
        bindingManager.amendSourceForBinding(criteriaList, QNames._QualificationsAssert);

        bindingManager.amendSourceForBinding(criteriaList, QNames._QualificationsPoliciesList);
        bindingManager.amendSourceForBinding(ekut, QNames._ExtendedKeyUsage_QNAME.getLocalPart());
        bindingManager.amendSourceForBinding(csdat, QNames._CertSubjectDNAttribute_QNAME.getLocalPart());

        bindingManager.bindAll();

        // update all the preview information on the multivalue buttons
        policyIdentifier.refreshContentInformation();
        extendedKeyUsage.refreshContentInformation();
        certSubjectDNA.refreshContentInformation();
        qualifierButton.refreshContentInformation();
        keyUsageButton.refreshContentInformation();
    }
}