package com.neba.Lidet.service;

import com.neba.Lidet.model.TestModel;
import com.neba.Lidet.repository.BirthDayRepository;
import com.neba.Lidet.repository.TestRepository;
import com.neba.Lidet.response.PingResponse;
import com.neba.Lidet.response.TestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestService {
    private TestRepository testRepository;

    public TestResponse saveTestBirthday(String friendName) {
        try {
            Optional<TestModel> userModel = testRepository.findByName(friendName);
            if (userModel.isEmpty()) {
                TestModel model = userModel.get();
                var userdata = TestResponse.Data.builder().name(model.getName()).build();
                return TestResponse.builder().error(false).error_msg("").data(userdata).build();
            } else {
                return TestResponse.builder().error(true).error_msg("user not found").build();
            }
        } catch (Exception e) {
            System.out.println("Error while saving " + e.toString());
            return TestResponse.builder().error(true).error_msg("error occurred " + e.toString()).build();
        }
    }

    public PingResponse ping() {
        try{
            var data =   PingResponse.Data.builder().statusMessage("the api is working").build();
            return PingResponse.builder().data(data).error(false).error_msg("").build();
        }
        catch (Exception e) {
            return PingResponse.builder().error(true).error_msg("error " + e ).build();
        }


    }
}
