package com.daedafusion.graph.routing.util;

import com.daedafusion.graph.storage.NodeAccess;

/**
 * Turns an unidirectional weight Approximation into a bidirectional consistent one.
 *
 * Ikeda, T., Hsu, M.-Y., Imai, H., Nishimura, S., Shimoura, H., Hashimoto, T., Tenmoku, K., and
 * Mitoh, K. (1994). A fast algorithm for finding better routes by ai search techniques. In VNIS,
 * pages 291–296.
 *
 *
 * @author jansoe
 */
public class ConsistentWeightApproximator
{

    private NodeAccess nodeAccess;
    private Weighting weighting;
    private WeightApproximator uniDirApproximatorForward, uniDirApproximatorReverse;

    public ConsistentWeightApproximator(WeightApproximator weightApprox){
        uniDirApproximatorForward = weightApprox;
        uniDirApproximatorReverse = weightApprox.duplicate();
    }

    public void setSourceNode(long sourceNode){
        uniDirApproximatorReverse.setGoalNode(sourceNode);
    }

    public void setGoalNode(long goalNode){
        uniDirApproximatorForward.setGoalNode(goalNode);
    }

    public double approximate(long fromNode, boolean reverse)    {
        double weightApproximation = 0.5*(uniDirApproximatorForward.approximate(fromNode)
                                          - uniDirApproximatorReverse.approximate(fromNode));
        if (reverse) {
            weightApproximation *= -1;
        }

        return weightApproximation;
    }
}
