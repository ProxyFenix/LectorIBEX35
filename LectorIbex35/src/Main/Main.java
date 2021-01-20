package Main;

import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	private final static String ARCHIVO = "./src/archivoActualizacionLista.txt";

	public static void main(String[] args) {

		// Aqui llamamos al método dandole cada cuánto tiempo queremos que refresque la
		// tabla
		Scanner sc = new Scanner(System.in);
		System.out.println("¿Cada cuantos minutos quiere actualizar la información?");
		int minutos = sc.nextInt();
		System.out.println("Ha decidido actualizar la información cada " + minutos + " minuto(s).");
		int segundos = minutos * 60;

		while (true) {
			try {
				actualizarListaIbex(segundos);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void actualizarListaIbex(int segundos) throws IOException, MalformedURLException {
		// Metemos los datos en una lista ya que estamos para más orden
		ArrayList<String> listaDatos = new ArrayList<String>();

		// Cogemos la url
		URL url = new URL("https://www.bolsamadrid.es/esp/aspx/Mercados/Precios.aspx?indice=ESI100000000&punto=indice");
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
		connection.connect();

		InputStreamReader in = new InputStreamReader(connection.getInputStream());

		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(in);

		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		in.close();

		String htmlContent = builder.toString();
		String tablaPre = htmlContent.substring(htmlContent.indexOf("<td class=\"DifFlBj\"") + 1);
		String tabla = tablaPre.substring(0,tablaPre.indexOf("</tr>"));
		Pattern p = Pattern.compile("(?<=\\>)(.*?)(?=\\<)");
		Matcher m = p.matcher(tabla);
		while (m.find()) {
			listaDatos.add(m.group());
		}
		String datos = listaDatos.toString().replace(", , ", ";").replace("[", "").replace("]", "");
		String fechaDatos = datos.substring(56,76);
		String datosPreFinal = datos.replace(fechaDatos, "");
		String datosFinales = fechaDatos.replace(fechaDatos.substring(16), ";") + datosPreFinal.replace("IBEX 35&#174;", "IBEX35");
		System.out.println(datosFinales);

		// Escribimos en el fichero y escupimos por consola ya de paso
		BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO));
		// Aqui va toda la retaila
		escritor.append(datosFinales + "\n");
		escritor.close();
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
