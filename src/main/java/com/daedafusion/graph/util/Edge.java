package com.daedafusion.graph.util;

/**
 * Created by mphilpot on 3/25/15.
 */
public interface Edge
{
    public enum Direction{S2O, direction, O2S}
    /**
     * @return hash of the subject, predicate and object hashes
     */
    long getEdgeId();

    long getSubject();

    long getPredicate();

    long getObject();

    Direction getPredicateDirection();

    double getDistance();
}
