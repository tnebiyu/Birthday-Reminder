package com.neba.Lidet;

import com.neba.Lidet.repository.BirthDayRepository;
import com.neba.Lidet.service.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@ComponentScan(basePackages = "com.neba.Lidet")
public class LidetApplication {

	public static void main(String[] args) {

		try {
			ApplicationContext context = SpringApplication.run(LidetApplication.class, args);


			BirthDayRepository birthDayRepository = context.getBean(BirthDayRepository.class);


			String temporaryMonth = "";
			String temporaryYear = "";
			int dateStep = 0;
			String temporaryName = "";




			Bot bot = new Bot(birthDayRepository,  temporaryName, dateStep, temporaryYear,
					temporaryMonth );


			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(bot);

			System.out.println("Bot started successfully");
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
