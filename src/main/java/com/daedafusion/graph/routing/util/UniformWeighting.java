package com.daedafusion.graph.routing.util;

import com.daedafusion.graph.util.Edge;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 3/26/15.
 */
public class UniformWeighting implements Weighting
{
    private static final Logger log = Logger.getLogger(UniformWeighting.class);

    @Override
    public double getMinWeight(double distance)
    {
        return 0;
    }

    @Override
    public double calcWeight(Edge edge)
    {
        return 1.0;
    }
}
