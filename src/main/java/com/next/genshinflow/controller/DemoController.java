package com.next.genshinflow.controller;

import com.next.genshinflow.controller.response.DemoResponse;
import com.next.genshinflow.controller.swagger.DemoApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: remove
@RequestMapping("/demo")
@RestController
public class DemoController implements DemoApi {

    @GetMapping("")
    public ResponseEntity<DemoResponse> healthCheck() {
        return ResponseEntity.ok(new DemoResponse(true));
    }
}
