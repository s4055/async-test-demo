package async.test.demo.service;

import async.test.demo.prop.ExternalUrlProps;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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

  @Async
  public CompletableFuture<Boolean> uploadFile(MultipartFile file) {
    try {
      String filePath = UUID.randomUUID() + "_" + file.getOriginalFilename();
      file.transferTo(new File(filePath));
      log.info("file save success = {}", filePath);
      return CompletableFuture.completedFuture(true);
    } catch (Exception e) {
      log.info("{}", e.getMessage());
      log.info("file save fail = {}", file.getOriginalFilename());
      return CompletableFuture.completedFuture(false);
    }
  }
}
