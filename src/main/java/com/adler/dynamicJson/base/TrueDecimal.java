package com.adler.dynamicJson.base;

import java.io.Serializable;
import java.math.BigDecimal;

public class TrueDecimal implements Serializable{
	
	private static final long serialVersionUID = -7103959842181457636L;
	private Integer integer;
	private String decimal;
	
	public TrueDecimal(){}
	
	public TrueDecimal(BigDecimal b) {
		integer = b.abs().intValue();
		decimal = b.remainder(BigDecimal.ONE).toPlainString();
	}
	
	public Integer getInteger() {
		return integer;
	}
	public void setInteger(Integer integer) {
		this.integer = integer;
	}
	public String getDecimal() {
		return decimal;
	}
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}
	
	@Override
	public String toString() {
		return "TrueDecimal [integer=" + integer + ", decimal=" + decimal + "]";
	}
	
	public BigDecimal toBigDecimal(){
		BigDecimal res = new BigDecimal(integer);
		BigDecimal dec = new BigDecimal(decimal);
		return res.add(dec);
	}

}
