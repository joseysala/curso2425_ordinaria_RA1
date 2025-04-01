package utils;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class TimeManager 
{
	/**
	 * Este método permite obtener los segundos de una duración dada en formato texto: HH:MM:SS
	 * @param tiempo 
	 * @return devuelve el número de segundos
	 */
    public static long conversionASegundos(String tiempo) 
    {
    	Duration duracion = null;
        try 
        {
            String[] partes = tiempo.split(":");
            
            duracion= Duration.ofHours(Long.parseLong(partes[0]))
                    .plusMinutes(Long.parseLong(partes[1]))
                    .plusSeconds(Long.parseLong(partes[2]));
        } 
        catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) 
        {
            System.err.println("Formato de tiempo incorrecto: " + tiempo);
        }
       
        if (duracion != null)
        	return duracion.toSeconds();
        else
        	return -1;
    }

}
