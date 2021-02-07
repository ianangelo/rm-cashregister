package com.rocketmiles.cashregister.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CashRegisterHelper {
	
	public Map<Integer, Integer> cashRegistry = new LinkedHashMap<>();
	private List<Integer> cashDenominations = Arrays.asList(20, 10, 5, 2, 1);
	
	
	public CashRegisterHelper() {
		cashRegistry = initializeCashRegistry();
	}
	
	public CashRegisterHelper(List<Integer> cashDenominations) {
		this.cashDenominations = cashDenominations;
		cashRegistry = initializeCashRegistry();
	}
	
	private Map<Integer, Integer> initializeCashRegistry() {
		return cashDenominations.stream()
				.collect(Collectors.toMap(Function.identity(), e -> 0));
	}
	
	public Map<Integer, Integer> getCashRegistry() {
		return cashRegistry;
	}
	
	public List<Integer> getCashDenominations() {
		return cashDenominations;
	}
	
	public String getCurrentState() {
		
		int totalAmount = getBillsTotal();
		LinkedHashMap<Integer, Integer> sortedCashRegistry = new LinkedHashMap<>();
		cashRegistry.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())) 
		    .forEachOrdered(x -> sortedCashRegistry.put(x.getKey(), x.getValue()));
		
		
		String state = sortedCashRegistry.values()
                .stream()
                .sequential()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
		
		return new StringBuilder()
				.append("$")
				.append(totalAmount)
				.append(" ")
				.append(state)
				.toString();
		
	}
	
	public int getBillsTotal() {
		return cashRegistry.entrySet()
				.stream()
				.mapToInt(i -> i.getValue() * i.getKey())
				.sum();
	}
	
	public void putBills(int[] bills) {
		if (bills.length != cashDenominations.size()) return;
		updateBills(bills, "ADD");
	}
	
	public void takeBills(int[] bills) {
		if (bills.length != cashDenominations.size()) return;
		updateBills(bills, "SUBTRACT");
	}
	
	public int[] parseInputData(String[] input) throws NumberFormatException{
		int[] bills = new int[input.length];
		if (input.length != cashDenominations.size()) {
			System.err.println("Number of bills entered does not much the saved number of cash denominations. ");
		} else {
			for (int i = 0; i < input.length; i++) {
				bills[i] = Integer.parseInt(input[i]);
			}
		}
		
		return bills;
	}
	
	public void updateBills(int[] bills, String operation) {
		if (bills.length != cashDenominations.size()) return;
		
		IntStream.range(0, bills.length)
			.forEachOrdered(i -> {
				int currentDenomination = cashDenominations.get(i);
				int updatedCount = cashRegistry.getOrDefault(currentDenomination, 0);
				if (operation.equals("ADD")) {
					updatedCount += bills[i];
				} else if (operation.equals("SUBTRACT")) {
					updatedCount -= bills[i];
					if (updatedCount < 0) { // Insufficient funds, resetting to 0
						System.out.println("Warning: No more bills remaining for $"+currentDenomination+" + cash denomination");
						updatedCount = 0;
					}
				}
				cashRegistry.replace(currentDenomination, updatedCount);
			});
	}
	
	public String getChange(int change) {
		int[] denominations =  cashDenominations.stream()
				.sorted(Comparator.reverseOrder())
				.mapToInt(i->i).toArray();
		LinkedList<Integer> sortedBillsCount = new LinkedList<Integer>();
		cashRegistry.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())) 
		    .forEachOrdered(x -> sortedBillsCount.add(x.getValue()));
		int[] billsCount = sortedBillsCount.stream()
				.mapToInt(Integer::intValue)
				.toArray();
		int[] availableBills = getAvailableBills(denominations, billsCount);
		Map<Integer, Integer> billsMap = new HashMap<Integer, Integer>();
    	for (int bill : availableBills) {
    		int currentCount = billsMap.getOrDefault(bill, 0) + 1;
    		billsMap.put(bill, currentCount);
    	}
		List<Integer> coinsChangeList = recurChangeCombinations(
				change, 
				denominations, 
				new int[1000], 
				0, 
				0, 
				0, 
				availableBills,
				billsMap);
		
		if (coinsChangeList == null) {
			return "sorry - insufficient funds";
		}
		
		Map<Integer, Integer> changeRegistry = getCashDenominations()
        		.stream()
				.collect(Collectors.toMap(Function.identity(), e -> 0));
		
		Map<Integer, Integer> originalCashRegistry = new LinkedHashMap<Integer, Integer>(cashRegistry);
		
		for (int bill : coinsChangeList) {
        	changeRegistry.put(bill, changeRegistry.getOrDefault(bill, 0) + 1);
        	int updatedCount = cashRegistry.getOrDefault(bill, 0) - 1;
        	if (updatedCount < 0) {
        		cashRegistry = originalCashRegistry;
        		return "sorry - insufficient funds";
        	}
        	cashRegistry.put(bill, updatedCount); 
        }
		
		return parseChangeToStr(changeRegistry);
	}
	
	private String parseChangeToStr(Map<Integer, Integer> changeRegistry) {
		LinkedHashMap<Integer, Integer> sortedChangeRegistry = new LinkedHashMap<>();
		changeRegistry.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())) 
		    .forEachOrdered(x -> sortedChangeRegistry.put(x.getKey(), x.getValue()));
		
		
		return sortedChangeRegistry.values()
                .stream()
                .sequential()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
	}
	
	private List<Integer> getPossibleChangeList(
			int[] changeCombinations, 
			int index, 
			int[] availableBills, 
			int targetAmount,
			Map<Integer, Integer> billsMap) {
    	int currentCombinationTotal = 0;
    	List<Integer> retList = new ArrayList<Integer>();
    	Map<Integer, Integer> currentMap = new HashMap<Integer, Integer>();
        for(int i=0;i < index; i++) {
        	int current = changeCombinations[i];
        	// Track each entry of combination
        	if ( Arrays.stream(availableBills).anyMatch(bill -> bill == current)) {
        		currentCombinationTotal += current;
        		retList.add(current);
        		currentMap.put(current, currentMap.getOrDefault(current, 0) + 1);
        		
        	}
        }
        if (currentCombinationTotal == targetAmount) {
        	//Check if the combination matches the availability of bills:
        	boolean isCombinationValid = billsMap.entrySet().parallelStream()
            	.allMatch(entry -> entry.getValue() >= currentMap.getOrDefault(entry.getKey(), -1));
        	return isCombinationValid ? retList : null;
        }
        return null;
    }
	
	private int[] getAvailableBills(int[] bills, int[] counts) {
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
	
	private List<Integer>  recurChangeCombinations(
			int targetChange, 
			int[] amounts, 
			int[] combo, 
			int startIndex, 
			int currentCombinationTotal, 
			int index, 
			int[] availableBills,
			Map<Integer, Integer> billsMap) {
		
        if(currentCombinationTotal == targetChange) {
        	List<Integer> currentList = getPossibleChangeList(combo, index, availableBills, targetChange, billsMap);
            if (currentList != null ) {
            	return currentList;
            }
        }

        if(currentCombinationTotal > targetChange) {
            return null;
        }


        for(int i = 0; i < amounts.length; i++) {
            currentCombinationTotal = currentCombinationTotal + amounts[i];
            if (index >= combo.length) {
            	return null;
            }
            combo[index] = amounts[i];
            List<Integer> currentList = recurChangeCombinations(
            		targetChange, 
            		amounts, combo, 
            		startIndex, 
            		currentCombinationTotal, 
            		index + 1, 
            		availableBills,
            		billsMap);
            if (currentList != null) {
            	return currentList;
            }
            currentCombinationTotal = currentCombinationTotal - amounts[i];
        }
        
        return null;
    }
	
}
