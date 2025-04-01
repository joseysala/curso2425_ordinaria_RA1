package utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Competicion 
{
	private String nombre;
	private LocalDate fecha;
	private String ubicacion;
	private HashMap<Participante,String> resultados;
	private String premios[];
	
	public Competicion()
	{
		premios = new String[3];
		resultados = new HashMap<Participante, String>();
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public HashMap<Participante, String> getResultados() {
		return resultados;
	}
	public String[] getPremios() {
		return premios;
	}
	
	public void addResultado(Participante p, String r)
	{
		if (resultados != null)
		{
			resultados.put(p, r);
		}
	}
	
	public void addPremio (String premio, int pos)
	{
		premios[pos] = premio;
	}
	@Override
	public String toString() 
	{
		return this.getNombre() + "-" + this.getUbicacion() + "-" + this.getFecha() +"- premios:" + this.premios[0] + ", "+ this.premios[1]+ ", "+ this.premios[2];
	}
	
	public Map<Participante,String> getCampeon()
	{
		long mejorTiempo = Long.MAX_VALUE;
		long tiempoParticipante;
		Participante campeon = null;
		String tiempoCampeon = null;
		Map<Participante, String> entradaCampeon  = null;
		
		if (resultados != null && resultados.size() > 0)
		{
			entradaCampeon = new HashMap<Participante,String>();
			for (Participante p: resultados.keySet())
			{
			
				tiempoParticipante = TimeManager.conversionASegundos(resultados.get(p));
				if (mejorTiempo > tiempoParticipante)
				{
					mejorTiempo = tiempoParticipante;
					campeon = p;
					tiempoCampeon = resultados.get(p);
				}
			}
			entradaCampeon.put(campeon, tiempoCampeon);
		}
		
		return entradaCampeon;
	}
	
	public Map<Participante,String> getVencidos()
	{
		long peorTiempo = 0L;
		long tiempoParticipante;
		String tiempoPerdedor = null;
		Map<Participante, String> entradasVencidos= null;
		
		if (resultados != null && resultados.size() > 0)
		{
			entradasVencidos = new HashMap<Participante,String>();
			for (Participante p: resultados.keySet())
			{
			
				tiempoParticipante = TimeManager.conversionASegundos(resultados.get(p));
				if (peorTiempo < tiempoParticipante)
				{
					peorTiempo = tiempoParticipante;
					tiempoPerdedor = resultados.get(p);
				}
				if (!this.getCampeon().containsKey(p))
						entradasVencidos.put(p, tiempoPerdedor);
			}
			
		}
		
		return entradasVencidos;
	}
	
}
