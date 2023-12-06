package com.neba.Lidet;

import com.neba.Lidet.service.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@ComponentScan(basePackages = "com.neba.Lidet")
public class LidetApplication {

	public static void main(String[] args) {

		SpringApplication.run(LidetApplication.class, args);
		try{
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new Bot());
			System.out.println("Bot started succesffully");
		}
		catch(TelegramApiException e){
			e.printStackTrace();

		}
	}

}
