package com.daedafusion.graph.routing.util;

import com.daedafusion.graph.storage.NodeAccess;
import com.daedafusion.graph.util.DistanceCalc;
import com.daedafusion.graph.util.DistanceCalcEarth;

/**
 * Approximates the distance to the goalNode by weighting the beeline distance according to the distance weighting
 * @author jansoe
 */
public class BeelineWeightApproximator implements WeightApproximator {

    private NodeAccess nodeAccess;
    private Weighting weighting;
    private DistanceCalc distanceCalc;
    double toLat, toLon;

    public BeelineWeightApproximator(NodeAccess nodeAccess, Weighting weighting) {
        this.nodeAccess = nodeAccess;
        this.weighting = weighting;
        setDistanceCalc(new DistanceCalcEarth());
    }

    public void setGoalNode(long toNode){
        toLat = nodeAccess.getLatitude(toNode);
        toLon = nodeAccess.getLongitude(toNode);
    }

    @Override
    public WeightApproximator duplicate() {
        return new BeelineWeightApproximator(nodeAccess, weighting).setDistanceCalc(distanceCalc);
    }


    @Override
    public double approximate(long fromNode) {

        double fromLat, fromLon, dist2goal, weight2goal;
        fromLat  = nodeAccess.getLatitude(fromNode);
        fromLon = nodeAccess.getLongitude(fromNode);
        dist2goal = distanceCalc.calcDist(toLat, toLon, fromLat, fromLon);
        weight2goal = weighting.getMinWeight(dist2goal);

        return weight2goal;
    }

    public BeelineWeightApproximator setDistanceCalc(DistanceCalc distanceCalc) {
        this.distanceCalc = distanceCalc;
        return this;
    }
}
