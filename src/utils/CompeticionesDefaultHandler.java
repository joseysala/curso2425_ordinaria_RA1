package utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class CompeticionesDefaultHandler extends DefaultHandler
{
	private static Logger LOG = LoggerFactory.getLogger(CompeticionesDefaultHandler.class);
			
	private ArrayList<Competicion> competiciones = null;
	private Competicion competicion = null;	
	private Participante participante = null;
	private String tiempo = null;
	
	private StringBuilder sb = null;
	
	private final String COMPETICIONES ="competiciones";
	private final String COMPETICION ="competicion";
	private final String NOM_COMPETICION ="nombre";
	private final String FECHA_COMPETICION ="fecha";
	private final String UBICACION_COMPETICION ="ubicacion";
	private final String PARTICIPANTE_COMPETICION ="participante";
	private final String NOM_PARTICIPANTE ="nombre";
	private final String EDAD_PARTICIPANTE ="edad";
	private final String TIEMPO_PARTICIPANTE ="tiempo";
	private final String PAIS_PARTICIPANTE ="pais";
	private final String PREMIO_1 = "puesto_1";
	private final String PREMIO_2 = "puesto_2";
	private final String PREMIO_3 = "puesto_3";
	
	
	
	@Override
	public void startDocument() throws SAXException 
	{
		LOG.debug("Comenzando a parsear documento");
	}

	@Override
	public void endDocument() throws SAXException 
	{
		LOG.debug("Finalizando parseo de documento");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName)
		{
		case COMPETICIONES:
			competiciones = new ArrayList<Competicion>();
		break;
		case COMPETICION:
				competicion = new Competicion();
				competicion.setNombre(attributes.getValue(NOM_COMPETICION));
		break;
		case PARTICIPANTE_COMPETICION:
				participante = new Participante();
		break;
	
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		switch (qName)
		{
		case PREMIO_1:
			if (competicion != null)
			{
				competicion.addPremio(sb.toString(), 0);
			}
		break;
		
		case PREMIO_2:
			if (competicion != null)
			{
				competicion.addPremio(sb.toString(), 1);
			}
		break;
		case PREMIO_3:
			if (competicion != null)
			{
				competicion.addPremio(sb.toString(), 2);
			}
		break;
		
		case UBICACION_COMPETICION:
			
			String ubi = sb.toString();
			if (competicion != null)
			{
				competicion.setUbicacion(ubi);
			}
			break;
			
		case FECHA_COMPETICION:
		
			String fecha = sb.toString();
			if (competicion != null)
			{
				competicion.setFecha(LocalDate.parse(fecha));
			}
			break;
		
		case NOM_PARTICIPANTE:
		
			String nombre = sb.toString();
			if (participante != null)
			{
				participante.setNombre(nombre);
			}
		break;
		
		case PAIS_PARTICIPANTE:
		
			String pais = sb.toString();
			if (participante != null)
			{
				participante.setPais(pais);
			}
		break;

		case EDAD_PARTICIPANTE:
		
			String edad = sb.toString();
			if (participante != null)
			{
				participante.setEdad(Integer.parseInt(edad));
			}
		break;
		
		case TIEMPO_PARTICIPANTE:
			tiempo = sb.toString();
		break;
		
		case PARTICIPANTE_COMPETICION:
			if (participante != null && tiempo != null)
			{
				competicion.addResultado(participante, tiempo);
				LOG.debug("Nuevo resultado: "+ participante.getNombre() + " "+ tiempo);
			}
		break;
		
		case COMPETICION:
			if (competiciones != null && competicion != null)
			{
				competiciones.add(competicion);
				LOG.debug("Nueva competicion: "+ competicion.getNombre());
			}
		}
	
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		sb = new StringBuilder();
		sb.append(ch, start, length);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		LOG.error("Se ha detectado error durante el parseo: "+ e.getMessage());
	}

	public List<Competicion> getCompeticiones() 
	{
			return competiciones;
	}

}
