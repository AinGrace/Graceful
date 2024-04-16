package aingrace.TGBot.Data.Weather;


public record WeatherData(Location location, Current current, Forecast forecast) {
    public String currentWeather() {
        return String.format(
                    """
                    <b>[Погода на данный момент]</b>
                    
                    <b>Город</b> --> <u>%s</u>
                    <b>Страна</b> --> <u>%s</u>
                    <b>Погода</b> --> <u>%s</u>
                    <b>Темпратура</b> --> <u>%s градусов</u>
                    <b>Чувствуется</b> <b>как</b> --> <u>%s градусов</u>
                    <b>Скорость</b> <b>ветра</b> --> <u>%s Км/ч</u>
                    <b>Влажность</b> --> <u>%s</u>
                    
                    <blockquote>Данные обновлены в %s по местному времени</blockquote>
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
                
                    <b>Город</b> >> <u>%s</u>
                    <b>Страна</b> >> <u>%s</u>
                    <b>Погода</b> >> <u>В основном %s</u>
                    <b>Макс</b> <b>темпратура</b> >> <u>%s градусов</u>
                    <b>Мин</b> <b>темрпатура</b> >> <u>%s градусов</u>
                    <b>Средняя</b> <b>темрпатура</b> >> <u>%s градусов</u>
                    <b>Скорость</b> <b>ветра</b> >> <u>%s Км/Ч</u>
                    <b>Влажность</b> >> <u>%s</u>
                """,
                location.name(), location.country(), conditionText, day.maxtempC(),
                day.mintempC(), day.avgtempC(), day.maxwindKph(), day.avghumidity()+"%");
    }
}
