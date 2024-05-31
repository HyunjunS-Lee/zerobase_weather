package zerobase.weatherAssignment.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weatherAssignment.WeatherAssignmentApplication;
import zerobase.weatherAssignment.domain.DateWeather;
import zerobase.weatherAssignment.domain.Mydiary;
import zerobase.weatherAssignment.repository.DateWeatherRepository;
import zerobase.weatherAssignment.repository.MydiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class MyDiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final MydiaryRepository myDiaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private static final Logger logger = LoggerFactory.getLogger(WeatherAssignmentApplication.class);

    public MyDiaryService(MydiaryRepository myDiaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.myDiaryRepository = myDiaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        logger.info("started to save WeatherDate");
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createMyDiary(LocalDate date, String text){
        logger.info("started to create diary");
        DateWeather dateWeather = getDateWeather(date);

        Mydiary nowMyDiary = new Mydiary();
        nowMyDiary.setDateWeather(dateWeather);
        nowMyDiary.setText(text);
        myDiaryRepository.save(nowMyDiary);
    }

    private DateWeather getWeatherFromApi(){
        String weatherInformation = getWeatherInformation();
        Map<String, Object> parsedWeather = parseWeather(weatherInformation);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parsedWeather.get("temp"));
        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0){
            return getWeatherFromApi();
        }else{
            return dateWeatherListFromDB.get(0); //있으면 첫번째꺼 가지고 반환
        }
    }

    @Transactional(readOnly = true)
    public List<Mydiary> readMyDiary(LocalDate date){
        logger.info("Started to read my diary");
        return myDiaryRepository.findAllByDate(date);
    }

    public List<Mydiary> readMyDiaries(LocalDate firstDate, LocalDate lastDate){
        logger.info("Started to read my diaries");
        return myDiaryRepository.findAllByDateBetween(firstDate, lastDate);
    }

    public void updateMyDiary(LocalDate date, String text){
        logger.info("Started to update my diary");
        Mydiary firtDiary = myDiaryRepository.getFirstByDate(date);
        firtDiary.setText(text);
        myDiaryRepository.save(firtDiary);
    }

    public void deleteMyDiary(LocalDate date){
        logger.info("Started to delete my diary");
        myDiaryRepository.deleteAllByDate(date);
    }

    private String getWeatherInformation(){
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl); //url객체
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            BufferedReader br;
            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        }catch (Exception e){
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        }catch (ParseException e){
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }


}
