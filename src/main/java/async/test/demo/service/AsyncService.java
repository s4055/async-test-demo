package async.test.demo.service;

import async.test.demo.prop.ExternalUrlProps;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncService {

  private final ExternalUrlProps externalUrlProps;

  @Async
  public CompletableFuture<?> externalUrlA() {
    log.info("=============== externalUrlA ===============");
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(externalUrlProps.getA(), String.class);
    return CompletableFuture.completedFuture(result);
  }

  @Async
  public CompletableFuture<?> externalUrlB() {
    log.info("=============== externalUrlB ===============");
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(externalUrlProps.getB(), String.class);
    return CompletableFuture.completedFuture(result);
  }
}
