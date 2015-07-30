/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.daedafusion.graph.util;

import com.daedafusion.graph.util.shapes.BBox;
import com.daedafusion.graph.util.shapes.GHPoint;

import static java.lang.Math.*;

/**
 * @author Peter Karich
 */
public class DistanceCalcEarth implements DistanceCalc
{
    /**
     * mean radius of the earth
     */
    public final static double R = 6371000; // m
    /**
     * Radius of the earth at equator
     */
    public final static double R_EQ = 6378137; // m
    /**
     * Circumference of the earth
     */
    public final static double C = 2 * PI * R;
    public final static double KM_MILE = 1.609344;

    /**
     * Calculates distance of (from, to) in meter.
     * <p/>
     * http://en.wikipedia.org/wiki/Haversine_formula a = sin²(Δlat/2) +
     * cos(lat1).cos(lat2).sin²(Δlong/2) c = 2.atan2(√a, √(1−a)) d = R.c
     */
    @Override
    public double calcDist( double fromLat, double fromLon, double toLat, double toLon )
    {
        double sinDeltaLat = sin(toRadians(toLat - fromLat) / 2);
        double sinDeltaLon = sin(toRadians(toLon - fromLon) / 2);
        double normedDist = sinDeltaLat * sinDeltaLat
                + sinDeltaLon * sinDeltaLon * cos(toRadians(fromLat)) * cos(toRadians(toLat));
        return R * 2 * asin(sqrt(normedDist));
    }

    public double calcDenormalizedDist( double normedDist )
    {
        return R * 2 * asin(sqrt(normedDist));
    }

    /**
     * Returns the specified length in normalized meter.
     */
    @Override
    public double calcNormalizedDist( double dist )
    {
        double tmp = sin(dist / 2 / R);
        return tmp * tmp;
    }

    @Override
    public double calcNormalizedDist( double fromLat, double fromLon, double toLat, double toLon )
    {
        double sinDeltaLat = sin(toRadians(toLat - fromLat) / 2);
        double sinDeltaLon = sin(toRadians(toLon - fromLon) / 2);
        return sinDeltaLat * sinDeltaLat
                + sinDeltaLon * sinDeltaLon * cos(toRadians(fromLat)) * cos(toRadians(toLat));
    }

    /**
     * Circumference of the earth at different latitudes (breitengrad)
     */
    public double calcCircumference( double lat )
    {
        return 2 * PI * R * cos(toRadians(lat));
    }

    public double calcSpatialKeyMaxDist( int bit )
    {
        bit = bit / 2 + 1;
        return (int) C >> bit;
    }

    public boolean isDateLineCrossOver( double lon1, double lon2 )
    {
        return abs(lon1 - lon2) > 180.0;
    }

    @Override
    public BBox createBBox( double lat, double lon, double radiusInMeter )
    {
        if (radiusInMeter <= 0)
            throw new IllegalArgumentException("Distance must not be zero or negative! " + radiusInMeter + " lat,lon:" + lat + "," + lon);

        // length of a circle at specified lat / dist
        double dLon = (360 / (calcCircumference(lat) / radiusInMeter));

        // length of a circle is independent of the longitude
        double dLat = (360 / (DistanceCalcEarth.C / radiusInMeter));

        // Now return bounding box in coordinates
        return new BBox(lon - dLon, lon + dLon, lat - dLat, lat + dLat);
    }

    @Override
    public double calcNormalizedEdgeDistance( double rLatDeg, double rLonDeg,
            double aLatDeg, double aLonDeg,
            double bLatDeg, double bLonDeg )
    {
        return calcNormalizedEdgeDistanceNew(rLatDeg, rLonDeg, aLatDeg, aLonDeg, bLatDeg, bLonDeg, false);
    }

    /**
     * New edge distance calculation where no validEdgeDistance check would be necessary
     * <p>
     * @return the normalized distance of the query point "r" to the project point "c" onto the line
     * segment a-b
     */
    public double calcNormalizedEdgeDistanceNew( double rLatDeg, double rLonDeg,
            double aLatDeg, double aLonDeg,
            double bLatDeg, double bLonDeg, boolean reduceToSegment )
    {
        double shrinkFactor = cos(toRadians((aLatDeg + bLatDeg) / 2));

        double aLon = aLonDeg * shrinkFactor;

        double bLon = bLonDeg * shrinkFactor;

        double rLon = rLonDeg * shrinkFactor;

        double deltaLon = bLon - aLon;
        double deltaLat = bLatDeg - aLatDeg;

        if (deltaLat == 0)
            // special case: horizontal edge
            return calcNormalizedDist(aLatDeg, rLonDeg, rLatDeg, rLonDeg);

        if (deltaLon == 0)
            // special case: vertical edge        
            return calcNormalizedDist(rLatDeg, aLonDeg, rLatDeg, rLonDeg);

        double norm = deltaLon * deltaLon + deltaLat * deltaLat;
        double factor = ((rLon - aLon) * deltaLon + (rLatDeg - aLatDeg) * deltaLat) / norm;

        // make new calculation compatible to old
        if (reduceToSegment)
        {
            if (factor > 1)
                factor = 1;
            else if (factor < 0)
                factor = 0;
        }
        // x,y is projection of r onto segment a-b
        double c_lon = aLon + factor * deltaLon;
        double c_lat = aLatDeg + factor * deltaLat;
        return calcNormalizedDist(c_lat, c_lon / shrinkFactor, rLatDeg, rLonDeg);
    }

    @Override
    public GHPoint calcCrossingPointToEdge( double rLatDeg, double rLonDeg,
            double aLatDeg, double aLonDeg,
            double bLatDeg, double bLonDeg )
    {
        double shrinkFactor = cos(toRadians((aLatDeg + bLatDeg) / 2));
        double aLon = aLonDeg * shrinkFactor;

        double bLon = bLonDeg * shrinkFactor;

        double rLon = rLonDeg * shrinkFactor;

        double deltaLon = bLon - aLon;
        double deltaLat = bLatDeg - aLatDeg;

        if (deltaLat == 0)
            // special case: horizontal edge
            return new GHPoint(aLatDeg, rLonDeg);

        if (deltaLon == 0)
            // special case: vertical edge        
            return new GHPoint(rLatDeg, aLonDeg);

        double norm = deltaLon * deltaLon + deltaLat * deltaLat;
        double factor = ((rLon - aLon) * deltaLon + (rLatDeg - aLatDeg) * deltaLat) / norm;

//        if (false)
//        {
//            if (factor > 1)
//                factor = 1;
//            else if (factor < 0)
//                factor = 0;
//        }

        // x,y is projection of r onto segment a-b
        double c_lon = aLon + factor * deltaLon;
        double c_lat = aLatDeg + factor * deltaLat;
        return new GHPoint(c_lat, c_lon / shrinkFactor);
    }

    @Override
    public boolean validEdgeDistance( double rLatDeg, double rLonDeg,
            double aLatDeg, double aLonDeg,
            double bLatDeg, double bLonDeg )
    {
        double shrinkFactor = cos(toRadians((aLatDeg + bLatDeg) / 2));
        double aLon = aLonDeg * shrinkFactor;

        double bLon = bLonDeg * shrinkFactor;

        double rLon = rLonDeg * shrinkFactor;

        double arX = rLon - aLon;
        double arY = rLatDeg - aLatDeg;
        double abX = bLon - aLon;
        double abY = bLatDeg - aLatDeg;
        double abAr = arX * abX + arY * abY;

        double rbX = bLon - rLon;
        double rbY = bLatDeg - rLatDeg;
        double abRb = rbX * abX + rbY * abY;

        // calculate the exact degree alpha(ar, ab) and beta(rb,ab) if it is case 1 then both angles are <= 90°
        // double ab_ar_norm = Math.sqrt(ar_x * ar_x + ar_y * ar_y) * Math.sqrt(ab_x * ab_x + ab_y * ab_y);
        // double ab_rb_norm = Math.sqrt(rb_x * rb_x + rb_y * rb_y) * Math.sqrt(ab_x * ab_x + ab_y * ab_y);
        // return Math.acos(ab_ar / ab_ar_norm) <= Math.PI / 2 && Math.acos(ab_rb / ab_rb_norm) <= Math.PI / 2;
        return abAr > 0 && abRb > 0;
    }

    @Override
    public String toString()
    {
        return "EXACT";
    }
}
