package com.binary_studio.dependency_detector;

import java.util.*;

public final class DependencyDetector {

	private DependencyDetector() {
	}

	private static void stringToEdge(DependencyList libraries, Graph graph) {
		// build graph edge
		List<String> librariesList = new ArrayList<>(libraries.libraries);

		List<String> librariesDependencies = new ArrayList<>();

		for (String[] str : libraries.dependencies) {
			librariesDependencies.addAll(Arrays.asList(str));
		}

		// improved performance, thanks to my solution from Algorithms and Data Struct
		// finding perfect numbers
		// https://github.com/Cooplix/ASD/blob/master/Liczba%20doskonala%20v2/main.cpp
		for (int k = 0; k < librariesDependencies.size() / 2; k++) {
			graph.addEdge(librariesList.indexOf(librariesDependencies.get(2 * k)),
					librariesList.indexOf(librariesDependencies.get(2 * k + 1)));
		}
	}

	public static boolean canBuild(DependencyList libraries) {
		Graph graph = new Graph(libraries.libraries.size());
		stringToEdge(libraries, graph);

		return graph.isCyclic();

	}

}
