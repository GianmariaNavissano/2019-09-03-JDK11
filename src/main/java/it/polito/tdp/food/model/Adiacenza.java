package it.polito.tdp.food.model;

public class Adiacenza {
	
	private String porzione1;
	private String porzione2;
	private int peso;
	
	public Adiacenza(String porzione1, String porzione2, int peso) {
		super();
		this.porzione1 = porzione1;
		this.porzione2 = porzione2;
		this.peso = peso;
	}

	public String getPorzione1() {
		return porzione1;
	}

	public void setPorzione1(String porzione1) {
		this.porzione1 = porzione1;
	}

	public String getPorzione2() {
		return porzione2;
	}

	public void setPorzione2(String porzione2) {
		this.porzione2 = porzione2;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}
	
	
	

}
