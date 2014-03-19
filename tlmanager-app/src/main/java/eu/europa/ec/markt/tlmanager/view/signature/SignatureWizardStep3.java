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

package eu.europa.ec.markt.tlmanager.view.signature;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor.ValidatingPanel;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

import eu.europa.ec.markt.dss.DigestAlgorithm;
import eu.europa.ec.markt.dss.exception.DSSException;
import eu.europa.ec.markt.tlmanager.core.signature.SignatureManager;

/**
 * The third step of the signature wizard.
 *
 * @version $Revision$ - $Date$
 */

public class SignatureWizardStep3 implements ValidatingPanel<Object> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SignatureWizardStep4.class);

    private SignatureManager manager;
    private SignatureStep3 panel;

    /**
     * The default SignatureStep3tructor for SignatureWizardStep3.
     *
     * @param manager the manager
     */
    public SignatureWizardStep3(SignatureManager manager) {
        this.manager = manager;
        panel = new SignatureStep3(this);
    }

    /**
     * Sets the selected certificate.
     *
     * @param certificate the new selected certificate
     */
    public void setSelectedCertificate(Certificate certificate) {
        manager.setSelectedCertificate(certificate);
    }

    /**
     * Sets the selected digestAlgorithm.
     *
     * @param digestAlgorithm the new digestAlgorithm
     */
    public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
        manager.setDigestAlgorithm(digestAlgorithm);
    }

    /**
     * Sets the target.
     *
     * @param target the new target
     */
    public void setTarget(File target) {
        manager.setTarget(target);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void addChangeListener(ChangeListener arg0) {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Component getComponent() {
        return panel;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public HelpCtx getHelp() {
        return null;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void readSettings(Object arg0) {
        try {
            panel.setCertificates(manager.getCertificates());
            panel.setDigestAlgorithm(manager.getDigestAlgorithm());
        } catch (DSSException se) { // this should not happen because it's already checked in
            LOG.error(se.getMessage(), se);
            JOptionPane.showMessageDialog(panel, se.getMessage()); // step2#validate()
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void removeChangeListener(ChangeListener arg0) {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void storeSettings(Object arg0) {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void validate() throws WizardValidationException {
        if (manager.getSelectedCertificate() == null) {
            throw new WizardValidationException(null, "No certificate selected!", null);
        }
        if (manager.getTarget() == null) {
            throw new WizardValidationException(null, "No output file specified!", null);
        }

        // perform actual signing
        try {
            manager.sign();
        } catch (IOException ioe) {
            throw new WizardValidationException(null, ioe.getMessage(), null);
        }
    }
}