package com.example.restservice.services;

import com.example.restservice.Entity.Message;
import com.example.restservice.repository.InMemoryRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDate;
import java.util.List;


public class AnalyzerBotService extends TelegramLongPollingBot {

    @Autowired
    private InMemoryRepository repository;

    private final long MY_ID = 6284830603L;
    private final long CHANNEL_ID = 1730363637L;
    private static final Dotenv env = Dotenv.configure().load();
    private final String token = env.get("TOKEN");
    private final String botName = env.get("BOT_NAME");
    private LocalDate lastCollectionDate = LocalDate.now();

    public AnalyzerBotService() {
        repository = new InMemoryRepository();
        System.out.println("AnalyzerBot initialized.");
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasChannelPost() && update.getChannelPost().getChatId() == CHANNEL_ID) {
            String messageText = update.getChannelPost().getText();
            int messageDate = update.getChannelPost().getDate();

            repository.addMessage(new Message(messageText, messageDate));
            System.out.println(repository.getLustMessage());
        }

        if (isNewDay()) {
            sandingDailyMessages();
        }
    }

    private boolean isNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastCollectionDate)) {
            lastCollectionDate = today;
            return true;
        }
        return false;
    }

    private void sandingDailyMessages() {
        List<Message> messagesForToday = repository.getDailyMessages();
        for (Message message : messagesForToday) {
            System.out.println(message.toString());
        }
    }



    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new AnalyzerBotService());
    }
}
