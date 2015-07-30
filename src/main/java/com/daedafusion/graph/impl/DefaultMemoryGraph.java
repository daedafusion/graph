package com.daedafusion.graph.impl;

import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.storage.NodeAccess;
import com.daedafusion.graph.util.Edge;
import com.daedafusion.graph.util.EdgeExplorer;
import com.daedafusion.graph.util.EdgeFilter;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by mphilpot on 3/25/15.
 */
public class DefaultMemoryGraph implements Graph
{
    private static final Logger log = Logger.getLogger(DefaultMemoryGraph.class);

    private final Map<Long, Edge> edges;

    private final Map<Long, Set<Edge>> nodesS2O;
    private final Map<Long, Set<Edge>> nodesO2S;

    public DefaultMemoryGraph()
    {
        edges = new LinkedHashMap<>();
        nodesS2O = new HashMap<>();
        nodesO2S = new HashMap<>();
    }

    @Override
    public Edge edge(long subject, long predicate, long object)
    {
        return edge(subject, predicate, object, 1.0);
    }

    @Override
    public Edge edge(long subject, long predicate, long object, double distance)
    {
        Edge e = new DefaultEdge(subject, predicate, object, Edge.Direction.S2O, distance);
        edges.put(e.getEdgeId(), e);
        if(!nodesS2O.containsKey(subject))
            nodesS2O.put(subject, new HashSet<Edge>());
        if(!nodesO2S.containsKey(object))
            nodesO2S.put(object, new HashSet<Edge>());
        nodesS2O.get(subject).add(e);
        nodesO2S.get(object).add(e);
        return e;
    }

    @Override
    public Edge getEdge(long edgeId)
    {
        return edges.get(edgeId);
    }

    @Override
    public EdgeExplorer createEdgeExplorer(EdgeFilter filter)
    {
        return new DefaultEdgeIterable(this, filter);
    }

    @Override
    public NodeAccess getNodeAccess()
    {
        return new NodeAccess()
        {
            @Override
            public Set<Edge> getEdges(long baseNode)
            {
                Set<Edge> result = new HashSet<>();
                if(nodesO2S.containsKey(baseNode))
                    result.addAll(nodesO2S.get(baseNode));
                if(nodesS2O.containsKey(baseNode))
                    result.addAll(nodesS2O.get(baseNode));
                return result;
            }

            @Override
            public double getLatitude(long toNode)
            {
                return 0;
            }

            @Override
            public double getLongitude(long toNode)
            {
                return 0;
            }
        };
    }
}
