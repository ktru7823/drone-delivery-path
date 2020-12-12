# drone-delivery-path
Using dynamic programming on a directed acyclic graph to determine an optimal path.

The scenario:
A package needs to be delivered by drone from a "source" zone (interchangeably referred to as nodes) to a "target" zone. There is a set of zones, with specified paths that drones may take. Each zone has its own drone, with its own speed and cooldown time. Upon reaching each zone, the drone must cooldown. At this point, there is a choice to continue with the same drone, or switch to the drone of the current zone. The task to the find the combination of drones and paths which minimise the delivery time.

Input:  
![input](drone-delivery-path/input.PNG)

Note that the provided graph is expected to be a directed acyclic graph.

Input explanation:  
Define "n" zones.  
Then, provide a list of (non target) zones, along with their associated drone rate and cooldown.  
Then, identify the target zone as "s".  
Then, define "m" edges.  
Then, provide a list of the edges, along with their associated weights.  

Output:  
For every node "u", the cost of the optimal delivery path from "u" to "s".  

An example of valid input (n10_0.in) and expected output (n10_0.out) is provided.
