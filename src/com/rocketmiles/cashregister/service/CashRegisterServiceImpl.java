package com.rocketmiles.cashregister.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rocketmiles.cashregister.helper.CashRegisterHelper;
import com.rocketmiles.cashregister.model.CashRegister;

public class CashRegisterServiceImpl implements CashRegisterService {

	CashRegisterHelper helper = new CashRegisterHelper();

	@Override
	public String getCurrentState(CashRegister cashReg) {
		int totalAmount = getBillsTotal(cashReg);
		LinkedHashMap<Integer, Integer> sortedCashRegistry = new LinkedHashMap<>();
		cashReg.getCashRegistry().entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedCashRegistry.put(x.getKey(), x.getValue()));

		String state = sortedCashRegistry.values().stream().sequential().map(Object::toString)
				.collect(Collectors.joining(" "));

		return new StringBuilder().append("$").append(totalAmount).append(" ").append(state).toString();
	}

	private int getBillsTotal(CashRegister cashReg) {
		return cashReg.getCashRegistry().entrySet().stream().mapToInt(i -> i.getValue() * i.getKey()).sum();
	}

	@Override
	public void putBills(CashRegister cashReg, int[] bills) {
		if (bills.length != cashReg.getCashDenominations().size())
			return;
		updateBills(cashReg, bills, "ADD");

	}

	@Override
	public void takeBills(CashRegister cashReg, int[] bills) {
		if (bills.length != cashReg.getCashDenominations().size())
			return;
		updateBills(cashReg, bills, "SUBTRACT");

	}

	@Override
	public void updateBills(CashRegister cashReg, int[] bills, String operation) {
		if (bills.length != cashReg.getCashDenominations().size())
			return;

		IntStream.range(0, bills.length).forEachOrdered(i -> {
			int currentDenomination = cashReg.getCashDenominations().get(i);
			int updatedCount = cashReg.getCashRegistry().getOrDefault(currentDenomination, 0);
			if (operation.equals("ADD")) {
				updatedCount += bills[i];
			} else if (operation.equals("SUBTRACT")) {
				updatedCount -= bills[i];
				if (updatedCount < 0) { // Insufficient funds, resetting to 0
					System.out.println(
							"Warning: No more bills remaining for $" + currentDenomination + " + cash denomination");
					updatedCount = 0;
				}
			}
			cashReg.getCashRegistry().replace(currentDenomination, updatedCount);
		});
	}

	@Override
	public String getChange(CashRegister cashReg, int change) {
		int[] denominations = cashReg.getCashDenominations().stream().sorted(Comparator.reverseOrder()).mapToInt(i -> i)
				.toArray();
		LinkedList<Integer> sortedBillsCount = new LinkedList<Integer>();
		cashReg.getCashRegistry().entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedBillsCount.add(x.getValue()));
		int[] billsCount = sortedBillsCount.stream().mapToInt(Integer::intValue).toArray();
		int[] availableBills = helper.getAvailableBills(denominations, billsCount);
		Map<Integer, Integer> billsMap = new HashMap<Integer, Integer>();
		for (int bill : availableBills) {
			int currentCount = billsMap.getOrDefault(bill, 0) + 1;
			billsMap.put(bill, currentCount);
		}
		List<Integer> coinsChangeList = helper.recurChangeCombinations(change, denominations, new int[1000], 0, 0, 0,
				availableBills, billsMap);

		if (coinsChangeList == null) {
			return "sorry - insufficient funds";
		}

		Map<Integer, Integer> changeRegistry = cashReg.getCashDenominations().stream()
				.collect(Collectors.toMap(Function.identity(), e -> 0));

		Map<Integer, Integer> originalCashRegistry = new LinkedHashMap<Integer, Integer>(cashReg.getCashRegistry());

		for (int bill : coinsChangeList) {
			changeRegistry.put(bill, changeRegistry.getOrDefault(bill, 0) + 1);
			int updatedCount = cashReg.getCashRegistry().getOrDefault(bill, 0) - 1;
			if (updatedCount < 0) {
				cashReg.setCashRegistry(originalCashRegistry);
				return "sorry - insufficient funds";
			}
			cashReg.getCashRegistry().put(bill, updatedCount);
		}

		return helper.parseChangeToStr(changeRegistry);
	}

}
