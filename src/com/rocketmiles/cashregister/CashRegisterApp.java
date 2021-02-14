package com.rocketmiles.cashregister;

import java.util.Scanner;

import com.rocketmiles.cashregister.helper.InputReader;
import com.rocketmiles.cashregister.model.CashRegister;
import com.rocketmiles.cashregister.service.CashRegisterService;
import com.rocketmiles.cashregister.service.CashRegisterServiceImpl;

public class CashRegisterApp {

	public static void main(String[] args) {
		
		CashRegister cashRegister = new CashRegister();
		CashRegisterService service = new CashRegisterServiceImpl();
		Scanner sc = new Scanner(System.in);
		
		boolean running = true;
		
		while (running) {
			showMenu();
			String selection = InputReader.readMenuSelection(sc);
			if (selection.equalsIgnoreCase("show")) {
				System.out.println(service.getCurrentState(cashRegister));
			} else if (selection.equalsIgnoreCase("put")) {
				int[] inputBills = InputReader.readArrayInput(sc);
				service.putBills(cashRegister, inputBills);
				System.out.println(service.getCurrentState(cashRegister));
			} else if (selection.equalsIgnoreCase("take")) {
				int[] inputBills = InputReader.readArrayInput(sc);
				service.takeBills(cashRegister, inputBills);
				System.out.println(service.getCurrentState(cashRegister));
			} else if (selection.equalsIgnoreCase("change")) {
				int amount = InputReader.readIntInput(sc);
				System.out.println(service.getChange(cashRegister, amount));
				System.out.println(service.getCurrentState(cashRegister));
			} else if (selection.equalsIgnoreCase("quit")) {
				running = false;
				sc.close();
				System.out.println("Session has been closed. Thanks for using this app.");
				
			} else {
				System.out.println("Invalid command.");
			}
		}
		
		
	}
	
	public static void showMenu() {
		System.out.println("********************************");
		System.out.println("Select an action: ");
		System.out.println("[show] - Show current state");
		System.out.println("[put]  - Put bills in each denomination");
		System.out.println("[take] - Take bills in each denomination");
		System.out.println("[change] - Request change");
		System.out.println("[quit] - Exit session");
		
	}
}
