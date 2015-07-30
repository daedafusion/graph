package com.daedafusion.graph.storage;

import com.daedafusion.graph.util.Edge;

import java.util.Set;

/**
 * Created by mphilpot on 3/25/15.
 */
public interface NodeAccess
{
    Set<Edge> getEdges(long baseNode);

    double getLatitude(long toNode);

    double getLongitude(long toNode);
}
