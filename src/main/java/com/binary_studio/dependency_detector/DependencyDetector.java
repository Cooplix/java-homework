package com.binary_studio.dependency_detector;

import java.util.*;

public final class DependencyDetector {

	private DependencyDetector() {
	}

	private static void stringToEdge(DependencyList libraries, Graph graph) { //build graph edge
		List<String> librariesList = new ArrayList<>(libraries.libraries);

		List<String> librariesDependencies = new ArrayList<>();


		for (String[] str : libraries.dependencies) {
			librariesDependencies.addAll(Arrays.asList(str));
		}

		//This is not the optimal way to get the index, but my knowledge is not enough to improve
		for (int i = 0; i < librariesDependencies.size() - 1; i += 2) {
			int x;
			for (int j = 0; j < librariesList.size(); j++) {
				int y;
				if (librariesDependencies.get(i).equals(librariesList.get(j))) {
					x = librariesList.indexOf(librariesList.get(j));
					for (int z = 0; z < librariesList.size(); z++) {
						if (librariesDependencies.get(i + 1).equals(librariesList.get(z))) {
							y = librariesList.indexOf(librariesList.get(z));
							graph.addEdge(x, y);
						}
					}
				}
			}
		}
	}

	public static boolean canBuild(DependencyList libraries) {
		Graph graph = new Graph(libraries.libraries.size());
		stringToEdge(libraries, graph);

		return graph.isCyclic();

	}
}
