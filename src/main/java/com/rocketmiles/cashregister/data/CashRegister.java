package com.rocketmiles.cashregister.data;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CashRegister {
	
	private Map<Integer, Integer> cashRegistry = new LinkedHashMap<>();
	private List<Integer> cashDenominations = Arrays.asList(20, 10, 5, 2, 1);
	
	public CashRegister() {
		cashRegistry = initializeCashRegistry();
	}
	
	public CashRegister(List<Integer> cashDenominations) {
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
	public void setCashRegistry(Map<Integer, Integer> cashRegistry) {
		this.cashRegistry = cashRegistry;
	}
	public List<Integer> getCashDenominations() {
		return cashDenominations;
	}
	public void setCashDenominations(List<Integer> cashDenominations) {
		this.cashDenominations = cashDenominations;
	}
	
	
}
