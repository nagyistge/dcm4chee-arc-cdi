/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Accurate Software Design, LLC.
 * Portions created by the Initial Developer are Copyright (C) 2006-2008
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See listed authors below.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package org.dcm4chee.archive.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.PersonName;
import org.dcm4che3.data.Tag;
import org.dcm4che3.soundex.FuzzyStr;
import org.dcm4che3.util.DateUtils;
import org.dcm4chee.archive.conf.AttributeFilter;

/**
 * @author Damien Evans <damien.daddy@gmail.com>
 * @author Justin Falk <jfalkmu@gmail.com>
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @version $Revision$ $Date$
 * @since Feb 29, 2008
 */
@Entity
@Table(name = "mwl_item")
public class MWLItem implements Serializable {

    public static final String SCHEDULED = "SCHEDULED";
    public static final String ARRIVED = "ARRIVED";
    public static final String READY = "READY";
    public static final String STARTED = "STARTED";
    public static final String DEPARTED = "DEPARTED";
    public static final String COMPLETED = "COMPLETED";
    public static final String DISCONTINUED = "DISCONTINUED";

    private static final long serialVersionUID = 5655030469102270878L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "pk")
    private long pk;

    @Basic(optional = false)
    @Column(name = "created_time", updatable = false)
    private Date createdTime;

    @Basic(optional = false)
    @Column(name = "updated_time")
    private Date updatedTime;

    @Basic(optional = false)
    @Column(name = "sps_id")
    private String scheduledProcedureStepID;

    @Basic(optional = false)
    @Column(name = "req_proc_id")
    private String requestedProcedureID;

    @Basic(optional = false)
    @Column(name = "study_iuid")
    private String studyInstanceUID;

    @Column(name = "accession_no")
    private String accessionNumber;

    @Basic(optional = false)
    @Column(name = "modality")
    private String modality;

    @Basic(optional = false)
    @Column(name = "sps_start_date")
    private String scheduledStartDate;

    @Basic(optional = false)
    @Column(name = "sps_start_time")
    private String scheduledStartTime;

    @Basic(optional = false)
    @Column(name = "perf_phys_name")
    private String scheduledPerformingPhysicianName;
    
    @Basic(optional = false)
    @Column(name = "perf_phys_i_name")
    private String scheduledPerformingPhysicianIdeographicName;

    @Basic(optional = false)
    @Column(name = "perf_phys_p_name")
    private String scheduledPerformingPhysicianPhoneticName;

    @Basic(optional = false)
    @Column(name = "perf_phys_fn_sx")
    private String scheduledPerformingPhysicianFamilyNameSoundex;
    
    @Basic(optional = false)
    @Column(name = "perf_phys_gn_sx")
    private String scheduledPerformingPhysicianGivenNameSoundex;

    @Basic(optional = false)
    @Column(name = "sps_status")
    private String status;

    @Basic(optional = false)
    @Column(name = "item_attrs")
    private byte[] encodedAttributes;

    @Transient
    private Attributes cachedAttributes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "mwl_item_fk")
    private Collection<ScheduledStationAETitle> scheduledStationAETs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_fk")
    private Patient patient;

    public long getPk() {
        return pk;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public String getScheduledProcedureStepID() {
        return scheduledProcedureStepID;
    }

    public String getRequestedProcedureID() {
        return requestedProcedureID;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public String getModality() {
        return modality;
    }

    public String getScheduledStartDate() {
        return scheduledStartDate;
    }

    public String getScheduledStartTime() {
        return scheduledStartTime;
    }

    public String getScheduledPerformingPhysicianName() {
        return scheduledPerformingPhysicianName;
    }

    public String getScheduledPerformingPhysicianIdeographicName() {
        return scheduledPerformingPhysicianIdeographicName;
    }

    public String getScheduledPerformingPhysicianPhoneticName() {
        return scheduledPerformingPhysicianPhoneticName;
    }

    public String getScheduledPerformingPhysicianFamilyNameSoundex() {
        return scheduledPerformingPhysicianFamilyNameSoundex;
    }

    public String getScheduledPerformingPhysicianGivenNameSoundex() {
        return scheduledPerformingPhysicianGivenNameSoundex;
    }

    public String getStatus() {
        return status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "MWLItem[pk=" + pk
                + ", spsid=" + scheduledProcedureStepID
                + ", rpid=" + requestedProcedureID
                + ", suid=" + studyInstanceUID
                + ", accno=" + accessionNumber
                + ", modality=" + modality
                + ", performer=" + scheduledPerformingPhysicianName
                + ", start=" + scheduledStartDate + scheduledStartTime
                + ", status=" + status
                + "]";
    }

    @PrePersist
    public void onPrePersist() {
        Date now = new Date();
        createdTime = now;
        updatedTime = now;
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedTime = new Date();
    }

    public byte[] getEncodedAttributes() {
        return encodedAttributes;
    }

    public Attributes getAttributes() throws BlobCorruptedException {
        if (cachedAttributes == null)
            cachedAttributes = Utils.decodeAttributes(encodedAttributes);
        return cachedAttributes;
    }

    public void setAttributes(Attributes attrs, FuzzyStr fuzzyStr) {
        Attributes spsItem = attrs
                .getNestedDataset(Tag.ScheduledProcedureStepSequence);
        if (spsItem == null) {
            throw new IllegalArgumentException(
                    "Missing Scheduled Procedure Step Sequence (0040,0100) Item");
        }
        scheduledProcedureStepID = spsItem.getString(Tag.ScheduledProcedureStepID);
        modality = spsItem.getString(Tag.Modality, "*").toUpperCase();
        Date dt = spsItem.getDate(Tag.ScheduledProcedureStepStartDateAndTime);
        if (dt != null) {
            scheduledStartDate = DateUtils.formatDA(null, dt);
            scheduledStartTime = spsItem.containsValue(Tag.ScheduledProcedureStepStartTime)
                    ? DateUtils.formatTM(null, dt)
                    : "*";
        } else {
            scheduledStartDate = "*";
            scheduledStartTime = "*";
        }
        PersonName pn = new PersonName(spsItem.getString(Tag.ScheduledPerformingPhysicianName), true);
        scheduledPerformingPhysicianName = pn.contains(PersonName.Group.Alphabetic) 
                ? pn.toString(PersonName.Group.Alphabetic, false) : "*";
        scheduledPerformingPhysicianIdeographicName = pn.contains(PersonName.Group.Ideographic)
                ? pn.toString(PersonName.Group.Ideographic, false) : "*";
        scheduledPerformingPhysicianPhoneticName = pn.contains(PersonName.Group.Phonetic)
                ? pn.toString(PersonName.Group.Phonetic, false) : "*";
        scheduledPerformingPhysicianFamilyNameSoundex = 
                Utils.toFuzzy(fuzzyStr, pn.get(PersonName.Component.FamilyName));
        scheduledPerformingPhysicianGivenNameSoundex =
                Utils.toFuzzy(fuzzyStr, pn.get(PersonName.Component.GivenName));
        status = spsItem.getString(Tag.ScheduledProcedureStepStatus, SCHEDULED);
        
        requestedProcedureID = attrs.getString(Tag.RequestedProcedureID);
        studyInstanceUID = attrs.getString(Tag.StudyInstanceUID);
        accessionNumber = attrs.getString(Tag.AccessionNumber);

        encodedAttributes = Utils.encodeAttributes(cachedAttributes = attrs);
    }
}