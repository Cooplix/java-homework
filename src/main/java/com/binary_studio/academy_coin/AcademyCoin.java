package com.binary_studio.academy_coin;

import java.util.ArrayList;
import java.util.stream.Stream;

public final class AcademyCoin {

	private AcademyCoin() {
	}

	public static int maxProfit(Stream<Integer> prices) {
		ArrayList<Integer> pricesToList = new ArrayList<>();
		prices.forEach(pricesToList::add);

		int maxProfit = 0;

		for (int i = 0; i < pricesToList.size() - 1; i++) {
			int profit = 0;
			if (pricesToList.get(i + 1) - pricesToList.get(i) > 0) {
				profit = pricesToList.get(i + 1) - pricesToList.get(i);
			}
			maxProfit += profit;
		}
		return maxProfit;
	}
}
