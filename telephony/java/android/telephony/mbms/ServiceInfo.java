/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.telephony.mbms;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Describes a cell-broadcast service. This class should not be instantiated directly -- use
 * {@link StreamingServiceInfo} or FileServiceInfo TODO: add link once that's unhidden
 */
public class ServiceInfo implements Parcelable {
    // arbitrary limit on the number of locale -> name pairs we support
    final static int MAP_LIMIT = 1000;

    private final Map<Locale, String> names;
    private final String className;
    private final List<Locale> locales;
    private final String serviceId;
    private final Date sessionStartTime;
    private final Date sessionEndTime;

    /** @hide */
    public ServiceInfo(Map<Locale, String> newNames, String newClassName, List<Locale> newLocales,
            String newServiceId, Date start, Date end) {
        if (newNames == null || newNames.isEmpty() || TextUtils.isEmpty(newClassName)
                || newLocales == null || newLocales.isEmpty() || TextUtils.isEmpty(newServiceId)
                || start == null || end == null) {
            throw new IllegalArgumentException("Bad ServiceInfo construction");
        }
        if (newNames.size() > MAP_LIMIT) {
            throw new RuntimeException("bad map length " + newNames.size());
        }
        if (newLocales.size() > MAP_LIMIT) {
            throw new RuntimeException("bad locales length " + newLocales.size());
        }
        names = new HashMap(newNames.size());
        names.putAll(newNames);
        className = newClassName;
        locales = new ArrayList(newLocales);
        serviceId = newServiceId;
        sessionStartTime = (Date)start.clone();
        sessionEndTime = (Date)end.clone();
    }

    public static final Parcelable.Creator<ServiceInfo> CREATOR =
            new Parcelable.Creator<ServiceInfo>() {
        @Override
        public ServiceInfo createFromParcel(Parcel source) {
            return new ServiceInfo(source);
        }

        @Override
        public ServiceInfo[] newArray(int size) {
            return new ServiceInfo[size];
        }
    };

    /** @hide */
    protected ServiceInfo(Parcel in) {
        int mapCount = in.readInt();
        if (mapCount > MAP_LIMIT || mapCount < 0) {
            throw new RuntimeException("bad map length" + mapCount);
        }
        names = new HashMap(mapCount);
        while (mapCount-- > 0) {
            Locale locale = (java.util.Locale) in.readSerializable();
            String name = in.readString();
            names.put(locale, name);
        }
        className = in.readString();
        int localesCount = in.readInt();
        if (localesCount > MAP_LIMIT || localesCount < 0) {
            throw new RuntimeException("bad locale length " + localesCount);
        }
        locales = new ArrayList<Locale>(localesCount);
        while (localesCount-- > 0) {
            Locale l = (java.util.Locale) in.readSerializable();
            locales.add(l);
        }
        serviceId = in.readString();
        sessionStartTime = (java.util.Date) in.readSerializable();
        sessionEndTime = (java.util.Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Set<Locale> keySet = names.keySet();
        dest.writeInt(keySet.size());
        for (Locale l : keySet) {
            dest.writeSerializable(l);
            dest.writeString(names.get(l));
        }
        dest.writeString(className);
        int localesCount = locales.size();
        dest.writeInt(localesCount);
        for (Locale l : locales) {
            dest.writeSerializable(l);
        }
        dest.writeString(serviceId);
        dest.writeSerializable(sessionStartTime);
        dest.writeSerializable(sessionEndTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * User displayable names listed by language. Do not modify the map returned from this method.
     */
    public Map<Locale, String> getNames() {
        return names;
    }

    /**
     * The class name for this service - used to categorize and filter
     */
    public String getClassName() {
        return className;
    }

    /**
     * The languages available for this service content
     */
    public List<Locale> getLocales() {
        return locales;
    }

    /**
     * The carrier's identifier for the service.
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * The start time indicating when this service will be available.
     */
    public Date getSessionStartTime() {
        return sessionStartTime;
    }

    /**
     * The end time indicating when this session stops being available.
     */
    public Date getSessionEndTime() {
        return sessionEndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) {
            return false;
        }
        if (!(o instanceof ServiceInfo)) {
            return false;
        }
        ServiceInfo that = (ServiceInfo) o;
        return Objects.equals(names, that.names) &&
                Objects.equals(className, that.className) &&
                Objects.equals(locales, that.locales) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(sessionStartTime, that.sessionStartTime) &&
                Objects.equals(sessionEndTime, that.sessionEndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(names, className, locales, serviceId, sessionStartTime, sessionEndTime);
    }
}
