package com.eason.listener;


class CodeScanner {
	private static final String[] FGKEYWORDS = {"declare", "define","and", "not", "or", "if", "lambda", "any"};
	private StringBuffer fDoc, fBuffer;
	private int fPos, fEnd, fStartToken;
	public CodeScanner() {
		fBuffer = new StringBuffer();
	}
	
	private boolean hasKey(String key) {
		for (int i = 0; i < FGKEYWORDS.length; i++) {
			if (FGKEYWORDS[i].equals(key))
				return true;
		}
		return false;
	}

	public final int getStartOffset() {
		return fStartToken;
	}

	public final int getLength() {
		return fPos - fStartToken;
	}
	
	public int nextToken() {
		int c;
		fStartToken = fPos;
		while (true) {
			switch (c = read()) {
			case Constant.EOF:
				return Constant.EOF;
			case ';':
				while (true) {
					c = read();
					if ((c == Constant.EOF) || (c == Constant.EOL)) {
						unread(c);
						return Constant.COMMENT;
					}
				}
			case '"':
				while(true) {
					c = read();
					switch (c) {
					case '"':
						return Constant.STRING;
					case Constant.EOF:
						unread(c);
						return Constant.STRING;
					case '\\':
						c = read();
						break;
					}
				}
			case '!':
			case '<':
			case '>':
					c = read();
					if (c == '=') {
						return Constant.VAR;
					}
			case '=': return Constant.VAR;
			case '+':
			case '*':
			case '/':
			case '-':{
				c = read();
				if (c == ' ' && c != Constant.EOF && c != Constant.EOL) {
					return Constant.VAR;
				}
			}
			default:
				if (Character.isWhitespace((char) c)) {
					do {
						c = read();
					} while (Character.isWhitespace((char) c));
					unread(c);
					return Constant.WHITE;
				}
				if (Character.isJavaIdentifierStart((char) c)) {
					fBuffer.setLength(0);
					do {
						fBuffer.append((char) c);
						c = read();
					} while (Character.isJavaIdentifierPart((char) c));
					unread(c);
					if (hasKey(fBuffer.toString()))
						return Constant.KEY;
					return Constant.WORD;
				}
				return Constant.OTHER;
			}
		}
	}

	private int read() {
		if (fPos <= fEnd) {
			return fDoc.charAt(fPos++);
		}
		return Constant.EOF;
	}
	
	private void unread(int c) {
		if (c != Constant.EOF)
			fPos--;
	}

	public void setRange(String text) {
		fDoc = new StringBuffer(text);
		fPos = 0;
		fEnd = fDoc.length() - 1;
	}
}