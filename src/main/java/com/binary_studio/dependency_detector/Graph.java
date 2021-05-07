package com.binary_studio.dependency_detector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class Graph {

	// This code I taken of lectures of Algorithm and Data Structs, sorry ¯\_(ツ)_/¯
	// The first attempt was from writing Floyd's Cycle detection,
	// but it didn't pass all the tests.
	// DFS performed better in this case.

	private final int V;

	private final List<List<Integer>> adj;

	public Graph(int V) {
		this.V = V;
		adj = new ArrayList<>(V);

		for (int i = 0; i < V; i++)
			adj.add(new LinkedList<>());
	}

	private boolean isCyclicUtil(int i, boolean[] visited, boolean[] recStack) {
		if (recStack[i])
			return true;

		if (visited[i])
			return false;

		visited[i] = true;

		recStack[i] = true;
		List<Integer> children = adj.get(i);

		for (Integer c : children)
			if (isCyclicUtil(c, visited, recStack))
				return true;

		recStack[i] = false;

		return false;
	}

	public void addEdge(int source, int dest) {
		adj.get(source).add(dest);
	}

	public boolean isCyclic() {
		boolean[] visited = new boolean[V];
		boolean[] recStack = new boolean[V];

		// If you need standard DFS, change return to the opposite

		for (int i = 0; i < V; i++)
			if (isCyclicUtil(i, visited, recStack))
				return false;

		return true;
	}

}
