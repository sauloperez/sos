package com.redch;

public enum Action {
	ADD("add"), DELETE("delete");
	
	private String value;
	
	Action(String s) {
		value = s;
	}

	public String getValue() {
		return value;
	}
}
