package com.daedafusion.graph.impl;

import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.util.Edge;
import com.daedafusion.graph.util.EdgeExplorer;
import com.daedafusion.graph.util.EdgeFilter;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * Created by mphilpot on 3/25/15.
 */
public class DefaultEdgeIterable implements EdgeExplorer, Iterator<Edge>
{
    private static final Logger log = Logger.getLogger(DefaultEdgeIterable.class);
    private final EdgeFilter filter;
    private final Graph graph;
    private long baseNode;

    private Iterator<Edge> currentEdgesIter;

    public DefaultEdgeIterable(Graph graph, EdgeFilter filter)
    {
        this.graph = graph;
        this.filter = filter;
    }

    @Override
    public Iterator<Edge> setBaseNode(long baseNode)
    {
        this.baseNode = baseNode;
        currentEdgesIter = graph.getNodeAccess().getEdges(baseNode).iterator();
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return currentEdgesIter.hasNext();
    }

    @Override
    public Edge next()
    {
        return currentEdgesIter.next();
    }

    @Override
    public void remove()
    {
        throw new RuntimeException("Remove not supported");
    }
}
