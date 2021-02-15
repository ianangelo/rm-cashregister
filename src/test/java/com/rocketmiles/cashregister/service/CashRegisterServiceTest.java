package com.rocketmiles.cashregister.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.rocketmiles.cashregister.data.CashRegister;

public class CashRegisterServiceTest {

	private CashRegisterService service = new CashRegisterServiceImpl();

	// 1-- Tests
	@Test
	public void testShowCashRegister() {
		givenCashRegistry();
		whenShowCurrentState();
		thenCurrentStateIsShown();
	}

	@Test
	public void testPutBills() {
		givenCashRegistry();
		givenInputBills();
		whenPutBills();
		whenShowCurrentState();
		thenPutBillsSuccess();
	}

	@Test
	public void testTakeBills() {
		givenCashRegistry();
		givenInputBills();
		whenTakeBills();
		whenShowCurrentState();
		thenTakeBillsSuccess();
	}

	@Test
	public void testTakeBills_Insufficient() {
		givenCashRegistry();
		givenLargeInputBills();
		whenTakeBills();
		whenShowCurrentState();
		thenTakeBillsFailed();
	}

	@Test
	public void testGetChange() {
		givenCashRegistry();
		change = 36;
		whenGetChange();
		thenGetChangeSuccess();

	}

	@Test
	public void testGetChange_Insufficient() {
		cashReg = new CashRegister();
		change = 25;
		whenGetChange();
		thenGetChangeFailed();
	}

	// 2 -- Input
	private int[] inputBills;
	private CashRegister cashReg;
	private int change;

	// 3 -- Output
	private String currentState;
	private String returnedChange;

	// 4 -- Given

	private void givenCashRegistry() {
		cashReg = new CashRegister();
		cashReg.getCashRegistry().put(1, 5);
		cashReg.getCashRegistry().put(2, 4);
		cashReg.getCashRegistry().put(5, 3);
		cashReg.getCashRegistry().put(10, 2);
		cashReg.getCashRegistry().put(20, 1);
	}

	private void givenInputBills() {
		inputBills = new int[] { 1, 2, 3, 0, 5 };
	}

	private void givenLargeInputBills() {
		inputBills = new int[] { 5, 10, 3, 10, 5 };
	}

	// 5 -- When

	private void whenShowCurrentState() {
		currentState = service.getCurrentState(cashReg);
	}

	private void whenPutBills() {
		service.putBills(cashReg, inputBills);
	}

	private void whenTakeBills() {
		service.takeBills(cashReg, inputBills);
	}

	private void whenGetChange() {
		returnedChange = service.getChange(cashReg, change);
	}
	// 6 -- Then

	private void thenCurrentStateIsShown() {
		assertNotEquals(null, currentState);
		assertEquals("$68 1 2 3 4 5".trim(), currentState.trim());
	}

	private void thenPutBillsSuccess() {
		assertEquals("$128 2 4 6 4 10".trim(), currentState);
	}

	private void thenTakeBillsFailed() {
		assertEquals("$0 0 0 0 0 0".trim(), currentState.trim());
	}

	private void thenTakeBillsSuccess() {
		assertEquals("$8 0 0 0 4 0".trim(), currentState.trim());
	}

	private void thenGetChangeSuccess() {
		assertEquals("1 1 1 0 1".trim(), returnedChange);
	}

	private void thenGetChangeFailed() {
		assertEquals("sorry - insufficient funds", returnedChange);
	}
}
