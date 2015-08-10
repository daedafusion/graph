package com.daedafusion.graph.routing.util;

/**
 * Specifies a weight approximation between an node and the goalNode according to the specified weighting.
 *
 * @author jansoe
 */
public interface WeightApproximator
{

    /**
     * @return minimal weight fromNode to the goalNode
     */
    double approximate(long fromNode);

    void setGoalNode(long to);

    /**
     * makes a deep copy of itself
     */
    WeightApproximator duplicate();
}
