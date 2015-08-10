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

//import com.daedafusion.graph.util.DefaultEdgeFilter;
import com.daedafusion.graph.storage.EdgeEntry;
import com.daedafusion.graph.storage.Graph;
import com.daedafusion.graph.storage.NodeAccess;
import com.daedafusion.graph.util.*;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

/**
 * Stores the nodes for the found path of an algorithm. It additionally needs the edgeIds to make
 * edge determination faster and less complex as there could be several edges (u,v) especially for
 * graphs with shortcuts.
 *
 * @author Peter Karich
 * @author Ottavio Campana
 * @author jan soe
 */
public class Path
{
    protected Graph graph;
    // we go upwards (via EdgeEntry.parent) from the goal node to the origin node
    protected boolean reverseOrder = true;
    private boolean found;
    protected EdgeEntry edgeEntry;
    private long fromNode = Long.MIN_VALUE;
    protected long endNode = Long.MIN_VALUE;
    private TLongList edgeIds;
    private double weight;
    private NodeAccess nodeAccess;

    public Path(Graph graph)
    {
        this.weight = Double.MAX_VALUE;
        this.graph = graph;
        this.nodeAccess = graph.getNodeAccess();
        this.edgeIds = new TLongArrayList();
    }

    /**
     * Populates an unextracted path instances from the specified path p.
     */
    Path(Path p)
    {
        this(p.graph);
        weight = p.weight;
        edgeIds = new TLongArrayList(p.edgeIds);
        edgeEntry = p.edgeEntry;
    }

    public Path setEdgeEntry( EdgeEntry edgeEntry )
    {
        this.edgeEntry = edgeEntry;
        return this;
    }

    protected void addEdge( long edge )
    {
        edgeIds.add(edge);
    }

    protected Path setEndNode( long end )
    {
        endNode = end;
        return this;
    }

    /**
     * We need to remember fromNode explicitely as its not saved in one edgeId of edgeIds.
     */
    protected Path setFromNode( long from )
    {
        fromNode = from;
        return this;
    }

    /**
     * @return the first node of this Path.
     */
    private long getFromNode()
    {
        if (fromNode == Long.MIN_VALUE)
            throw new IllegalStateException("Call extract() before retrieving fromNode");

        return fromNode;
    }

    public boolean isFound()
    {
        return found;
    }

    public Path setFound( boolean found )
    {
        this.found = found;
        return this;
    }

    void reverseOrder()
    {
        if (!reverseOrder)
            throw new IllegalStateException("Switching order multiple times is not supported");

        reverseOrder = false;
        edgeIds.reverse();
    }

    /**
     * This weight will be updated during the algorithm. The initial value is maximum double.
     */
    public double getWeight()
    {
        return weight;
    }

    public Path setWeight( double w )
    {
        this.weight = w;
        return this;
    }

    /**
     * Extracts the Path from the shortest-path-tree determined by edgeEntry.
     */
    public Path extract()
    {
        if (isFound())
            throw new IllegalStateException("Extract can only be called once");

        EdgeEntry goalEdge = edgeEntry;
        setEndNode(goalEdge.adjNode);
        while (goalEdge.edge != Long.MIN_VALUE)
        {
            processEdge(goalEdge.edge, goalEdge.adjNode);
            goalEdge = goalEdge.parent;
        }

        setFromNode(goalEdge.adjNode);
        reverseOrder();
        return setFound(true);
    }

    protected void processEdge( long edgeId, long adjNode )
    {
        addEdge(edgeId);
    }

    /**
     * The callback used in forEveryEdge.
     */
//    private static interface EdgeVisitor
//    {
//        void next(Edge edgeBase, int index, Edge prev);
//    }

    /**
     * Iterates over all edges in this path sorted from start to end and calls the visitor callback
     * for every edge.
     * <p>
     * @param visitor callback to handle every edge. The edge is decoupled from the iterator and can
     * be stored.
     */
//    private void forEveryEdge( EdgeVisitor visitor )
//    {
//        long tmpNode = getFromNode();
//        int len = edgeIds.size();
//        for (int i = 0; i < len; i++)
//        {
//            Edge edgeBase = graph.getEdge(edgeIds.get(i));
//            Edge prev = (i != 0) ? graph.getEdge(edgeIds.get(i-1)) : null;
//
//            if (edgeBase == null)
//                throw new IllegalStateException("Edge " + edgeIds.get(i) + " was empty when requested with node " + tmpNode
//                        + ", array index:" + i + ", edges:" + edgeIds.size());
//
//            visitor.next(edgeBase, i, prev);
//        }
//    }

    /**
     * Returns the list of all edges.
     */
//    public List<Edge> calcEdges()
//    {
//        final List<Edge> edges = new ArrayList<Edge>(edgeIds.size());
//        if (edgeIds.isEmpty())
//            return edges;
//
//        forEveryEdge(new EdgeVisitor()
//        {
//            @Override
//            public void next( Edge eb, int i, Edge prev )
//            {
//                edges.add(eb);
//            }
//        });
//        return edges;
//    }

    /**
     * @return the uncached node indices of the tower nodes in this path.
     */
    public TLongList calcNodes()
    {
        final TLongArrayList nodes = new TLongArrayList(edgeIds.size() + 1);
        if (edgeIds.isEmpty())
        {
            if (isFound())
            {
                nodes.add(endNode);
            }
            return nodes;
        }

        long tmpNode = getFromNode();
        nodes.add(tmpNode);

        for(int i = 0; i < edgeIds.size(); i++)
        {
            Edge edge = graph.getEdge(edgeIds.get(i));

            long next = (edge.getSubject() == tmpNode) ? edge.getObject() : edge.getSubject();

            nodes.add( next );

            tmpNode = next;
        }

        return nodes;
    }

    @Override
    public String toString()
    {
        return "edges:" + edgeIds.size();
    }

    public String toDetailsString()
    {
        String str = "";
        for (int i = 0; i < edgeIds.size(); i++)
        {
            if (i > 0)
                str += "->";

            str += edgeIds.get(i);
        }
        return toString() + ", found:" + isFound() + ", " + str;
    }
}
