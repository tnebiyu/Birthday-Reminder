//package com.neba.Lidet.service;
//
//import com.neba.Lidet.repository.BirthDayRepository;
//import com.neba.Lidet.model.BirthDay;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class ReminderService {
//
//    private BirthDayRepository birthDayRepository;
//
//
//
//    public void sendBirthdayReminders(Long chatId) {
//        LocalDate today = LocalDate.now();
//        LocalDate start = today.minusDays(7); // 7 days before today
//        LocalDate end = today.plusDays(7);   // 7 days after today
//
//        Optional<List<BirthDay>> upcomingBirthdays = birthDayRepository.findByChatIdAndBirthdayDateBetween(chatId, start, end);
//        for (BirthDay birthday : upcomingBirthdays.get()) {
//            String reminderMessage = "Don't forget! Your friend " + birthday.getName() +
//                    "'s birthday is coming up on " + birthday.getBirthdayDate() + "!";
//          //  bot.sendTextMessage(chatId, reminderMessage);
//
//        }
//    }
//}
//
