package com.eason.listener;

public interface Constant {
	public static final int EOF = -1;
	public static final int EOL = 10;
	public static final int WORD = 0;
	public static final int WHITE = 1;
	public static final int KEY = 2;
	public static final int COMMENT = 3;
	public static final int VAR = 4;
	public static final int STRING = 5;
	public static final int OTHER = 6;
	public static final int NUMBER = 7;
	public static final int MAXIMUM_TOKEN = 8;
	
	//Action
	public static final int UNDO = Integer.MAX_VALUE;
	public static final int REDO = UNDO - 1;
	public static final int DELETE = UNDO - 2;
	
	
	public static final int FIND = 100;
}
