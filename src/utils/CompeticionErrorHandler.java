package utils;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CompeticionErrorHandler implements ErrorHandler {


		@Override
		public void warning(SAXParseException exception) throws SAXException {
			System.err.println("Mi warning:" + exception.getMessage());
			throw new SAXException();
			
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			System.err.println("Mi error:" + exception.getMessage());
			
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			System.err.println("Mi fatal error:" + exception.getMessage());
			
		}
		
}
