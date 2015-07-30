package com.daedafusion.graph.routing.util;

import com.daedafusion.graph.util.Edge;
import com.daedafusion.graph.util.EdgeFilter;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 3/25/15.
 */
public class DefaultEdgeFilter implements EdgeFilter
{
    private static final Logger log = Logger.getLogger(DefaultEdgeFilter.class);

    @Override
    public boolean accept(Edge edge)
    {
        return true;
    }
}
