# mec_generation_tool

The jar file MECDataGenerator.jar provides a tool to generate MEC frequencies for a graph provided in a text file (i.e. MECExample.txt).

The -s flag will let you output the skeleta statistics as well as the MEC frequency distribution for each skeleta.  (Note that this is a toy script and should work quickly on up to 8 node graphs, but will start to become extremely slow and output lots of text when running across all 9, 10 node graphs). 

The program automatically buckets/hashes skeleta according to their MEC frequency distribution, and so the output will indicate whether there were at least two skeleta with the same MEC frequency distribution.  

To run the code simply do:

java -jar MECDataGenerator.jar yourfile.txt -s 

The input file should represent the adjacency list for the graph: the first line should be the number of nodes and the number of edges, and the remaining lines should represent the edges (1 indexed).

For example, MECExample.txt represents the complete bipartite graph on 6 nodes:

6 9  
1 4  
1 5  
1 6  
2 4  
2 5  
2 6  
3 4  
3 5  
3 6  

For example, the following will output the skeleton data and MEC frequency distribution for the complete bipartite graph on 6 nodes.   

Command:
java -jar MECDataGenerator.jar MECExample.txt -s 

Output:
$ 9 $ 0 $ 18 $ 3 3 3 3 3 3 $ 104 $ 230   
1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 4 4 4 4 4 4

The first line is $ delimited and represents number of edges, number of triangles, number of induced 3 paths, degree distribution, number of MECs and number of DAGs respectively.
