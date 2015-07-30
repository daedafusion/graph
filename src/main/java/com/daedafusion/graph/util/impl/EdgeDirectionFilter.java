package com.daedafusion.graph.util.impl;

import com.daedafusion.graph.util.Edge;
import com.daedafusion.graph.util.EdgeFilter;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 3/26/15.
 */
public class EdgeDirectionFilter implements EdgeFilter
{
    private static final Logger log = Logger.getLogger(EdgeDirectionFilter.class);
    private final Edge.Direction direction;

    public EdgeDirectionFilter(Edge.Direction direction)
    {
        this.direction = direction;
    }

    @Override
    public boolean accept(Edge edge)
    {
        return edge.getPredicateDirection().equals(direction);
    }
}
