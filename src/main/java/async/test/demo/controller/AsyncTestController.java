package async.test.demo.controller;

import async.test.demo.service.AsyncTestService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
public class AsyncTestController {

  private final AsyncTestService asyncTestService;

  // 비동기 예외 테스트`
  @PostMapping("/test/error")
  public ResponseEntity<String> asyncTestError() {
    asyncTestService.asyncTestError();
    return ResponseEntity.ok("success");
  }

  // 서비스 안에서 비동기 처리
  @PostMapping("/test/basic")
  public ResponseEntity<String> asyncTestBasic() {
    CompletableFuture<String> future = asyncTestService.asyncTestBasic();
    return ResponseEntity.ok("success");
  }

  // 서비스 안에서 비동기 처리한 결과를 모아서 처리
  @PostMapping("/test/chain")
  public ResponseEntity<Integer> asyncTestChain() throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> a = asyncTestService.computeA();
    CompletableFuture<Integer> b = asyncTestService.computeB();

    CompletableFuture<Integer> combined =
        a.thenCombine(b, Integer::sum)
            .thenApply(
                sum -> {
                  log.info("Sum result {}", sum);
                  return sum * 2;
                });

    return ResponseEntity.ok(combined.get());
  }

  @PostMapping("/test/chain2")
  public CompletableFuture<Integer> asyncTestChain2()
      throws ExecutionException, InterruptedException {
    return asyncTestService
        .computeA()
        .thenCombine(asyncTestService.computeB(), Integer::sum)
        .thenApply(sum -> sum * 2);
  }
}
