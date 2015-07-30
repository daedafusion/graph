package com.daedafusion.graph.impl;

import com.daedafusion.graph.util.Edge;
import com.google.common.hash.Hashing;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

/**
 * Created by mphilpot on 3/25/15.
 */
public class DefaultEdge implements Edge
{
    private static final Logger log = Logger.getLogger(DefaultEdge.class);
    private final double distance;

    private long edgeId;
    private long subject;
    private long predicate;
    private long object;
    private Direction direction;

    public DefaultEdge(long subject, long predicate, long object)
    {
        this(subject, predicate, object, Direction.S2O, 0.0);
    }

    public DefaultEdge(long subject, long predicate, long object, Direction direction, double distance)
    {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.direction = direction;
        this.distance = distance;

        edgeId = Hashing.murmur3_128().hashString(String.format("%d%d%d", subject, predicate, object), Charset.forName("UTF-8")).asLong();
    }

    @Override
    public long getEdgeId()
    {
        return edgeId;
    }

    @Override
    public long getSubject()
    {
        return subject;
    }

    @Override
    public long getPredicate()
    {
        return predicate;
    }

    @Override
    public long getObject()
    {
        return object;
    }

    @Override
    public Direction getPredicateDirection()
    {
        return direction;
    }

    @Override
    public double getDistance()
    {
        return distance;
    }
}
