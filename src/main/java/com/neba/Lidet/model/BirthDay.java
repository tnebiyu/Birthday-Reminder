package com.neba.Lidet.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "birthdays")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BirthDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String name;
    private LocalDate birthdayDate;

}
