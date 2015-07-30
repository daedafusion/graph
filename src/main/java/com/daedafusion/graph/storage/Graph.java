package com.daedafusion.graph.storage;

import com.daedafusion.graph.util.Edge;
import com.daedafusion.graph.util.EdgeExplorer;
import com.daedafusion.graph.util.EdgeFilter;

/**
 * Created by mphilpot on 3/25/15.
 */
public interface Graph
{
    Edge edge(long subject, long predicate, long object);

    Edge edge(long subject, long predicate, long object, double distance);

    Edge getEdge(long edgeId);

    EdgeExplorer createEdgeExplorer(EdgeFilter filter);

    NodeAccess getNodeAccess();
}
