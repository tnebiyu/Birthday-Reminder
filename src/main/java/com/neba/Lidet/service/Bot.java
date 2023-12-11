package com.neba.Lidet.service;
import com.neba.Lidet.model.BirthDay;
import com.neba.Lidet.repository.BirthDayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@Slf4j
@AllArgsConstructor
public  class Bot extends TelegramLongPollingBot {

    private final BirthDayRepository birthDayRepository;






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
        long chatId = update.getMessage().getChatId();
        String temporaryName = "";

        int dateStep = 0;
        String temporaryYear = "";
        String temporaryMonth = "";
        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                temporaryName = "";
                dateStep = 0;
                temporaryYear = "";
                temporaryMonth = "";

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
                        System.out.println("this is birthday " + birthdayDate);
                        System.out.println("year: " + year + " month: " + month + " day: " + day);
                        BirthDay newBirthDay = BirthDay.builder()
                                .birthdayDate(birthdayDate)
                                .chatId(chatId)
                                .name(temporaryName).build();

                        saveBirthday(newBirthDay);


                        sendTextMessage(chatId, " noted " + birthdayDate + " Friend's name and birthday saved!");


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


    public void sendTextMessage(long chatId, String message) {
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



    private boolean validateDate(int year, int month, int day) {
        if (year < 1900 || year > LocalDate.now().getYear()) {
            return false;
        }

        if (month < 1 || month > 12) {
            return false;
        }

        return day >= 1 && day <= Month.of(month).maxLength();
    }
    @Scheduled(fixedRate = 20000)
    public void sendBirthdayReminders() {
        List<BirthDay> allBirthdays = birthDayRepository.findAll();

        for (BirthDay birthday : allBirthdays) {

            String reminderMessage = "Happy Birthday, " + birthday.getName() + "! 🎉";

            sendTextMessage(birthday.getChatId(), reminderMessage);
        }
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void sendBirthdayReminders1() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(7);
        LocalDate end = today.plusDays(7);
        System.out.println("Executing sendBirthdayGreetings method...");

        List<BirthDay> allBirthdays = birthDayRepository.findAll();

        Map<Long, List<BirthDay>> birthdaysByChatId = allBirthdays.stream()
                .collect(Collectors.groupingBy(BirthDay::getChatId));

        for (Map.Entry<Long, List<BirthDay>> entry : birthdaysByChatId.entrySet()) {
            Long chatId = entry.getKey();
            List<BirthDay> userBirthdays = entry.getValue();

            for (BirthDay birthday : userBirthdays) {
                if (isWithinDateRangeIgnoringYear(birthday.getBirthdayDate(), start, end)) {
                    int age = calculateAge(birthday.getBirthdayDate().getYear(), today.getYear());
                    int dayLeft = birthday.getBirthdayDate().getDayOfMonth() - today.getDayOfMonth();

                    String reminderMessage = "Reminder: " + birthday.getName() + "'s birthday is in  " + dayLeft + " days "
                            + " days left " + dayLeft + " They will be " + age + " years old.";
                    log.info(reminderMessage);
                    System.out.println(" this is the reminder message " + reminderMessage);

                    sendTextMessage(chatId, reminderMessage);
                }
            }
        }
    }



    private boolean isWithinDateRangeIgnoringYear(LocalDate date, LocalDate start, LocalDate end) {
        int monthDay = date.getDayOfYear();
        int startMonthDay = start.getDayOfYear();
        int endMonthDay = end.getDayOfYear();

        return monthDay >= startMonthDay && monthDay <= endMonthDay;
    }



    private boolean isWithinDateRange(LocalDate date, LocalDate start, LocalDate end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }


    private boolean isBirthdayToday(LocalDate birthday, LocalDate today) {
        return birthday.getMonthValue() == today.getMonthValue()
                && birthday.getDayOfMonth() == today.getDayOfMonth();
    }



    private int calculateAge(int birthYear, int currentYear){
        return currentYear - birthYear;
    }
}
