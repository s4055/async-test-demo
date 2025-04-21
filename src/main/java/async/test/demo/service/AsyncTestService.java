package async.test.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncTestService {

  @Async
  public void asyncTestError() {
    log.info("=============== asyncTestException ===============");
    throw new RuntimeException("고의로 발생시킨 예외");
  }

  @Async
  public CompletableFuture<String> asyncTestBasic() {
    log.info("=============== asyncTestBasic start ===============");
    int size = 1000;
    IntStream.range(0, size).forEach(i -> log.info("=> {}", i));
    log.info("=============== asyncTestBasic end ===============");
    return CompletableFuture.completedFuture("success");
  }

  @Async
  public CompletableFuture<Integer> computeA() {
    log.info("=============== computeA start ===============");
    log.info("computeA {}", Thread.currentThread().getName());
    log.info("=============== computeA end ===============");
    return CompletableFuture.completedFuture(10);
  }

  @Async
  public CompletableFuture<Integer> computeB() {
    log.info("=============== computeB start ===============");
    log.info("computeB {}", Thread.currentThread().getName());
    log.info("=============== computeB end ===============");
    return CompletableFuture.completedFuture(30);
  }
}
