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
import com.daedafusion.graph.storage.Graph;

/**
 * Common subclass for bidirectional algorithms.
 * @author Peter Karich
 */
public abstract class AbstractBidirAlgo extends AbstractRoutingAlgorithm
{
    int visitedCountFrom;
    int visitedCountTo;
    protected boolean finishedFrom;
    protected boolean finishedTo;

    abstract void initFrom( long from, double dist );

    abstract void initTo( long to, double dist );

    protected abstract Path createAndInitPath();

    protected abstract boolean isWeightLimitReached();

    abstract void checkState( long fromBase, long fromAdj, long toBase, long toAdj );

    abstract boolean fillEdgesFrom();

    abstract boolean fillEdgesTo();

    public AbstractBidirAlgo(Graph graph, Weighting weighting)
    {
        super(graph, weighting);
    }

    @Override
    public Path calcPath( long from, long to )
    {
        checkAlreadyRun();
        createAndInitPath();
        initFrom(from, 0);
        initTo(to, 0);
        runAlgo();
        return extractPath();
    }

    protected void runAlgo()
    {
        while (!finished() && !isWeightLimitReached())
        {
            if (!finishedFrom)
                finishedFrom = !fillEdgesFrom();

            if (!finishedTo)
                finishedTo = !fillEdgesTo();
        }
    }

    @Override
    public int getVisitedNodes()
    {
        return visitedCountFrom + visitedCountTo;
    }
}
