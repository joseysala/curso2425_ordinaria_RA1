package ordinaria;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Competicion;
import utils.TimeManager;
import utils.XmlManager;


public class Programa 
{
	private static Logger LOG = LoggerFactory.getLogger(Programa.class);

	public static void main(String[] args) 
	{
        String tiempoStr1 = "02:15:30"; // Formato HH:MM:SS
        String tiempoStr2 = "02:35:40";
        
        LOG.info("Duración en segundos: " + TimeManager.conversionASegundos(tiempoStr1));
        LOG.info("Duración en segundos: " + TimeManager.conversionASegundos(tiempoStr2));
        
        
        if (args.length != 3)
		{
			LOG.error("Error. Se esperaban dos parámetros: [xml] [xsd] [dtd]");
			System.exit(1);
		}
		
		XmlManager.setFicheroXml(new File(args[0]));
		XmlManager.setFicheroDtd(new File(args[2]));
		XmlManager.setFicheroXsd(new File(args[1]));
		
		if (! XmlManager.validarFicheros())
		{
			LOG.error("Ha fallado la validación de ficheros XML, XSD y DTD");
			System.exit(2);
		}
		
		//List<Competicion> competiciones = XmlManager.parsearXmlSax();
		List<Competicion> competiciones =XmlManager.parsearXmlDOM();
		
		if (competiciones.size() == 0)
		{
			LOG.warn("No se ha recuperado ninguna competición del fichero xml");
			System.exit(3);
		}
		
		for (Competicion comp:competiciones)
		{
			LOG.info(comp.toString());
		}
		
		XmlManager.generarXmlCampeones(competiciones);
		XmlManager.generarXmlVencidos(competiciones);
		
	}

	
}
