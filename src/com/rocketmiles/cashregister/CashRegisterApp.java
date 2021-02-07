package com.rocketmiles.cashregister;

import java.util.Scanner;

import com.rocketmiles.cashregister.helper.CashRegisterHelper;
import com.rocketmiles.cashregister.helper.InputReader;

public class CashRegisterApp {

	public static void main(String[] args) {
		
		CashRegisterHelper helper = new CashRegisterHelper();
		Scanner sc = new Scanner(System.in);
		
		boolean running = true;
		
		while (running) {
			showMenu();
			String selection = InputReader.readMenuSelection(sc);
			if (selection.equalsIgnoreCase(InputReader.MENU_SHOW)) {
				System.out.println(helper.getCurrentState());
			} else if (selection.equalsIgnoreCase(InputReader.MENU_PUT)) {
				int[] inputBills = InputReader.readArrayInput(sc);
				helper.putBills(inputBills);
				System.out.println(helper.getCurrentState());
			} else if (selection.equalsIgnoreCase(InputReader.MENU_TAKE)) {
				int[] inputBills = InputReader.readArrayInput(sc);
				helper.takeBills(inputBills);
				System.out.println(helper.getCurrentState());
			} else if (selection.equalsIgnoreCase(InputReader.MENU_CHANGE)) {
				int amount = InputReader.readIntInput(sc);
				System.out.println(helper.getChange(amount));
				System.out.println(helper.getCurrentState());
			} else if (selection.equalsIgnoreCase(InputReader.MENU_QUIT)) {
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
