package com.rocketmiles.cashregister.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rocketmiles.cashregister.model.CashRegister;

public class CashRegisterHelper {

	public int[] parseInputData(CashRegister cashReg, String[] input) throws NumberFormatException {
		int[] bills = new int[input.length];
		if (input.length != cashReg.getCashDenominations().size()) {
			System.err.println("Number of bills entered does not much the saved number of cash denominations. ");
		} else {
			for (int i = 0; i < input.length; i++) {
				bills[i] = Integer.parseInt(input[i]);
			}
		}

		return bills;
	}

	public String parseChangeToStr(Map<Integer, Integer> changeRegistry) {
		LinkedHashMap<Integer, Integer> sortedChangeRegistry = new LinkedHashMap<>();
		changeRegistry.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedChangeRegistry.put(x.getKey(), x.getValue()));

		return sortedChangeRegistry.values().stream().sequential().map(Object::toString)
				.collect(Collectors.joining(" "));
	}

	private List<Integer> getPossibleChangeList(int[] changeCombinations, int index, int[] availableBills,
			int targetAmount, Map<Integer, Integer> billsMap) {
		int currentCombinationTotal = 0;
		List<Integer> retList = new ArrayList<Integer>();
		Map<Integer, Integer> currentMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < index; i++) {
			int current = changeCombinations[i];
			// Track each entry of combination
			if (Arrays.stream(availableBills).anyMatch(bill -> bill == current)) {
				currentCombinationTotal += current;
				retList.add(current);
				currentMap.put(current, currentMap.getOrDefault(current, 0) + 1);

			}
		}
		if (currentCombinationTotal == targetAmount) {
			// Check if the combination matches the availability of bills:
			boolean isCombinationValid = billsMap.entrySet().parallelStream()
					.allMatch(entry -> entry.getValue() >= currentMap.getOrDefault(entry.getKey(), -1));
			return isCombinationValid ? retList : null;
		}
		return null;
	}

	public int[] getAvailableBills(int[] bills, int[] counts) {
		int sum = 0;
		for (int count : counts) {
			sum += count;
		}
		int[] returnArray = new int[sum];
		int returnArrayIndex = 0;
		for (int i = 0; i < bills.length; i++) {
			int count = counts[i];
			while (count != 0) {
				returnArray[returnArrayIndex] = bills[i];
				returnArrayIndex++;
				count--;
			}
		}
		return returnArray;
	}

	public List<Integer> recurChangeCombinations(int targetChange, int[] amounts, int[] combo, int startIndex,
			int currentCombinationTotal, int index, int[] availableBills, Map<Integer, Integer> billsMap) {

		if (currentCombinationTotal == targetChange) {
			List<Integer> currentList = getPossibleChangeList(combo, index, availableBills, targetChange, billsMap);
			if (currentList != null) {
				return currentList;
			}
		}

		if (currentCombinationTotal > targetChange) {
			return null;
		}

		for (int i = 0; i < amounts.length; i++) {
			currentCombinationTotal = currentCombinationTotal + amounts[i];
			if (index >= combo.length) {
				return null;
			}
			combo[index] = amounts[i];
			List<Integer> currentList = recurChangeCombinations(targetChange, amounts, combo, startIndex,
					currentCombinationTotal, index + 1, availableBills, billsMap);
			if (currentList != null) {
				return currentList;
			}
			currentCombinationTotal = currentCombinationTotal - amounts[i];
		}

		return null;
	}

}
