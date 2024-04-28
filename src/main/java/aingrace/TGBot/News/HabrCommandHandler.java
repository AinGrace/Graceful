package aingrace.TGBot.News;

import com.apptasticsoftware.rssreader.RssReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HabrCommandHandler {

    private static final String DAILY_TOP_POSTS = "https://habr.com/ru/rss/articles/top/daily/?fl=ru";
    private static final String WEEKLY_TOP_POSTS = "https://habr.com/ru/rss/articles/top/weekly/?fl=ru";
    private static final String MONTHLY_TOP_POSTS = "https://habr.com/ru/rss/articles/top/monthly/?fl=ru";
    private static final String YEARLY_TOP_POSTS = "https://habr.com/ru/rss/articles/top/yearly/?fl=ru";

    private final RssReader rssReader;

    @Getter @Setter
    private Map<Long, Habr.UserState> states;


    public SendMessage handle(Message message) {
        if (!message.getText().matches("/habr (daily|weekly|monthly|yearly)( \\d)?$")) return null;
        Long chatId = message.getChatId();
        String command = message.getText().substring(6);

        String rssLink = switch (command) {
            case "daily" -> DAILY_TOP_POSTS;
            case "weekly" -> WEEKLY_TOP_POSTS;
            case "monthly" -> MONTHLY_TOP_POSTS;
            case "yearly" -> YEARLY_TOP_POSTS;
            default -> throw new IllegalStateException("Unexpected value: " + command);
        };


        try (var feed = rssReader.read(rssLink)) {
            List<String> links = feed.map(item -> item.getLink().orElseThrow()).toList();

            Habr.UserState state = states.getOrDefault(chatId, new Habr.UserState());
            state.setLinks(links);
            state.setPivot(0);
            states.put(chatId, state);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text(links.getFirst())
                    .replyMarkup(Habr.getMarkup(state.getPivot()))
                    .build();


        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return null;
    }

}

