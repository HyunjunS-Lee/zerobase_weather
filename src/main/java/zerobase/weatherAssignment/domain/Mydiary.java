package zerobase.weatherAssignment.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity//myDiary에서 값을 가져옵니다.
public class Mydiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //db에 맡김, 자동생성
    private int id;
    private String weather;
    private String icon;
    private double temperature;
    private String text;
    private LocalDate date;

    public void setDateWeather(DateWeather dateWeather){
        this.date = dateWeather.getDate();
        this.weather = dateWeather.getWeather();
        this.icon = dateWeather.getIcon();
        this.temperature = dateWeather.getTemperature();
    }
}
