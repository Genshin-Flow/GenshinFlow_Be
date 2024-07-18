package com.next.genshinflow.controller;

import com.next.genshinflow.controller.response.DemoResponse;
import com.next.genshinflow.controller.swagger.DemoApi;
import com.next.genshinflow.domain.entity.DemoEntity;
import com.next.genshinflow.domain.repository.DemoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: remove
@RequestMapping("/demo")
@RestController
public class DemoController implements DemoApi {

    private final DemoRepository demoRepository;

    public DemoController(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    @GetMapping("")
    public ResponseEntity<DemoResponse> healthCheck() {
        demoRepository.save(new DemoEntity());
        return ResponseEntity.ok(new DemoResponse(true));
    }
}
