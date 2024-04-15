package aingrace.TGBot.Data.Weather;


public record WeatherData(Location location, Current current, Forecast forecast) {
    public String currentWeather() {
        return String.format(
                    """
                    <b>[Погода на данный момент]</b>
                    
                    Город -> %s
                    Страна -> %s
                    Погода -> %s
                    Темпратура -> %s градусов
                    Чувствуется как -> %s градусов
                    Скорость ветра -> %s Км/ч
                    Влажность -> %s
                    
                    Данные обновлены в %s по местному времени
                    """,
                location.name(), location.country(), current.condition().text(),
                current.temperature(), current.feelsLikeC(), current.windKph(),
                current.humidity()+"%", current.lastUpdated().substring(11));
    }

    public String todayWeather() {
        Day day = forecast.forecastday()[0].day();
        String conditionText = day.condition().text();
        return String.format(
                """
                <b>[Погода на сегодня]</b>
                
                    Город -> %s
                    Страна -> %s
                    Погода -> В основном %s
                    Макс темпратура -> %s градусов
                    Мин темрпатура -> %s градусов
                    Средняя темрпатура -> %s градусов
                    Скорость ветра -> %s Км/Ч
                    Влажность -> %s
                """,
                location.name(), location.country(), conditionText, day.maxtempC(),
                day.mintempC(), day.avgtempC(), day.maxwindKph(), day.avghumidity()+"%");
    }
}
