package com.neba.Lidet.service;
import com.neba.Lidet.model.BirthDay;
import com.neba.Lidet.repository.BirthDayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;



@Slf4j
@AllArgsConstructor
public  class Bot extends TelegramLongPollingBot {

    private final BirthDayRepository birthDayRepository;
//    @Value("${BOT_TOKEN}")
//    private String botToken;
//    @Value("${BOT_NAME}")
//    private String bot_name;
String temporaryName = "";
    int dateStep = 0;
    String temporaryYear = "";
    String temporaryMonth = "";

//   private  ReminderService reminderService;



    @Override
    public String getBotUsername() {
        return "lidetoch_bot";
    }
    @Override
    public String getBotToken() {

        return "6614558388:AAHd26qrd74GgItDH2IS_d7_Zq3ltr93LtU";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                dateStep = 0;
                sendTextMessage(chatId, " Welcome to the Birthday Reminder Bot! Please enter your friend's name.");
            } else if (temporaryName != null) {
                if (dateStep == 0) {

                    temporaryName = messageText;

                    sendTextMessage(chatId, " noted " + temporaryName + " Please enter the year (YYYY) of your friend's birthday.");

                        dateStep = 1;




                } else if (dateStep == 1) {
                    temporaryYear = messageText;


                            int month = Integer.parseInt(messageText);
                            sendTextMessage(chatId, " noted " + temporaryYear + " Please enter a month (1-12) of your  friend");
dateStep= 2;



                }

                else if (dateStep == 2) {

                    temporaryMonth = messageText;
                    sendTextMessage(chatId, " noted " + temporaryMonth + " Please enter the day (1-31) of your friend's birthday.");
                            dateStep = 3;

                } else if (dateStep == 3) {
                    int year = Integer.parseInt(temporaryYear);
                    int month = Integer.parseInt(temporaryMonth);
                    int day = Integer.parseInt(messageText);

                    if (validateDate(year, month, day)) {
                        LocalDate birthdayDate = LocalDate.of(year, month, day);
                       // birthDayService.saveBirthday(chatId, temporaryName, birthdayDate);
                        System.out.println("this is birthday " + birthdayDate);
                        System.out.println("year: " + year + " month: " + month + " day: " + day);
                        BirthDay newBirthDay = BirthDay.builder()
                                .birthdayDate(birthdayDate)
                                .chatId(chatId)
                                .name(temporaryName).build();
//                        if (this.birthDayRepository != null) {
//                            this.birthDayRepository.save(newBirthDay);
//                        } else {
//                            System.out.println("ERROR OCCURED ");
//
//                        }
                        saveBirthday(newBirthDay);


                        sendTextMessage(chatId, " noted " + birthdayDate + " Friend's name and birthday saved!");
                       // sendBirthdayReminders(chatId);
                        temporaryName = null;
                        temporaryYear = null;
                        temporaryMonth = null;

                        dateStep = 0;
                    } else {
                        sendTextMessage(chatId, " Please enter a valid date.");
                    }
                }
            }
        }

    }

    private void sendTextMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }

    public void saveBirthday(BirthDay birthDay) {
        try {
            Optional<BirthDay> birthDayFind = birthDayRepository.findByName(birthDay.getName());

            if (birthDayFind.isPresent()) {
                sendTextMessage(birthDay.getChatId(), " Birthday is already present");
            }

            birthDayRepository.save(birthDay);
        } catch (Exception e) {
            log.error("Error saving birthday", e);
            sendTextMessage(birthDay.getChatId(), " An error occurred while saving the birthday.");
        }
    }
    public void saveBirthday2(BirthDay birthDay) {
        try {
            log.info("Attempting to find by name");
            Optional<BirthDay> birthDayFind = birthDayRepository.findByName(birthDay.getName());
            log.info("Find by name completed");

            // ... rest of your method ...
        } catch (Exception e) {
            log.error("Error in saveBirthday: " + e.getMessage(), e);
        }
    }


    private boolean validateDate(int year, int month, int day) {
        if (year < 1900 || year > LocalDate.now().getYear()) {
            return false;
        }

        if (month < 1 || month > 12) {
            return false;
        }

        return day >= 1 && day <= Month.of(month).maxLength();
    }

//    @Scheduled(cron = "0 0 9 * * ?") // Run the task every day at 9:00 AM
//    public void sendBirthdayReminders(Long chatId) {
//
////        reminderService.sendBirthdayReminders(chatId);
//    }
}
