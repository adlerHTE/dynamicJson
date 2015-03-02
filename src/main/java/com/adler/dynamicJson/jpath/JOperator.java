package com.adler.dynamicJson.jpath;

public enum JOperator {EQ("="),GT(">"),LS("<"),GE(">="),LE("<="),NE("!=");

 private String symbol;
 
 private JOperator(String s){
	 symbol=s; 
 }

 public String getSymbol() {
	return symbol;
}

public static JOperator fromSymbol(String string) {
	if (EQ.getSymbol().equals(string)) return EQ;
	if (GT.getSymbol().equals(string)) return GT;
	if (LS.getSymbol().equals(string)) return LS;
	if (GE.getSymbol().equals(string)) return GE;
	if (LE.getSymbol().equals(string)) return LE;
	if (NE.getSymbol().equals(string)) return NE;
	return null;
}

public boolean isTrueWithCompareVal(int compare) {
	// compare = -1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val.
	if (this.equals(EQ)) return compare == 0;
	if (this.equals(NE)) return compare != 0;
	
	if (this.equals(GT)) return compare > 0;
	if (this.equals(GE)) return compare >= 0;
	
	if (this.equals(LS)) return compare < 0;
	if (this.equals(LE)) return compare <= 0;
	
	return false;
}


}
