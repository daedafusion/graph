[![Build Status](https://travis-ci.org/daedafusion/graph.svg?branch=master)](https://travis-ci.org/daedafusion/graph)

[![Coverage Status](https://coveralls.io/repos/github/daedafusion/graph/badge.svg?branch=master)](https://coveralls.io/github/daedafusion/graph?branch=master)

# graph
Graph Algorithms (AStar, Dijkstra) adapted from Graphhopper (https://github.com/graphhopper/graphhopper/)

## about
This port of the graphhopper library changes the definition of a node and edge identifiers from an int to a long.

Additionally, the infrastructure tying the implementation to the OSM file definition has been removed so that the
base algorithms can be used independently.  The routing algorithms can now be used with Jena and other onotology engines.
