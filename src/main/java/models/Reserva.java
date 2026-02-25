package models;

import java.sql.Time;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
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

}
