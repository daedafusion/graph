package com.daedafusion.graph.routing;

import com.daedafusion.graph.impl.DefaultMemoryGraph;
import com.daedafusion.graph.routing.util.Weighting;
import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.util.Edge;
import org.apache.log4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mphilpot on 3/25/15.
 */
public class DijkstraTest extends AbstractRoutingTester
{
    private static final Logger log = Logger.getLogger(DijkstraTest.class);

    public RoutingAlgorithm createAlgo(Graph graph)
    {
        return new Dijkstra(graph, new Weighting()
        {
            @Override
            public double getMinWeight(double distance)
            {
                return 0;
            }

            @Override
            public double calcWeight(Edge edge)
            {
                return edge.getDistance();
            }
        });
    }
}
