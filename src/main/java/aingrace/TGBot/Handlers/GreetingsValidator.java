package aingrace.TGBot.Handlers;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.telegram.telegrambots.meta.api.objects.message.Message;

//@Component
public class GreetingsValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Message.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Message msg = (Message) target;

        if (!msg.hasText()) {
            errors.reject("Message has no text");
            return;
        }
        if (!msg.getText().matches("[Бб]от")) errors.reject("Text is not suitable for handling");

    }
}
