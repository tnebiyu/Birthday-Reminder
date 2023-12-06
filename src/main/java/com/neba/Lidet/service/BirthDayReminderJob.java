//package com.neba.Lidet;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//public class BirthDayReminderJob implements Job {
//    private Bot bot;
//
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//
//        long chatId = (Long) context.getJobDetail().getJobDataMap().get("chatId");
//        String friendName = (String) context.getJobDetail().getJobDataMap().get("friendName");
//
//
//        sendBirthdayReminder(chatId, friendName);
//    }
//
//    public void sendBirthdayReminder(long chatId, String friendName) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText("Happy Birthday, " + friendName + "! 🎉");
//
//        try {
//            bot.execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
