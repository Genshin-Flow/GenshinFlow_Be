package com.next.genshinflow.controller.swagger;

import com.next.genshinflow.controller.response.DemoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Demo APi", description = "데모 API")
public interface DemoApi {

    @Operation(summary = "Demo healthCheck API", description = "demo api for testing test infrastructure")
    ResponseEntity<DemoResponse> healthCheck();
}
