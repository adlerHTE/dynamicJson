package com.adler.test;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.adler.dynamicJson.JsonMapFactory;
import com.adler.dynamicJson.base.JsonMap;

public class JsonMapFactoryTest {

	public static class myPojo{
		private String a;
		private Long l;
		private BigDecimal e;
		private Object o;
		private List<String> ss;
		private String[] ss2;
		private List<myPojo> pp;

		public myPojo() {
		}
		public myPojo(String string) {
			this.setA(string);
		}
		public String[] getSs2() {
			return ss2;
		}
		public void setSs2(String[] ss2) {
			this.ss2 = ss2;
		}
		public List<String> getSs() {
			return ss;
		}
		public void setSs(List<String> ss) {
			this.ss = ss;
		}
		public String getA() {
			return a;
		}
		public void setA(String a) {
			this.a = a;
		}
		public Long getL() {
			return l;
		}
		public void setL(Long l) {
			this.l = l;
		}
		public BigDecimal getE() {
			return e;
		}
		public void setE(BigDecimal e) {
			this.e = e;
		}
		public Object getO() {
			return o;
		}
		public void setO(Object o) {
			this.o = o;
		}
		public List<myPojo> getPp() {
			return pp;
		}
		public void setPp(List<myPojo> pp) {
			this.pp = pp;
		}
		@Override
		public String toString() {
			return "myPojo [a=" + a + ", l=" + l + ", e=" + e + ", o=" + o
					+ ", ss=" + ss + ", ss2=" + Arrays.toString(ss2) + ", pp="
					+ pp + "]";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
			result = prime * result + ((e == null) ? 0 : e.hashCode());
			result = prime * result + ((l == null) ? 0 : l.hashCode());
			result = prime * result + ((o == null) ? 0 : o.hashCode());
			result = prime * result + ((pp == null) ? 0 : pp.hashCode());
			result = prime * result + ((ss == null) ? 0 : ss.hashCode());
			result = prime * result + Arrays.hashCode(ss2);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			myPojo other = (myPojo) obj;
			if (a == null) {
				if (other.a != null) return false;
			} else if (!a.equals(other.a)) return false;
			if (e == null) {
				if (other.e != null) return false;
			} else if (!e.equals(other.e)) return false;
			if (l == null) {
				if (other.l != null) return false;
			} else if (!l.equals(other.l)) return false;
			if (o == null) {
				if (other.o != null) return false;
			} else if (!o.equals(other.o)) return false;
			if (pp == null) {
				if (other.pp != null) return false;
			} else if (!pp.equals(other.pp)) return false;
			if (ss == null) {
				if (other.ss != null) return false;
			} else if (!ss.equals(other.ss)) return false;
			if (!Arrays.equals(ss2, other.ss2)) return false;
			return true;
		}
		
		
	}
	
	


	
	@Test
	public void testNull() {
		
		JsonMap map = JsonMapFactory.getJsonMap(null);
		
		System.err.println(map);
		Assert.assertEquals(JsonMap.NULL, map);
	}
	
	
	@Test
	public void testGoAndComeBack() throws InstantiationException, IllegalAccessException{
		myPojo m = new myPojo();
		m.setA("AAAA");
		m.setL(4L);
		m.setE(new BigDecimal("55.5"));
		System.err.println(m);
		JsonMap map = JsonMapFactory.getJsonMap(m);
		System.err.println(""+map);
		
		myPojo mm = JsonMapFactory.getObjectFromJsonMap(myPojo.class, map);
		System.err.println(m);
		System.err.println(mm);
		Assert.assertEquals(m, mm);
		
	}
	
	@Test
	public void testAddPropertyToArray() throws InstantiationException, IllegalAccessException{
		myPojo m = new myPojo();
		m.setA("AAAA");
		m.setL(4L);
		m.setE(new BigDecimal("55.5"));
		List<String> ss = new ArrayList<String>();
		ss.add("1");
		m.setSs(ss);
		m.setSs2(new String[]{"A","B"});
		List<myPojo> pp = new ArrayList<JsonMapFactoryTest.myPojo>();
		pp.add(new myPojo("aaaaa"));
		m.setPp(pp);
		JsonMap map = JsonMapFactory.getJsonMap(m);
		System.err.println(map.toFullString());
		
		myPojo mm = JsonMapFactory.getObjectFromJsonMap(myPojo.class, map);
		System.err.println(m);
		System.err.println(mm);
		Assert.assertEquals(m, mm);
	}
	
	
	
}
