package async.test.demo.controller;

import async.test.demo.service.AsyncService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/async")
public class AsyncController {

  private final AsyncService asyncService;

  /**
   * 비동기 연습 1 <br>
   * 1. 외부 API 병렬 호츨 <br>
   * 2. CompletableFuture.allOf(...).join()을 통해 결과 병합 처리 <br>
   */
  @PostMapping("/info")
  public ResponseEntity<?> info() {
    CompletableFuture<?> aFuture = asyncService.externalUrlA();
    CompletableFuture<?> bFuture = asyncService.externalUrlB();

    // aFuture, bFuture 모두 완료될 때까지 대기
    CompletableFuture.allOf(aFuture, bFuture).join();

    Map<String, Object> response = new HashMap<>();
    response.put("aFuture", aFuture.join());
    response.put("bFuture", bFuture.join());

    return ResponseEntity.ok(response);
  }
}
