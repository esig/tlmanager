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

package eu.europa.ec.markt.tlmanager.core.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a representation of the 'Expected supervision/accreditation status flow for a single CSP service' which is described in Study on Cross-Border Interoperability of
 * eSignatures (CROBIES). Please note, that the exact keys are not hardcoded here but expected to be configured accordingly in 'tlmanager.properties'. However, only the last
 * keyword is essential to distinguish the state, so that it would still work with slightly changed configuration values.
 *
 * @version $Revision$ - $Date$
 */

public class StatusInformationFlow {

    private static final Logger LOG = LoggerFactory.getLogger(StatusInformationFlow.class);

    public static final String UNDER_SUPERVISION = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision";
    public static final String SUPERVISION_IN_CESSATION = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionincessation";
    public static final String SUPERVISION_CEASED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionceased";
    public static final String SUPERVISION_REVOKED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionrevoked";
    public static final String ACCREDITED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accredited";
    public static final String ACCREDITATION_CEASED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationceased";
    public static final String ACCREDITATION_REVOKED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationrevoked";
    public static final String SET_BY_NATIONAL_LAW = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/setbynationallaw";
    public static final String DEPRECATED_BY_NATIONAL_LAW = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/deprecatedbynationallaw";

    private static Map<String, Status> allStatus = new HashMap<String, Status>();

    private static Status underSupervision = new Status(UNDER_SUPERVISION, true);
    private static Status supervisionInCessation = new Status(SUPERVISION_IN_CESSATION, false);
    private static Status supervisionCeased = new Status(SUPERVISION_CEASED, false);
    private static Status supervisionRevoked = new Status(SUPERVISION_REVOKED, false);
    private static Status accredited = new Status(ACCREDITED, true);
    private static Status accreditationCeased = new Status(ACCREDITATION_CEASED, false);
    private static Status accreditationRevoked = new Status(ACCREDITATION_REVOKED, false);
    private static Status setByNationalLaw = new Status(SET_BY_NATIONAL_LAW, true);
    private static Status deprecatedByNationalLaw = new Status(DEPRECATED_BY_NATIONAL_LAW, false);

    /**
     * The default constructor for StatusInformationFlow.
     */
    static {

        init();
    }

    private StatusInformationFlow() {
    }

    /**
     * Finds the status with a name that matches the given name.
     *
     * @param name the string to match
     * @return the matching status
     */
    public static Status getMatchingStatus(final String name) {

        final Status status = allStatus.get(name);
        return status;
    }

    private static List<Status> createList(final Status... statuses) {

        final List<Status> list = new ArrayList<Status>();
        for (final Status status : statuses) {

            list.add(status);
        }
        return list;
    }

    private static void init() {

        // serviceStatuses = Configuration.getInstance().getTL().getTslServiceStatus();

        underSupervision.setOutGoing(createList(accredited, supervisionRevoked, supervisionCeased, supervisionInCessation));

        supervisionInCessation.setOutGoing(createList(supervisionRevoked, supervisionCeased));

        supervisionCeased.setOutGoing(createList(underSupervision));

        supervisionRevoked.setOutGoing(createList(underSupervision));

        accredited.setOutGoing(createList(accreditationCeased, accreditationRevoked));

        accreditationCeased.setOutGoing(createList(accreditationRevoked, accredited, underSupervision));

        accreditationRevoked.setOutGoing(createList(accredited, underSupervision));

        setByNationalLaw.setOutGoing(createList(deprecatedByNationalLaw));
        deprecatedByNationalLaw.setOutGoing(createList());
    }

    /**
     * A helping class to represent a single Status. <p/> <p/> DISCLAIMER: Project owner DG-MARKT.
     *
     * @author <a href="mailto:dgmarkt.Project-DSS@arhs-developments.com">ARHS Developments</a>
     * @version $Revision$ - $Date$
     */
    public static class Status {

        private boolean startPoint;
        private String name;
        private List<Status> outGoing;

        /**
         * The default constructor for Status.
         *
         * @param name       just the last part of the name
         * @param startPoint true, if this status can be a starting point
         */
        public Status(final String name, final boolean startPoint) {

            this.name = name;
            this.startPoint = startPoint;

            allStatus.put(this.name, this);
        }

        /**
         * @return the outGoing
         */
        public List<Status> getOutGoing() {
            return outGoing;
        }

        /**
         * @param outGoing the outGoing to set
         */
        protected void setOutGoing(final List<Status> outGoing) {
            this.outGoing = outGoing;
        }

        /**
         * @return the startPoint
         */
        public boolean isStartPoint() {
            return startPoint;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        public boolean hasOutgoingStatus(final Status status) {

            final boolean contains = outGoing.contains(status);
            return contains;
        }

        @Override
        public String toString() {
            return "Status{" +
                  "startPoint=" + startPoint +
                  ", name='" + name + '\'' +
                  ", outGoing=" + (outGoing == null ? "null" : outGoing.size()) +
                  '}';
        }
    }
}