package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlManager 
{
	private static org.slf4j.Logger LOG = LoggerFactory.getLogger(XmlManager.class);
	private static File ficheroXml;
	private static File ficheroDtd;
	private static File ficheroXsd;
	
	
	public static File getFicheroXsd() {
		return ficheroXsd;
	}

	public static void setFicheroXsd(File ficheroXsd) {
		XmlManager.ficheroXsd = ficheroXsd;
	}

	public static File getFicheroXml() {
		return ficheroXml;
	}

	public static void setFicheroXml(File ficheroXml) {
		XmlManager.ficheroXml = ficheroXml;
	}

	public static File getFicheroDtd() {
		return ficheroDtd;
	}

	public static void setFicheroDtd(File ficheroDtd) {
		XmlManager.ficheroDtd = ficheroDtd;
	}

	public static List<Competicion> getCompeticiones() {
		return competiciones;
	}

	private static List<Competicion> competiciones;
	
	public static boolean validarFicheros()
	{
		if (!ficheroXml.exists() || !ficheroXml.isFile())
		{
			System.err.println("Error con el fichero de entrada xml");
			return false;
		}
		
		if (!ficheroXsd.exists() || !ficheroXsd.isFile())
		{
			System.err.println("Error con el fichero de entrada xsd");
			return false;
		}
		
		if (!ficheroDtd.exists() || !ficheroDtd.isFile())
		{
			System.err.println("Error con el fichero de entrada dtd");
			return false;
		}
		
		if (!ficheroXml.getName().toLowerCase().endsWith("xml"))
		{
			System.err.println("Error con la extensión del fichero de entrada xml");
			return false;
		}
		
		if (!ficheroXsd.getName().toLowerCase().endsWith("xsd"))
		{
			System.err.println("Error con la extensión del fichero de entrada xsd");
			return false;
		}
		
		if (!ficheroDtd.getName().toLowerCase().endsWith("dtd"))
		{
			System.err.println("Error con la extensión del fichero de entrada dtd");
			return false;
		}
		
		return true;
	}

	public static List<Competicion> parsearXmlSax() 
	{
		SAXParserFactory spf = SAXParserFactory.newInstance(); 
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
		Schema schema = null; 
		SAXParser sp = null; 
		CompeticionesDefaultHandler competicionesDH = null; 
		
		try 
		{
			schema = sf.newSchema(ficheroXsd); 
			spf.setSchema(schema); 
			spf.setNamespaceAware(true); 
			
			sp = spf.newSAXParser();
		
			competicionesDH = new CompeticionesDefaultHandler();
			
			sp.parse(ficheroXml, competicionesDH);
			
			competiciones = competicionesDH.getCompeticiones();
			
		} 
		catch (SAXException | ParserConfigurationException | IOException e) 
		{
			System.err.println("Error durante el procesamiento del XML");
		}
		
		return competiciones;
	}
	
	
	public static void generarXmlCampeones(List<Competicion> competiciones)
	{
		// lo primero es crear la estructura de arbol en la RAM (mem ppal)
		
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db =null;
		Document documento = null;
		
		Element elementoCompeticion =null;
		
		try 
		{
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		
			documento  = db.newDocument();
			
			Element raiz = documento.createElement("vencedores");
			
			documento.appendChild(raiz);
			
			for (Competicion comp:competiciones)
			{
				elementoCompeticion = documento.createElement("competicion");
					
				elementoCompeticion.setAttribute("nombre", comp.getNombre());
				
				Element elementoParticipanteCampeon = documento.createElement("participante");
				
				
				Map<Participante, String> campeonMap = comp.getCampeon();
				
				if (!campeonMap.isEmpty())
				{
					Participante p = campeonMap.keySet().iterator().next();
					String tiempo = campeonMap.get(p);
					elementoParticipanteCampeon.setAttribute("nombre",p.getNombre());
					elementoParticipanteCampeon.setAttribute("tiempo",tiempo);
					elementoParticipanteCampeon.setAttribute("premio",comp.getPremios()[0]);
				}
					
				
				
				elementoCompeticion.appendChild(elementoParticipanteCampeon);
				
				
				raiz.appendChild(elementoCompeticion);
			}
			
			
			// a partir de ahora lo que tenemos que hacer es volcar a fichero el árbol
			
			TransformerFactory tf = null; 
			Transformer t =null;
			DOMSource ds = null;
			StreamResult sr = null;
			DOMImplementation domImp = null;
			DocumentType docType = null;
			
			String nombre_fichero_xml_salida = "vencedores_josesala.xml";
			try 
			{
				 tf= TransformerFactory.newInstance();
				 
				 t = tf.newTransformer();
				 
				 ds = new DOMSource(documento);
				 
				 sr = new StreamResult(new FileWriter(nombre_fichero_xml_salida));
				 
				 // Como nuestro XML de salida queremos que se valide contra un DTD (vencedores.dtd)
				 domImp = documento.getImplementation();
				 
				 docType = domImp.createDocumentType("doctype", null, "vencedores.dtd");
				 			 
				 t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
				 
				 t.setOutputProperty(OutputKeys.METHOD, "xml");
				 
				 t.setOutputProperty(OutputKeys.VERSION, "1.0");
				 
				 t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				 
				 t.setOutputProperty(OutputKeys.INDENT, "yes");
				 
				 t.setOutputProperty(OutputKeys.STANDALONE, "yes"); // xml independiente (se ignora)
				 
				 t.transform(ds, sr);
				 
				 LOG.info("Fichero xml creado: "+ nombre_fichero_xml_salida);
				 
			} 
			catch (TransformerConfigurationException e) 
			{
				LOG.error("Error generando XML: "+ nombre_fichero_xml_salida);
			} 
			catch (FileNotFoundException e) 
			{
				System.out.println("Error en el volcado del árbol DOM sobre el fichero XML");
			} 
			catch (TransformerException e) 
			{
				LOG.error("Error durante en el volcado del árbol DOM sobre el fichero XML");
			} 
			catch (IOException e) 
			{
				LOG.error("Error durante en el volcado del árbol DOM sobre el fichero XML");
			}
			
			
		} catch (ParserConfigurationException e) 
		{
			LOG.error("Error en la creación del árbol DOM");
		}
		
	}
	
	public static void generarXmlVencidos(List<Competicion> competiciones)
	{
		// lo primero es crear la estructura de arbol en la RAM (mem ppal)
		
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db =null;
		Document documento = null;
		
		Element elementoCompeticion =null;
		Element elementoParticipanteVencido =null;
		
		try 
		{
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		
			documento  = db.newDocument();
			
			Element raiz = documento.createElement("vencidos");
			
			documento.appendChild(raiz);
			
			for (Competicion comp:competiciones)
			{
				elementoCompeticion = documento.createElement("competicion");
					
				elementoCompeticion.setAttribute("nombre", comp.getNombre());
				
				Map<Participante, String> vencidosMap = comp.getVencidos();
								
				if (!vencidosMap.isEmpty())
				{
					for (Participante p:vencidosMap.keySet())
					{
						elementoParticipanteVencido = documento.createElement("participante");
						String tiempo = vencidosMap.get(p);
						elementoParticipanteVencido.setAttribute("nombre",p.getNombre());
						elementoParticipanteVencido.setAttribute("tiempo",tiempo);
						elementoParticipanteVencido.setAttribute("premio",comp.getPremios()[0]);
						elementoCompeticion.appendChild(elementoParticipanteVencido);
					}
					
				}
				raiz.appendChild(elementoCompeticion);
			}
			
			
			// a partir de ahora lo que tenemos que hacer es volcar a fichero el árbol
			
			TransformerFactory tf = null; 
			Transformer t =null;
			DOMSource ds = null;
			StreamResult sr = null;
			DOMImplementation domImp = null;
			DocumentType docType = null;
			
			String nombre_fichero_xml_salida = "vencidos_josesala.xml";
			try 
			{
				 tf= TransformerFactory.newInstance();
				 
				 t = tf.newTransformer();
				 
				 ds = new DOMSource(documento);
				 
				 sr = new StreamResult(new FileWriter(nombre_fichero_xml_salida));
				 
				 // Como nuestro XML de salida queremos que se valide contra un DTD (vencedores.dtd)
				 domImp = documento.getImplementation();
				 
				 docType = domImp.createDocumentType("doctype", null, "vencidos.dtd");
				 			 
				 t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
				 
				 t.setOutputProperty(OutputKeys.METHOD, "xml");
				 
				 t.setOutputProperty(OutputKeys.VERSION, "1.0");
				 
				 t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				 
				 t.setOutputProperty(OutputKeys.INDENT, "yes");
				 
				 t.setOutputProperty(OutputKeys.STANDALONE, "yes"); // xml independiente (se ignora)
				 
				 t.transform(ds, sr);
				 
				 LOG.info("Fichero xml creado: "+ nombre_fichero_xml_salida);
				 
			} 
			catch (TransformerConfigurationException e) 
			{
				LOG.error("Error generando XML: "+ nombre_fichero_xml_salida);
			} 
			catch (FileNotFoundException e) 
			{
				System.out.println("Error en el volcado del árbol DOM sobre el fichero XML");
			} 
			catch (TransformerException e) 
			{
				LOG.error("Error durante en el volcado del árbol DOM sobre el fichero XML");
			} 
			catch (IOException e) 
			{
				LOG.error("Error durante en el volcado del árbol DOM sobre el fichero XML");
			}
			
			
		} catch (ParserConfigurationException e) 
		{
			LOG.error("Error en la creación del árbol DOM");
		}
		
	}
	public static List<Competicion> parsearXmlDOM()
	{
		List<Competicion> listaCompeticiones = new ArrayList<Competicion>();
		
		Competicion comp = null;
		Participante participante =null;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		dbf.setValidating(false); // el xml NO se validará contra un DTD
		
		// Validación contra Schema (XSD)
		dbf.setNamespaceAware(true);
		
		File ficheroXsd = new File("competicion.xsd");
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema =  null;
		
		dbf.setIgnoringElementContentWhitespace(true);  // ignorar nodos con espacios en blanco (sin información útil)
		
		try 		
		{
			schema = sf.newSchema(ficheroXsd);
			dbf.setSchema(schema);
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			// para controlar los posibles errores de validación, necesito un ErrorHandler
			db.setErrorHandler(new CompeticionErrorHandler());
			
			Document documento = db.parse(ficheroXml); 
			
			Element raiz = documento.getDocumentElement();  // recuperamos el elemento raiz del xml: "competiciones"
			LOG.info("Se dispone de "+ raiz.getChildNodes().getLength()+" competiciones en el fichero XML");
			
			NodeList listaNodosCompeticiones = raiz.getChildNodes();
			
			for (int i=0; i< listaNodosCompeticiones.getLength();i++)
			{
				Node nodoCompeticion = listaNodosCompeticiones.item(i);
				
				if (nodoCompeticion.getNodeType() == Node.ELEMENT_NODE)
				{
					Element elementoCompeticion = (Element) nodoCompeticion;
					String nombre_competicion = elementoCompeticion.getAttribute("nombre");
					
					Node nodoFecha= elementoCompeticion.getFirstChild(); // esto nos devuelve el primer nodo del elemento Alumno: Expediente
					Node nodoUbicacion = nodoFecha.getNextSibling(); // esto nos devuelve el nodo contiguo 
					Node nodoParticipantes = nodoUbicacion.getNextSibling(); 
					Node nodoPremios = elementoCompeticion.getLastChild(); // esto nos devuelve el último nodo de la secuencia
					
					comp = new Competicion();
					comp.setFecha(LocalDate.parse(nodoFecha.getTextContent()));
					comp.setNombre(nombre_competicion);
					comp.setUbicacion(nodoUbicacion.getTextContent());
					
					NodeList listaNodosParticipantes = nodoParticipantes.getChildNodes();
					for (int j=0; j< listaNodosParticipantes.getLength();j++)
					{
						Node nodoParticipante = listaNodosParticipantes.item(j);
						if (nodoParticipante.getNodeType() == Node.ELEMENT_NODE)
						{
							Element elementoParticipante = (Element) nodoParticipante;
							
							Node nodoNombreParticipante= elementoParticipante.getFirstChild();
							Node nodoEdadParticipante = nodoNombreParticipante.getNextSibling(); 
							Node nodoPaisParticipante = nodoEdadParticipante.getNextSibling(); 
							Node nodoTiempoParticipante = nodoPaisParticipante.getNextSibling(); 
							
							participante = new Participante();
							participante.setNombre(nodoNombreParticipante.getTextContent());
							participante.setEdad(Integer.parseInt(nodoEdadParticipante.getTextContent()));
							participante.setPais(nodoPaisParticipante.getTextContent());
							
							comp.addResultado(participante, nodoTiempoParticipante.getTextContent());
						}
					}
					NodeList listaNodosPremios = nodoPremios.getChildNodes();
					
					for (int k=0; k< listaNodosPremios.getLength();k++)
					{
						Node nodoPremio = listaNodosPremios.item(k);
						if (nodoPremio.getNodeType() == Node.ELEMENT_NODE)
						{
							Element elementoPremio = (Element) nodoPremio;
													
							comp.addPremio(elementoPremio.getTextContent(), k);
						}
					}
					listaCompeticiones.add(comp);
				}
			}
			
		}
		catch (ParserConfigurationException e) 
		{
			System.out.println("Error durante el parseo del fichero xml");
		} 
		catch (SAXException e) 
		{
			System.out.println("Error durante el parseo del fichero xml");
		} 
		catch (IOException e) 
		{
			System.out.println("Error durante el parseo del fichero xml");
		}
		
		return listaCompeticiones;
	}

}