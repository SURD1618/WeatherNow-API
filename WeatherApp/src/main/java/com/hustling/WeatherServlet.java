package com.hustling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    	public WeatherServlet() {
        	super();
    	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("index.html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Demo URL: https://api.openweathermap.org/data/2.5/weather?q=New%20York&appid=APIKEY
		String APIKey = "Your API KEY";
		
		String city =  request.getParameter("city");
		// Encode the city name to handle spaces and special characters
	    	String encodedCity = URLEncoder.encode(city, "UTF-8");
	    
	    	String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + APIKey;
		
		
		try {
	        //URL(String) is deprecated since Java 20. Instead of using new URL(String), we can use URI to create the URL object.
	        URI uri = new URI(apiUrl);
	        URL url = uri.toURL();

	        // API Integration
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");

	        // Read data from the API
	        InputStream inputStream = connection.getInputStream();
	        InputStreamReader reader = new InputStreamReader(inputStream);

	        StringBuilder responseContent = new StringBuilder();

	        Scanner scanner = new Scanner(reader);
	        while (scanner.hasNext()) {
	            responseContent.append(scanner.nextLine());
	        }
	        scanner.close();
			
		//System.out.println(responseContent); To check whether the details fetching from API are coming or not
			
	        Gson gson = new Gson();
            	JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
            
            	long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            	String date = new Date(dateTimestamp).toString();
            
            	double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            	int temperatureCelsius = (int) (temperatureKelvin - 273.15);
           
            	int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
            
            	double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
            
            	String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
            
            	// Sending data to the JSP page using request object
            	request.setAttribute("date", date);
            	request.setAttribute("city", city);
            	request.setAttribute("temperature", temperatureCelsius);
            	request.setAttribute("weatherCondition", weatherCondition); 
            	request.setAttribute("humidity", humidity);    
            	request.setAttribute("windSpeed", windSpeed);
            	request.setAttribute("weatherData", responseContent.toString());
            
            	connection.disconnect();

            	// Forward the request to the weather.jsp page for rendering
			
		//RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		//rd.forward(request, response);
			
		//Above two lines of code can be written in one line  
            	request.getRequestDispatcher("index.jsp").forward(request, response);
            
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
