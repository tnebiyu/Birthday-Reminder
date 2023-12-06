package com.neba.Lidet.Controller;

import com.neba.Lidet.repository.BirthDayRepository;
import com.neba.Lidet.response.PingResponse;
import com.neba.Lidet.response.TestResponse;
import com.neba.Lidet.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/birthday")
public class birthDayController {
    @Autowired
    TestService testService;
    @Autowired
    BirthDayRepository birthDayRepository;
   @PostMapping("/birthday")
    public ResponseEntity<TestResponse> saveBirthDay(@RequestBody  String name){
       TestResponse response = testService.saveTestBirthday(name);
        if (response.isError()){
          return   ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
    @PostMapping("/ping")
    public ResponseEntity<PingResponse> getPing(){

        PingResponse response = testService.ping();
        if (response.isError()){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);

    }



}
