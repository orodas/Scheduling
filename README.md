# Scheduling
Simulates First Come First Served, Shortest Job First, Priority Scheduling, Round Robin, and Shortest Remaining Time First scheduling.
Currently only supports small data sets and is written in Java.

Creates n randomized Job Objects each with its own arrival time, time remaining until completion, and priority.
All the tasks are stored in an array of Job Objects.
The jobs are then all "exectuted" according to which scheduling method is called.
Most methods use a binary heap to sort the jobs according to various key values.
It outputs the order which the jobs were executed and how long it took for each algorithm.
