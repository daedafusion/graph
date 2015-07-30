package com.daedafusion.graph.routing;

import com.daedafusion.graph.impl.DefaultMemoryGraph;
import com.daedafusion.graph.routing.util.Weighting;
import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.util.Edge;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mphilpot on 3/25/15.
 */
public abstract class AbstractRoutingTester
{
    private static final Logger log = Logger.getLogger(AbstractRoutingTester.class);

    public abstract RoutingAlgorithm createAlgo(Graph graph);

    @Test
    public void testCalcShortestPath()
    {
        Graph graph = createTestGraph();
        RoutingAlgorithm algo = createAlgo(graph);
        Path p = algo.calcPath(0, 7);
        assertEquals(p.toString(), createTList(0, 4, 5, 7), p.calcNodes());
    }

    @Test
    public void testCalcShortestPathReverse()
    {
        Graph graph = createTestGraph();
        RoutingAlgorithm algo = createAlgo(graph);
        Path p = algo.calcPath(7, 0);
        assertFalse(p.isFound());
    }

    @Test
    public void testCalcShortestPathWithLimit()
    {
        Graph graph = createTestGraph();
        RoutingAlgorithm algo = createAlgo(graph);
        algo.setWeightLimit(10);
        Path p = algo.calcPath(0, 7);
        assertTrue(algo.getVisitedNodes() < 7);
        assertFalse(p.isFound());
        assertEquals(p.toString(), createTList(), p.calcNodes());
    }

    @Test
    public void testNoPathFound()
    {
        Graph graph = new DefaultMemoryGraph();
        RoutingAlgorithm algo = createAlgo(graph);
        assertFalse(algo.calcPath(0, 1).isFound());

        // two disconnected areas
        graph.edge(0, 0, 1, 7);

        graph.edge(5, 0, 6, 2);
        graph.edge(5, 0, 7, 1);
        graph.edge(5, 0, 8, 1);
        graph.edge(7, 0, 8, 1);
        algo = createAlgo(graph);
        assertFalse(algo.calcPath(0, 5).isFound());
        // assertEquals(3, algo.getVisitedNodes());

        // disconnected as directed graph
        graph = new DefaultMemoryGraph();
        graph.edge(0, 0, 1, 1);
        graph.edge(0, 0, 2, 1);
        algo = createAlgo(graph);
        assertFalse(algo.calcPath(1, 2).isFound());
    }

    @Test
    public void testWikipediaShortestPath()
    {
        Graph graph = createWikipediaTestGraph();
        RoutingAlgorithm algo = createAlgo(graph);

        Path p = algo.calcPath(0, 4);
        assertEquals(p.toString(), 4, p.calcNodes().size());
    }

    @Test
    public void testCalcIf1EdgeAway()
    {
        Graph graph = createTestGraph();
        RoutingAlgorithm algo = createAlgo(graph);

        Path p = algo.calcPath(1, 2);
        assertEquals(createTList(1, 2), p.calcNodes());
    }

    public static TLongList createTList( long... list )
    {
        TLongList res = new TLongArrayList(list.length);
        for (long val : list)
        {
            res.add(val);
        }
        return res;
    }

    protected Graph createTestGraph()
    {
        Graph graph = new DefaultMemoryGraph();

        graph.edge(0, 0, 1, 7);
        graph.edge(0, 0, 4, 6);

        graph.edge(1, 0, 4, 2);
        graph.edge(1, 0, 5, 8);
        graph.edge(1, 0, 2, 2);

        graph.edge(2, 0, 5, 5);
        graph.edge(2, 0, 3, 2);

        graph.edge(3, 0, 5, 2);
        graph.edge(3, 0, 7, 10);

        graph.edge(4, 0, 6, 4);
        graph.edge(4, 0, 5, 7);

        graph.edge(5, 0, 6, 2);
        graph.edge(5, 0, 7, 1);

        Edge edge6_7 = graph.edge(6, 0, 7, 5);

//        updateDistancesFor(graph, 0, 0.0010, 0.00001);
//        updateDistancesFor(graph, 1, 0.0008, 0.0000);
//        updateDistancesFor(graph, 2, 0.0005, 0.0001);
//        updateDistancesFor(graph, 3, 0.0006, 0.0002);
//        updateDistancesFor(graph, 4, 0.0009, 0.0001);
//        updateDistancesFor(graph, 5, 0.0007, 0.0001);
//        updateDistancesFor(graph, 6, 0.0009, 0.0002);
//        updateDistancesFor(graph, 7, 0.0008, 0.0003);

//        edge6_7.setDistance(5 * edge6_7.getDistance());
        return graph;
    }

    protected Graph createWikipediaTestGraph()
    {
        Graph graph = new DefaultMemoryGraph();
        graph.edge(0, 0, 1, 7);
        graph.edge(0, 0, 2, 9);
        graph.edge(0, 0, 5, 14);
        graph.edge(1, 0, 2, 10);
        graph.edge(1, 0, 3, 15);
        graph.edge(2, 0, 5, 2);
        graph.edge(2, 0, 3, 11);
        graph.edge(3, 0, 4, 6);
        graph.edge(4, 0, 5, 9);
        return graph;
    }
}
