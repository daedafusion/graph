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

import com.daedafusion.graph.routing.util.*;
import com.daedafusion.graph.storage.EdgeEntry;
import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.util.DistancePlaneProjection;
import com.daedafusion.graph.util.Edge;
import com.daedafusion.graph.util.EdgeExplorer;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * This class implements the A* algorithm according to
 * http://en.wikipedia.org/wiki/A*_search_algorithm
 * <p/>
 * Different distance calculations can be used via setApproximation.
 * <p/>
 * @author Peter Karich
 */
public class AStar extends AbstractRoutingAlgorithm
{
    private WeightApproximator weightApprox;
    private int visitedCount;
    private TLongObjectMap<AStarEdge> fromMap;
    private PriorityQueue<AStarEdge> prioQueueOpenSet;
    private AStarEdge currEdge;
    private long to1 = -1;

    public AStar(Graph g, Weighting weighting)
    {
        super(g, weighting);
        initCollections(1000);
        BeelineWeightApproximator defaultApprox = new BeelineWeightApproximator(nodeAccess, weighting);
        defaultApprox.setDistanceCalc(new DistancePlaneProjection());
        setApproximation(defaultApprox);
    }

    /**
     * @param approx defines how distance to goal Node is approximated
     */
    public AStar setApproximation( WeightApproximator approx )
    {
        weightApprox = approx;
        return this;
    }

    protected void initCollections( int size )
    {
        fromMap = new TLongObjectHashMap<AStarEdge>();
        prioQueueOpenSet = new PriorityQueue<AStarEdge>(size);
    }

    @Override
    public Path calcPath( long from, long to )
    {
        checkAlreadyRun();
        to1 = to;
        weightApprox.setGoalNode(to);
        currEdge = createEdgeEntry(from, 0);
        fromMap.put(from, currEdge);
        return runAlgo();
    }

    private Path runAlgo()
    {
        double currWeightToGoal, distEstimation;
        EdgeExplorer explorer = outEdgeExplorer;
        while (true)
        {
            long currVertex = currEdge.adjNode;
            visitedCount++;
            if (isWeightLimitReached())
                return createEmptyPath();

            if (finished())
                break;

            Iterator<Edge> iter = explorer.setBaseNode(currVertex);
            while (iter.hasNext())
            {
                Edge e  = iter.next();

                if (!accept(e, currEdge.edge))
                    continue;

                long traversalId;
                if(isFollowIncoming())
                {
                    traversalId = (currEdge.adjNode == e.getSubject()) ? e.getObject() : e.getSubject();
                }
                else
                {
                    traversalId = e.getObject();
                }

                long neighborNode = traversalId;
                // cast to float to avoid rounding errors in comparison to float entry of AStarEdge weight
                float alreadyVisitedWeight = (float) (weighting.calcWeight(e)
                        + currEdge.weightOfVisitedPath);
                if (Double.isInfinite(alreadyVisitedWeight))
                    continue;

                AStarEdge ase = fromMap.get(traversalId);
                if ((ase == null) || ase.weightOfVisitedPath > alreadyVisitedWeight)
                {
                    currWeightToGoal = weightApprox.approximate(neighborNode);
                    distEstimation = alreadyVisitedWeight + currWeightToGoal;
                    if (ase == null)
                    {
                        ase = new AStarEdge(e.getEdgeId(), neighborNode, distEstimation, alreadyVisitedWeight);
                        fromMap.put(traversalId, ase);
                    } else
                    {
                        assert (ase.weight > distEstimation) : "Inconsistent distance estimate";
                        prioQueueOpenSet.remove(ase);
                        ase.edge = e.getEdgeId();
                        ase.weight = distEstimation;
                        ase.weightOfVisitedPath = alreadyVisitedWeight;
                    }

                    ase.parent = currEdge;
                    prioQueueOpenSet.add(ase);

                    updateBestPath(e, ase, traversalId);
                }
            }

            if (prioQueueOpenSet.isEmpty())
                return createEmptyPath();

            currEdge = prioQueueOpenSet.poll();
            if (currEdge == null)
                throw new AssertionError("Empty edge cannot happen");
        }

        return extractPath();
    }

    @Override
    protected Path extractPath()
    {
        return new Path(graph).setWeight(currEdge.weight).setEdgeEntry(currEdge).extract();
    }

    @Override
    protected AStarEdge createEdgeEntry( long node, double dist )
    {
        return new AStarEdge(Long.MIN_VALUE, node, dist, dist);
    }

    @Override
    protected boolean finished()
    {
        return currEdge.adjNode == to1;
    }

    @Override
    public int getVisitedNodes()
    {
        return visitedCount;
    }

    protected boolean isWeightLimitReached()
    {
        return currEdge.weight >= weightLimit;
    }

    public static class AStarEdge extends EdgeEntry
    {
        // the variable 'weight' is used to let heap select smallest *full* distance.
        // but to compare distance we need it only from start:
        double weightOfVisitedPath;

        public AStarEdge( long edgeId, long adjNode, double weightForHeap, double weightOfVisitedPath )
        {
            super(edgeId, adjNode, weightForHeap);
            this.weightOfVisitedPath = (float) weightOfVisitedPath;
        }
    }

    @Override
    public String getName()
    {
        return "astar";
    }
}
