/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.c4fcm.actionpath;

import com.google.android.gms.location.Geofence;

/**
 * A single Geofence object, defined by its center (latitude and longitude position) and radius.
 */
public class SimpleGeofence {
    // Instance variables
    private String mId;
    private double mLatitude;
    private double mLongitude;
    private float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;

    /**
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center. The value is not checked for validity.
     * @param longitude Longitude of the Geofence's center. The value is not checked for validity.
     * @param radius Radius of the geofence circle. The value is not checked for validity
     * @param expiration Geofence expiration duration in milliseconds The value is not checked for
     * validity.
     * @param transition Type of Geofence transition. The value is not checked for validity.
     */
    public SimpleGeofence(
           String geofenceId,
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition) {
    	initializeFields(latitude,longitude,radius,expiration,transition);
    	this.mId=geofenceId;
    }
    
    /**
     * @param latitude Latitude of the Geofence's center. The value is not checked for validity.
     * @param longitude Longitude of the Geofence's center. The value is not checked for validity.
     * @param radius Radius of the geofence circle. The value is not checked for validity
     * @param expiration Geofence expiration duration in milliseconds The value is not checked for
     * validity.
     * @param transition Type of Geofence transition. The value is not checked for validity.
     */
    public SimpleGeofence(
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition) {
        initializeFields(latitude,longitude,radius,expiration,transition);
    }

    private void initializeFields(
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition){
        // Center of the geofence
        this.mLatitude = latitude;
        this.mLongitude = longitude;

        // Radius of the geofence, in meters
        this.mRadius = radius;

        // Expiration time in milliseconds
        this.mExpirationDuration = expiration;

        // Transition type
        this.mTransitionType = transition;
    }
    
    /**
     * Get the geofence ID
     * @return A SimpleGeofence ID
     */
    public String getId() {
        return mId;
    }
    
	/**
	 * Set the geofence ID
	 * @param geofence id
	 */
    public void setId(String id) {
        mId = id;
    }

    /**
     * Get the geofence latitude
     * @return A latitude value
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Get the geofence longitude
     * @return A longitude value
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Get the geofence radius
     * @return A radius value
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * Get the geofence expiration duration
     * @return Expiration duration in milliseconds
     */
    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    /**
     * Get the geofence transition type
     * @return Transition type (see Geofence)
     */
    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a
     * SimpleGeofence.
     *
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                       .setRequestId(getId())
                       .setTransitionTypes(mTransitionType)
                       .setCircularRegion(
                               getLatitude(),
                               getLongitude(),
                               getRadius())
                       .setExpirationDuration(mExpirationDuration)
                       .build();
    }
}
