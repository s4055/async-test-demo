package async.test.demo.controller;

import async.test.demo.service.AsyncService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/async")
public class AsyncController {

  private final AsyncService asyncService;

  // 외부 API 병렬 호출 및 결과 병합
  @PostMapping("/info")
  public CompletableFuture<?> info() {
    CompletableFuture<?> aFuture = asyncService.externalUrlA();
    CompletableFuture<?> bFuture = asyncService.externalUrlB();

    return CompletableFuture.allOf(aFuture, bFuture)
        .thenApply(
            voided -> {
              Map<String, Object> response = new HashMap<>();
              response.put("a", aFuture.join());
              response.put("b", bFuture.join());
              return response;
            });
  }
}
