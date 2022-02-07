import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class WebScraper {

    public WebScraper() {
        String url = "https://www.fly540.com/flights/nairobi-to-mombasa?isoneway=0&depairportcode=NBO&arrvairportcode=MBA&date_from=111&date_to=222&adult_no=1&children_no=0&infant_no=0&currency=USD&searchFlight=";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 10);
        String tenDays = (String.valueOf(calendar.getTime()));
        calendar.add(Calendar.DATE, 7);
        String tenDaysAfterSeven = (String.valueOf(calendar.getTime()));
        calendar.add(Calendar.DATE, 3);

        String urlAfterTenDays = url.replace("111", tenDays.substring(0,3) + "%2C+" + tenDays.substring(8, 10) + "+" + tenDays.substring(4,7) + "+" + tenDays.substring(24,28)).replace("222", tenDaysAfterSeven.substring(0,3) + "%2C+" + tenDaysAfterSeven.substring(8,10) + "+" + tenDaysAfterSeven.substring(4,7) + "+" + tenDaysAfterSeven.substring(24,28));

        try {
            Document document = Jsoup.connect(urlAfterTenDays).get();
            File tenDayFlightsInCsv = new File("src\\main\\resources\\Flights_after_ten_days.csv");
            FileWriter outputfile = new FileWriter(tenDayFlightsInCsv);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] csvHeader = {"outbound_departure_airport", ";outbound_arrival_airport;outbound_departure_time;outbound_arrival_time;inbound_departure_airport;inbound_arrival_airport;inbound_departure_time;inbound_arrival_time;total_price;taxes"};
            writer.writeNext(csvHeader);

            for(Element flight : document.select("div.th.fly5-depart.fly5-flights table.table")) {
                String fullAirport = flight.select("span.flfrom").text();
                int firstOpenBracket = fullAirport.indexOf("(");
                int firstCloseBracket = fullAirport.indexOf(")");
                String startAirport = fullAirport.substring(firstOpenBracket + 1, firstCloseBracket);
                int secondOpenBracket = fullAirport.indexOf("(", firstCloseBracket);
                int secondCloseBracket = fullAirport.indexOf(")", secondOpenBracket);
                String finishAirport = fullAirport.substring(secondOpenBracket + 1, secondCloseBracket);
                String fullDate = flight.select("span.fldate").text();
                int firstComma = fullDate.indexOf(",");
                int secondComma = fullDate.indexOf(",", firstComma + 1);
                String date = fullDate.substring(firstComma + 2, secondComma);
                String totalTime = flight.select("span.fltime.ftop").text();
                int space = totalTime.indexOf(" ");
                String startTime = totalTime.substring(0, space);
                String endTime = totalTime.substring(space + 1, totalTime.length());
                String price = flight.select("span.flprice").text();
                double priceDouble = Double.parseDouble(price);

                for (Element flight2: document.select("div.th.fly5-return.fly5-flights table.table")) {
                    String fullAirport2 = flight2.select("span.flfrom").text();
                    int firstOpenBracket2 = fullAirport2.indexOf("(");
                    int firstCloseBracket2 = fullAirport2.indexOf(")");
                    String startAirport2 = fullAirport2.substring(firstOpenBracket2 + 1, firstCloseBracket2);
                    int secondOpenBracket2 = fullAirport2.indexOf("(", firstCloseBracket2);
                    int secondCloseBracket2 = fullAirport2.indexOf(")", secondOpenBracket2);
                    String finishAirport2 = fullAirport2.substring(secondOpenBracket2 + 1, secondCloseBracket2);
                    String fullDate2 = flight2.select("span.fldate").text();
                    int firstComma2 = fullDate2.indexOf(",");
                    int secondComma2 = fullDate2.indexOf(",", firstComma2 + 1);
                    String date2 = fullDate2.substring(firstComma2 + 2, secondComma2);
                    String totalTime2 = flight2.select("span.fltime.ftop").text();
                    int space2 = totalTime2.indexOf(" ");
                    String startTime2 = totalTime2.substring(0, space2);
                    String endTime2 = totalTime2.substring(space2 + 1, totalTime2.length());
                    String price2 = flight2.select("span.flprice").text();
                    double price2Double = Double.parseDouble(price2);
                    double totalPrice = priceDouble+price2Double;
                    double taxes = totalPrice*15/100f;

                    String[] data1 = {startAirport, ";"+finishAirport,";"+date + " " + startTime, ";"+date + " " + endTime, ";"+startAirport2, ";"+finishAirport2, ";"+date2 + " " + startTime2, ";"+date2 + " " + endTime2, ";"+totalPrice, ";"+taxes};
                    writer.writeNext(data1);
                }
            }
            writer.close();
            JOptionPane.showMessageDialog(null, "Process successful");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Process was not successful");
        }
    }
}
