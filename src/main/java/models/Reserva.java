package models;

import java.sql.Time;
import java.time.LocalDate;

public class Reserva {
	private String id;
	private LocalDate fecha;
	private Time hora;
	private int comensales;
	private String estado;

	public Reserva(String id, LocalDate fecha, Time hora, int comensales, String estado) {
		this.id = id;
		this.fecha = fecha;
		this.hora = hora;
		this.comensales = comensales;
		this.estado = estado;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Time getHora() {
		return hora;
	}

	public void setHora(Time hora) {
		this.hora = hora;
	}

	public int getComensales() {
		return comensales;
	}

	public void setComensales(int comensales) {
		this.comensales = comensales;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
