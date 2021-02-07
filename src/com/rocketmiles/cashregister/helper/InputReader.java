package com.rocketmiles.cashregister.helper;

import java.util.Scanner;

public class InputReader {
	
	public static final String MENU_SHOW 	= "show";
	public static final String MENU_PUT 	= "put";
	public static final String MENU_TAKE 	= "take";
	public static final String MENU_CHANGE 	= "change";
	public static final String MENU_QUIT 	= "quit";
	
	public static int[] readArrayInput(Scanner s) {
		System.out.println("Enter number of bills per denomination:");

        int[] input = new int[5];
        Scanner numScanner = new Scanner(s.nextLine());
        for (int i = 0; i < 5; i++) {
            if (numScanner.hasNextInt()) {
                input[i] = numScanner.nextInt();
            } else {
                System.out.println("Input does not match the available number of bill denominations.");
                break;
            }
        }

        return input;
    }
	
	public static String readMenuSelection(Scanner s) {
		System.out.println("Enter command: ");
		return s.nextLine();
	}
	
	public static int readIntInput(Scanner s) {
		System.out.println("Enter amount: ");
		int input = s.nextInt();
		s.nextLine();
		return input;
	}
}
