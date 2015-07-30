package com.daedafusion.graph.routing;

import com.daedafusion.graph.routing.util.Weighting;
import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.util.Edge;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 3/25/15.
 */
public class DijkstraBidrectionalRefTest extends AbstractRoutingTester
{
    private static final Logger log = Logger.getLogger(DijkstraBidrectionalRefTest.class);

    public RoutingAlgorithm createAlgo(Graph graph)
    {
        return new DijkstraBidirectionRef(graph, new Weighting()
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
