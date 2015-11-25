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
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
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

package org.dcm4chee.archive.query.impl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import org.dcm4che3.data.Tag;
import org.dcm4che3.net.Device;
import org.dcm4che3.util.StringUtils;
import org.dcm4chee.archive.conf.ArchiveDeviceExtension;
import org.dcm4chee.archive.conf.QueryParam;
import org.dcm4chee.archive.entity.*;
import org.dcm4chee.archive.query.DerivedSeriesFields;
import org.dcm4chee.storage.conf.Availability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import java.util.Date;

/**
 * Calculates the derived fields for a Series.
 *
 * Created by Umberto Cappellini on 6/12/15.
 */
public class DefaultDerivedSeriesFields implements DerivedSeriesFields {

    private final Device device;

    private int numberOfInstances;
    private String[] retrieveAETs;
    private Availability availability;
    private Date lastUpdateTime = null;
    private int numberOfVisibleImages;

    public DefaultDerivedSeriesFields(Device device) {
        this.device = device;
    }

    @Override
    public Expression<?>[] fields() {
        return new Expression<?>[]{
                QInstance.instance.retrieveAETs,
                QInstance.instance.availability,
                QInstance.instance.updatedTime,
                QInstance.instance.attributesBlob,
                QInstance.instance.sopClassUID
        };
    }

    @Override
    public int getNumberOfInstances() {
        return numberOfInstances;
    }

    @Override
    public String[] getRetrieveAETs() {
        return retrieveAETs;
    }

    @Override
    public Availability getAvailability() {
        return availability;
    }

    @Override
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public int getNumberOfVisibleImages() {
        return numberOfVisibleImages;
    }

    @Override
    public void addInstance(Tuple result, QueryParam param) {
        String[] retrieveAETs1 = StringUtils.split(
                result.get(QInstance.instance.retrieveAETs),
                '\\');
        Availability availability1 =
                result.get(QInstance.instance.availability);
        if (numberOfInstances++ == 0) {
            retrieveAETs = retrieveAETs1;
            availability = availability1;
        } else {
            retrieveAETs = Utils.intersection(
                    retrieveAETs, retrieveAETs1);
            if (availability.compareTo(availability1) < 0)
                availability = availability1;
        }
        Date instanceUpdateTime = result.get(QInstance.instance
                .updatedTime);
        if (lastUpdateTime == null || instanceUpdateTime.after(lastUpdateTime))
            lastUpdateTime = instanceUpdateTime;

        AttributesBlob blob = result.get(QInstance.instance.attributesBlob);
        String sopClass = result.get(QInstance.instance.sopClassUID);

        if (device.getDeviceExtension(ArchiveDeviceExtension.class).isVisibleSOPClass(sopClass)) {
            int numberOfVisibleImagesInInstance = blob != null ? blob.getAttributes().getInt(Tag.NumberOfFrames, 1) : 1;
            numberOfVisibleImages += numberOfVisibleImagesInInstance;
        }
    }

}
