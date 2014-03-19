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

package eu.europa.ec.markt.tlmanager.view.panel;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.security.auth.x500.X500Principal;
import javax.swing.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.certificate.DigitalIdentityModel;
import eu.europa.ec.markt.tlmanager.view.common.ContentDialogCloser;
import eu.europa.ec.markt.tlmanager.view.multivalue.ContentWatcher;

/**
 * A panel which allows uploading a certificate and displays its data.
 *
 * @version $Revision: 2497 $ - $Date: 2013-09-05 17:30:51 +0200 (Thu, 05 Sep 2013) $
 */

public class DigitalIdentityCertificatePanel extends JPanel implements ContentDialogCloser {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalIdentityCertificatePanel.class);
    private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeysComponents", Configuration.getInstance().getLocale());

    private List<ContentWatcher> contentWatcherListener;
    private JFileChooser fileChooser;
    private DigitalIdentityModel digitalIdentityModel;

    private DefaultListModel keyUsageModel;

    /**
     * The default constructor for DigitalIdentityPanel.
     */
    public DigitalIdentityCertificatePanel() {
        this.fileChooser = new JFileChooser();
        contentWatcherListener = new ArrayList<ContentWatcher>();
        keyUsageModel = new DefaultListModel();

        initComponents();
        certificateTitle.setTitle(uiKeys.getString("DigitalIdentityPanel.title"));

        notifyContentWatcher();
    }

    /**
     * Another constructor for DigitalIdentityPanel.
     */
    public DigitalIdentityCertificatePanel(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
        contentWatcherListener = new ArrayList<ContentWatcher>();
        keyUsageModel = new DefaultListModel();

        initComponents();
        certificateTitle.setTitle(uiKeys.getString("DigitalIdentityPanel.title"));

        notifyContentWatcher();
    }

    private void loadCertificate(File file) {
        FileInputStream inputStream = null;

        Security.addProvider(new BouncyCastleProvider());

        try {
            inputStream = new FileInputStream(file);

            X509Certificate cert = DSSUtils.loadCertificate(inputStream);
            digitalIdentityModel.setCertificate(cert);
            refresh();

        } catch (Exception ex) {
            String message = uiKeys.getString("DigitalIdentityPanel.error.message");
            JOptionPane.showMessageDialog(this, message, uiKeys.getString("DigitalIdentityPanel.error.title"), JOptionPane.INFORMATION_MESSAGE);
            LOG.warn(message + " " + ex.getMessage(), ex);
        }
    }

    /**
     *
     */
    public void refresh() {
        // clean data
        subject.setText("");
        issuer.setText("");
        serial.setText("");
        keyUsageModel.clear();
        if (digitalIdentityModel != null) {
            X509Certificate certificate = digitalIdentityModel.getCertificate();
            if (certificate != null) {
                final X500Principal subjectX500Principal = DSSUtils.getSubjectX500Principal(certificate);
                final X500Principal issuerX500Principal = DSSUtils.getIssuerX500Principal(certificate);
                final BigInteger serialNumber = certificate.getSerialNumber();



                if (subjectX500Principal != null) {

                    final String subjectName = subjectX500Principal.getName();
                    subject.setText(subjectName);
                }

                if (issuerX500Principal != null) {

                    final String issuerName = issuerX500Principal.getName();
                    issuer.setText(issuerName);
                }

                if (serialNumber != null) {
                    serial.setText(serialNumber.toString());
                }

                List<String> keyUsages = decodeKeyUsages(certificate.getKeyUsage());

                try {
                    List<String> extendedKeyUsages = certificate.getExtendedKeyUsage();
                    if (extendedKeyUsages != null) {
                        keyUsages.addAll(extendedKeyUsages);
                    }
                } catch (CertificateParsingException cpe) {
                    LOG.warn("Unable to parse extended key usages: " + cpe.getMessage());
                }

                for (String keyUsage : keyUsages) {
                    keyUsageModel.addElement(keyUsage);
                }
            }
            notifyContentWatcher();
        }
    }

    private List<String> decodeKeyUsages(boolean[] bits) {
        List<String> list = new ArrayList<String>();
        if (bits != null) {
            if (bits.length != 9) {
                LOG.error(">>>Unexpected length of KeyUsage Attribute!");
            } else {
                if (bits[0]) {
                    list.add("digital_signature");
                }
                if (bits[1]) {
                    list.add("non_repudiation");
                }
                if (bits[2]) {
                    list.add("key_encipherment");
                }
                if (bits[3]) {
                    list.add("data_encipherment");
                }
                if (bits[4]) {
                    list.add("key_agreement");
                }
                if (bits[5]) {
                    list.add("key_certsign");
                }
                if (bits[6]) {
                    list.add("crl_sign");
                }
                if (bits[7]) {
                    list.add("encipher_only");
                }
                if (bits[8]) {
                    list.add("decipher_only");
                }
            }
        }
        return list;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectCertificate = new javax.swing.JButton();
        certificateTitle = new eu.europa.ec.markt.tlmanager.view.common.TitledPanel();
        issuerScrollPane = new javax.swing.JScrollPane();
        issuer = new javax.swing.JTextArea();
        issuerLabel = new javax.swing.JLabel();
        subjectScrollPane = new javax.swing.JScrollPane();
        subject = new javax.swing.JTextArea();
        subjectLabel = new javax.swing.JLabel();
        keyUsagesLabel = new javax.swing.JLabel();
        serialLabel = new javax.swing.JLabel();
        serial = new javax.swing.JTextField();
        keyUsagesScrollPane = new javax.swing.JScrollPane();
        keyUsages = new javax.swing.JList();

        setName("DigitalIdentityPanel"); // NOI18N

        selectCertificate.setText(uiKeys.getString("DigitalIdentityPanel.select")); // NOI18N
        selectCertificate.setName("selectCertificate"); // NOI18N
        selectCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCertificateActionPerformed(evt);
            }
        });

        issuer.setColumns(5);
        issuer.setEditable(false);
        issuer.setLineWrap(true);
        issuer.setRows(3);
        issuer.setWrapStyleWord(true);
        issuerScrollPane.setViewportView(issuer);

        issuerLabel.setText(uiKeys.getString("DigitalIdentityPanel.issuer")); // NOI18N

        subject.setColumns(5);
        subject.setEditable(false);
        subject.setLineWrap(true);
        subject.setRows(3);
        subject.setWrapStyleWord(true);
        subjectScrollPane.setViewportView(subject);

        subjectLabel.setText(uiKeys.getString("DigitalIdentityPanel.subject")); // NOI18N

        keyUsagesLabel.setText(uiKeys.getString("DigitalIdentityPanel.keyUsages")); // NOI18N

        serialLabel.setText(uiKeys.getString("DigitalIdentityPanel.serial")); // NOI18N

        serial.setEditable(false);

        keyUsages.setModel(keyUsageModel);
        keyUsagesScrollPane.setViewportView(keyUsages);

        javax.swing.GroupLayout certificateTitleLayout = new javax.swing.GroupLayout(certificateTitle);
        certificateTitle.setLayout(certificateTitleLayout);
        certificateTitleLayout.setHorizontalGroup(
            certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(certificateTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keyUsagesLabel)
                    .addComponent(serialLabel)
                    .addComponent(issuerLabel)
                    .addComponent(subjectLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(issuerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(keyUsagesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(serial, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(subjectScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                .addContainerGap())
        );
        certificateTitleLayout.setVerticalGroup(
            certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(certificateTitleLayout.createSequentialGroup()
                .addGroup(certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(subjectLabel)
                    .addComponent(subjectScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(issuerLabel)
                    .addComponent(issuerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serialLabel))
                .addGap(18, 18, 18)
                .addGroup(certificateTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keyUsagesLabel)
                    .addComponent(keyUsagesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(selectCertificate)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(certificateTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(certificateTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(selectCertificate)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectCertificateActionPerformed(ActionEvent evt) {// GEN-FIRST:event_selectCertificateActionPerformed
        int returnValue = fileChooser.showOpenDialog(getRootPane());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadCertificate(selectedFile);
        }
    }// GEN-LAST:event_selectCertificateActionPerformed

    private void closeButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_closeButtonActionPerformed
        boolean closed = Util.closeDialog(evt);
        if (closed) {
            dialogWasClosed();
        }
    }// GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private eu.europa.ec.markt.tlmanager.view.common.TitledPanel certificateTitle;
    private javax.swing.JTextArea issuer;
    private javax.swing.JLabel issuerLabel;
    private javax.swing.JScrollPane issuerScrollPane;
    private javax.swing.JList keyUsages;
    private javax.swing.JLabel keyUsagesLabel;
    private javax.swing.JScrollPane keyUsagesScrollPane;
    private javax.swing.JButton selectCertificate;
    private javax.swing.JTextField serial;
    private javax.swing.JLabel serialLabel;
    private javax.swing.JTextArea subject;
    private javax.swing.JLabel subjectLabel;
    private javax.swing.JScrollPane subjectScrollPane;
    // End of variables declaration//GEN-END:variables
    /*
     * (non-Javadoc)
     * 
     * @see eu.europa.ec.markt.tlmanager.view.common.ContentDialogCloser#dialogWasClosed()
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void dialogWasClosed() {
        digitalIdentityModel.updateDigitalIdentity();
    }

    /**
     * @return the digitalIdentityModel
     */
    public DigitalIdentityModel getDigitalIdentityModel() {
        return digitalIdentityModel;
    }

    /**
     * Sets the certificate model. Used by Binding !
     *
     * @param digitalIdentityModel the new certificate model
     */
    public void setDigitalIdentityModel(DigitalIdentityModel digitalIdentityModel) {
        this.digitalIdentityModel = digitalIdentityModel;
        refresh();
    }

    /**
     * Provides the current content information.
     *
     * @return the content information
     */
    public String retrieveContentInformation() {

        String info = "";
        if (digitalIdentityModel != null) {

            X509Certificate certificate = digitalIdentityModel.getCertificate();
            if (certificate != null) {

                final X500Principal subjectX500Principal = DSSUtils.getSubjectX500Principal(certificate);
                if (subjectX500Principal != null) {

                    final String subjectName = subjectX500Principal.getName();
                    info = subjectName;
                } else {

                    // Note: this is actually a 'misuse' of the no selection entry, but here it just
                    // expresses, that there is no subject name for a certificate. The user can only
                    // change it by providing a different certificate. However, it's still possible.
                    info = Util.DEFAULT_NO_SELECTION_ENTRY;
                }
            }
        }
        return info;
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
     */
    public void notifyContentWatcher() {
        for (ContentWatcher l : contentWatcherListener) {
            String contentInfo = retrieveContentInformation();
            l.contentHasChanged(contentInfo.isEmpty(), contentInfo);
        }
    }
}
