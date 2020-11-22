# drone-delivery-path
Using dynamic programming on an acyclic graph to determine an optimal path.

The scenario:
A package needs to be delivered by drone from a "source" zone to a "target" zone. There is a set of zones, with specified paths that drones may take. Each zone has its own drone, with its own speed and cooldown time. Upon reaching each zone, the drone must cooldown. At this point, there is a choice to continue with the same drone, or switch to the drone of the current zone. The task to the find the combination of drones and paths which minimise the delivery time.
