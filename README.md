# Weather API

## Overview
This project is a Java-based web application that provides real-time weather data for any specified location. The application is built using Servlets and JSP, and it integrates with the OpenWeatherMap API to fetch and display current weather conditions. The user interface is developed using HTML, CSS, and JavaScript, providing a clean and interactive experience.

## Features
- **Real-Time Weather Data:** Get up-to-date weather information for any city worldwide.
- **Dynamic Weather Icons:** The application displays weather icons that correspond to the current weather condition of the selected city.
- **User-Friendly Interface:** The interface is intuitive and responsive, making it easy to search for weather data.
- **Servlet and JSP Integration:** The application showcases the use of Java Servlets and JSP for backend processing and dynamic content rendering.

## Technologies Used
- **Frontend:** HTML, CSS, JavaScript
- **Backend:** Java Servlets, JSP
- **API:** OpenWeatherMap API
- **Build Tool:** Apache Maven

## How It Works

### Client-Side
- The `index.html` file serves as the entry point for the application. It includes a search form where users can enter the name of a city to get weather details.
- Upon form submission, the data is sent to the `WeatherServlet` using the `POST` method. The form action is set to the servlet's URL pattern defined in `web.xml`.
- JavaScript is used to dynamically display weather icons based on the weather condition returned from the API.

### Server-Side
- The `WeatherServlet.java` handles the HTTP POST requests from the client. It fetches the weather details from the OpenWeatherMap API using the city name provided by the user.
- The servlet processes the API response, extracting relevant weather information such as temperature, humidity, wind speed, and weather condition.
- The servlet forwards the data to the `index.jsp` page, where it is rendered and displayed to the user.

### Servlet and JSP Integration
- The `web.xml` file is crucial for configuring the servlet and defining the URL patterns that map to the servlet class. This allows for seamless communication between the client-side HTML and the server-side Java code.
- The `index.jsp` page is responsible for rendering the weather details fetched by the servlet. It uses JSP expressions to display the data dynamically.

## Main Code Files

### `index.html`
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Weather App</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="style.css" />
</head>
<body>
    <div style="color:white; text-align:center">
        <img src="images/giphy.webp" style="height: auto; width: auto; filter: drop-shadow(10px 10px 10px black); border-radius: 50%; overflow: hidden; margin-bottom: 30px;" >
        <h1>Welcome to the Weather Web API</h1>
        <form action="WeatherServlet" method="post" class="searchInput">
            <input type="text" placeholder="Enter City Name" id="searchInput" name="city"/>
            <button id="searchButton"><i class="fa-solid fa-magnifying-glass"></i></button>
        </form>    
    </div>
</body>
</html>
```

### `WeatherServlet.java`
```java
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String APIKey = "Your API KEY";
        String city = request.getParameter("city");
        String encodedCity = URLEncoder.encode(city, "UTF-8");
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + APIKey;

        try {
            URI uri = new URI(apiUrl);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            StringBuilder responseContent = new StringBuilder();
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }
            scanner.close();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int temperatureCelsius = (int) (temperatureKelvin - 273.15);
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            request.setAttribute("date", date);
            request.setAttribute("city", city);
            request.setAttribute("temperature", temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition);
            request.setAttribute("humidity", humidity);
            request.setAttribute("windSpeed", windSpeed);
            request.setAttribute("weatherData", responseContent.toString());

            connection.disconnect();
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### `index.jsp`
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Weather App </title>
     <link rel="stylesheet" href="style.css" />
     <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
</head>

<body>

    <div class="mainContainer">
     <form action="WeatherServlet" method="post" class="searchInput">
            <input type="text" placeholder="Enter City Name" id="searchInput" name="city"/>
            <button id="searchButton"><i class="fa-solid fa-magnifying-glass"></i></button>
      </form>
        <div class="weatherDetails">
            <div class="weatherIcon">
                <img src="" alt="Clouds" id="weather-icon">
                <h2>${temperature} °C</h2>
                 <input type="hidden" id="wc" value="${weatherCondition}"> </input>
            </div>
            
            <div class="cityDetails">        
                <div class="desc"><strong>${city}</strong></div>
                <div class="date">${date}</div>
            </div>
            <div class="windDetails">
            	<div class="humidityBox">
            	<img src="https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhgr7XehXJkOPXbZr8xL42sZEFYlS-1fQcvUMsS2HrrV8pcj3GDFaYmYmeb3vXfMrjGXpViEDVfvLcqI7pJ03pKb_9ldQm-Cj9SlGW2Op8rxArgIhlD6oSLGQQKH9IqH1urPpQ4EAMCs3KOwbzLu57FDKv01PioBJBdR6pqlaxZTJr3HwxOUlFhC9EFyw/s320/thermometer.png" alt="Humidity">
                <div class="humidity">
                   <span>Humidity </span>
                   <h2>${humidity}% </h2>
                </div>
               </div> 
               
                <div class="windSpeed">
                    <img src="https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiyaIguDPkbBMnUDQkGp3wLRj_kvd_GIQ4RHQar7a32mUGtwg3wH

2ofD61c84F7TRhO4S_EWaMD8ztXZGcoGGA3IsjCxnEX6_RqY5QCMxW14aTxYHq5nZ8LnE9TGEpU1muGmNZbOXOM5AEBzeDMJpUG4UQevQnEITGblCjctGzMQd9k6YToWrxDi4m8Xg/s256/wind-blowing.png" alt="Humidity">
                    <span> Wind Speed</span>
                    <h2>${windSpeed} Km/hr</h2>
                </div>
            </div>
        </div> 
    </div>
    <script src="weather-icons.js"></script>
</body>

</html>
```

### `myScript.js`
```javascript
document.addEventListener("DOMContentLoaded", () => {
    const weatherCondition = document.getElementById("wc").value.toLowerCase();
    const icon = document.getElementById("weather-icon");

    switch (weatherCondition) {
        case "clear":
            icon.src = "https://cdn.pixabay.com/photo/2013/07/12/14/44/sun-149447_1280.png";
            break;
        case "clouds":
            icon.src = "https://cdn-icons-png.flaticon.com/512/414/414825.png";
            break;
        case "rain":
            icon.src = "https://cdn-icons-png.flaticon.com/512/1163/1163624.png";
            break;
        case "snow":
            icon.src = "https://cdn-icons-png.flaticon.com/512/6425/6425941.png";
            break;
        case "mist":
            icon.src = "https://cdn-icons-png.flaticon.com/512/1197/1197102.png";
            break;
        default:
            icon.src = "https://cdn.pixabay.com/photo/2017/02/12/21/29/weather-2063251_1280.png";
            break;
    }
});
```

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Apache Tomcat (or any Java web server)
- Apache Maven (for build management)
- An API key from [OpenWeatherMap](https://openweathermap.org/)

### Running the Application
1. Clone the repository to your local machine.
2. Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
3. Update the `WeatherServlet.java` file with your OpenWeatherMap API key.
4. Build the project using Maven.
5. Deploy the `.war` file to your Apache Tomcat server.
6. Access the application in your browser at `http://localhost:8080/weather-app/`.

## Future Enhancements
- **Add Forecast Data:** Extend the application to display a 5-day weather forecast.
- **Implement React Frontend:** Upgrade the UI to be more responsive and attractive using React.
- **Dynamic Background:** Change the background of the application according to the weather of the specified city.
