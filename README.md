# graph
Graph Algorithms (AStar, Dijkstra) adapted from Graphhopper (https://github.com/graphhopper/graphhopper/)

## about
This port of the graphhopper library changes the definition of a node and edge identifiers from an int to a long.

Additionally, the infrastructure tying the implementation to the OSM file definition has been removed so that the
base algorithms can be used independently.  The routing algorithms can now be used with Jena and other onotology engines.
