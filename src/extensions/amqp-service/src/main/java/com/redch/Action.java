package com.redch;

public enum Action {
	ADD(0), DELETE(1);
	
	private int value;
	
	Action(int n) {
		value = n;
	}

	public int getValue() {
		return value;
	}
}
