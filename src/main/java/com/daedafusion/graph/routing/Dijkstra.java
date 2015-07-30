/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.daedafusion.graph.routing;

import com.daedafusion.graph.routing.util.Weighting;
import com.daedafusion.graph.storage.EdgeEntry;
import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.util.Edge;
import com.daedafusion.graph.util.EdgeExplorer;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Implements a single source shortest path algorithm
 * http://en.wikipedia.org/wiki/Dijkstra's_algorithm
 * <p/>
 * @author Peter Karich
 */
public class Dijkstra extends AbstractRoutingAlgorithm
{
    protected TLongObjectMap<EdgeEntry> fromMap;
    protected PriorityQueue<EdgeEntry> fromHeap;
    protected EdgeEntry currEdge;
    private int visitedNodes;
    private long to = -1;

    public Dijkstra(Graph g, Weighting weighting)
    {
        super(g, weighting);
        initCollections(1000);
    }

    public void setFollowIncoming(boolean followIncoming)
    {
        this.followIncoming = followIncoming;
    }

    protected void initCollections( int size )
    {
        fromHeap = new PriorityQueue<EdgeEntry>(size);
        fromMap = new TLongObjectHashMap<EdgeEntry>(size);
    }

    @Override
    public Path calcPath( long from, long to )
    {
        checkAlreadyRun();
        this.to = to;
        currEdge = createEdgeEntry(from, 0);

        fromMap.put(from, currEdge);

        runAlgo();
        return extractPath();
    }

    protected void runAlgo()
    {
        EdgeExplorer explorer = outEdgeExplorer;
        while (true)
        {
            visitedNodes++;
            if (isWeightLimitReached() || finished())
                break;

            long startNode = currEdge.adjNode;
            Iterator<Edge> iter = explorer.setBaseNode(startNode);
            while (iter.hasNext())
            {
                Edge e = iter.next();

                if (!accept(e, currEdge.edge))
                    continue;

                long traversalId;
                if(isFollowIncoming())
                {
                    traversalId = (startNode == e.getSubject()) ? e.getObject() : e.getSubject();
                }
                else
                {
                    traversalId = e.getObject();
                }

                double tmpWeight = weighting.calcWeight(e) + currEdge.weight;
                if (Double.isInfinite(tmpWeight))
                    continue;

                EdgeEntry nEdge = fromMap.get(traversalId);
                if (nEdge == null)
                {
                    nEdge = new EdgeEntry(e.getEdgeId(), traversalId, tmpWeight);
                    nEdge.parent = currEdge;
                    fromMap.put(traversalId, nEdge);
                    fromHeap.add(nEdge);
                } else if (nEdge.weight > tmpWeight)
                {
                    fromHeap.remove(nEdge);
                    nEdge.edge = e.getEdgeId();
                    nEdge.weight = tmpWeight;
                    nEdge.parent = currEdge;
                    fromHeap.add(nEdge);
                } else
                    continue;

                updateBestPath(e, nEdge, traversalId);
            }

            if (fromHeap.isEmpty())
                break;

            currEdge = fromHeap.poll();
            if (currEdge == null)
                throw new AssertionError("Empty edge cannot happen");
        }
    }

    @Override
    protected boolean finished()
    {
        return currEdge.adjNode == to;
    }

    @Override
    protected Path extractPath()
    {
        if (currEdge == null || isWeightLimitReached() || !finished())
            return createEmptyPath();

        return new Path(graph).setWeight(currEdge.weight).setEdgeEntry(currEdge).extract();
    }

    @Override
    public int getVisitedNodes()
    {
        return visitedNodes;
    }

    protected boolean isWeightLimitReached()
    {
        return currEdge.weight >= weightLimit;
    }

    @Override
    public String getName()
    {
//        return AlgorithmOptions.DIJKSTRA;
        return "dijkstra";
    }
}
