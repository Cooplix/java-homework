package com.binary_studio.dependency_detector;

import java.util.Collections;
import java.util.LinkedList;

public final class DependencyDetector {

	private DependencyDetector() {
	}

	public static boolean canBuild(DependencyList libraries) {
		LinkedList<String> d = new LinkedList<>();

		for (String[] x : libraries.dependencies) {
			Collections.addAll(d, x);
		}

		FloydsCycleFinding floydsCycleFinding = new FloydsCycleFinding();
		for (String x : d) {
			floydsCycleFinding.push(x);
		}

		return floydsCycleFinding.detectLoop();

	}

	public static void main(String[] args) {

	}

}
