package com.daedafusion.graph.routing;

import com.daedafusion.graph.routing.util.Weighting;
import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.util.Edge;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 3/25/15.
 */
public class AStarTest extends AbstractRoutingTester
{
    private static final Logger log = Logger.getLogger(AStarTest.class);

    public RoutingAlgorithm createAlgo(Graph graph)
    {
        return new AStar(graph, new Weighting()
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
