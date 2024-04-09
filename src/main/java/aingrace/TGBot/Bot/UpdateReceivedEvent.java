package aingrace.TGBot.Bot;

import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateReceivedEvent extends ApplicationEvent {

    public final Update update;

    public UpdateReceivedEvent(Object source, Update update) {
        super(source);
        this.update = update;
    }
}
