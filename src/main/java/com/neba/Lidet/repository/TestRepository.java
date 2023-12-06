package com.neba.Lidet.repository;

import com.neba.Lidet.model.TestModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestRepository extends JpaRepository<TestModel, Long>  {

    Optional<TestModel> findByName(String name);
}
