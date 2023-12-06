package com.neba.Lidet.repository;
import com.neba.Lidet.model.BirthDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BirthDayRepository extends JpaRepository<BirthDay, Long> {

   Optional<List<BirthDay>> findByChatIdAndBirthdayDateBetween( Long chatId, LocalDate start, LocalDate end);
   @Query("SELECT u FROM birthdays u WHERE u.name = :name")
   Optional<BirthDay> findByName(@Param("name") String name);
}
