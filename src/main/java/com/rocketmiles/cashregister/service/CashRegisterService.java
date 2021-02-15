package com.rocketmiles.cashregister.service;

import com.rocketmiles.cashregister.data.CashRegister;

public interface CashRegisterService {
	public String getCurrentState(CashRegister cashReg);
	public void putBills(CashRegister cashReg, int[] bills);
	public void takeBills(CashRegister cashReg, int[] bills);
	public void updateBills(CashRegister cashReg, int[] bills, String operation);
	public String getChange(CashRegister cashReg, int change);
}
