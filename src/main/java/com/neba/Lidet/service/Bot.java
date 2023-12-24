package com.neba.Lidet.service;
import com.neba.Lidet.model.BirthDay;
import com.neba.Lidet.repository.BirthDayRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public  class Bot extends TelegramLongPollingBot {

    private final BirthDayRepository birthDayRepository;
    private int dateStep;
    private  String temporaryName;
   private String temporaryYear;
   private String temporaryMonth;



 public Bot(BirthDayRepository birthDayRepository){
     this.birthDayRepository = birthDayRepository;
     dateStep = 0;
     temporaryName = "";
     temporaryYear = "";
     temporaryMonth = "";
 }



    @Override
    public String getBotUsername() {

        return "lidetoch_bot";
    }
    @Override
    public String getBotToken() {

        return "6614558388:AAEH5dJd4o-oBJsl1PshdIbi4nug-M_fXa4";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            String messageText = message.getText();
            long chatId = message.getChatId();

            if (messageText.equals("/start")) {



                sendTextMessage(chatId, " Welcome to the Birthday Reminder Bot! Please enter your friend's name.");
            }


            else if (temporaryName != null) {

                if (dateStep == 0) {


                    temporaryName = messageText;


                    sendTextMessage(chatId, " noted " + temporaryName + " Please enter the year (YYYY) of your friend's birthday.");



                    dateStep = 1;



                }

                else if (dateStep == 1) {

                    temporaryYear = messageText;

                    sendTextMessage(chatId, " noted " + temporaryYear + " Please enter a month (1-12) of your friend");

                    dateStep = 2;


                }


                else if (dateStep == 2) {

                    temporaryMonth = messageText;

                    sendTextMessage(chatId, " noted " + temporaryMonth + " Please enter the day (1-31) of your friend's birthday.");
                   dateStep =3;
                } else if (dateStep == 3) {

                    int year = Integer.parseInt(temporaryYear);
                    int month = Integer.parseInt(temporaryMonth);
                    int day = Integer.parseInt(messageText);


                    if (validateDate(year, month, day)) {
                        LocalDate birthdayDate = LocalDate.of(year, month, day);
                        BirthDay newBirthDay = BirthDay.builder()
                                .birthdayDate(birthdayDate)
                                .chatId(chatId)
                                .name(temporaryName).build();

                        saveBirthday(newBirthDay);


                        sendTextMessage(chatId, " noted " + birthdayDate + " Friend's name and birthday saved!");




                        dateStep = 0;
                    } else {
                        sendTextMessage(chatId, " Please enter a valid date. by first entering /restart");
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
            sendTextMessage(chatId, " An error occurred while saving the birthday.");

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

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void happyBirthday() {
        LocalDate today = LocalDate.now();
        List<BirthDay> allBirthdays = birthDayRepository.findAll();
        Map<Long, List<BirthDay>> birthdaysByChatId = allBirthdays.stream()
                .collect(Collectors.groupingBy(BirthDay::getChatId));
        for (Map.Entry<Long, List<BirthDay>> entry : birthdaysByChatId.entrySet()) {
            Long chatId = entry.getKey();
            List<BirthDay> userBirthdays = entry.getValue();
            for (BirthDay birthday : userBirthdays) {
                if (isBirthdayToday(birthday.getBirthdayDate())) {
                    int age = calculateAge(birthday.getBirthdayDate().getYear(), today.getYear());
                    String happyBirthdayMessage = "🎉🎉🎉 Say Happy Birthday to " + birthday.getName() + " He/She will be " + age + " years old. 🎉";

                    sendTextMessage(chatId, happyBirthdayMessage);

                }
            }
        }
    }



    private boolean isBirthdayToday(LocalDate birthday) {
        LocalDate today = LocalDate.now();
        return today.getMonth() == birthday.getMonth() && today.getDayOfMonth() == birthday.getDayOfMonth();
    }


    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void sendBirthdayReminders() {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(2);
        List<BirthDay> allBirthdays = birthDayRepository.findAll();

        Map<Long, List<BirthDay>> birthdaysByChatId = allBirthdays.stream()
                .collect(Collectors.groupingBy(BirthDay::getChatId));

        for (Map.Entry<Long, List<BirthDay>> entry : birthdaysByChatId.entrySet()) {
            Long chatId = entry.getKey();
            List<BirthDay> userBirthdays = entry.getValue();

            for (BirthDay birthday : userBirthdays) {
                if (isWithinDateRangeIgnoringYear(birthday.getBirthdayDate(), today, end)) {
                    int age = calculateAge(birthday.getBirthdayDate().getYear(), today.getYear());
                    int dayLeft = birthday.getBirthdayDate().getDayOfMonth() - today.getDayOfMonth();

                    String reminderMessage = "Reminder: " + birthday.getName().toUpperCase() + "'s birthday is within " + dayLeft + " days. "
                            + dayLeft + " days left. He/She will be " + age + " years old. 🎉🎉🎉";


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



    private int calculateAge(int birthYear, int currentYear){
        return currentYear - birthYear;
    }
}
