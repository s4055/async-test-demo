package async.test.demo.service;

import async.test.demo.prop.ExternalUrlProps;
import java.util.HashMap;
import java.util.Map;
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
  public CompletableFuture<Map<String, Object>> processImage(MultipartFile file) {
    return CompletableFuture.supplyAsync(
            () -> {
              Map<String, Object> result = new HashMap<>();
              String thumbnail = createThumbnail(file);
              result.put("thumbnail", thumbnail);
              return result;
            })
        .handle(
            (result, ex) -> {
              if (ex != null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Failed to process image: " + ex.getMessage());
                return errorResponse;
              }
              return result;
            })
        .exceptionally(
            ex -> {
              Map<String, Object> errorResponse = new HashMap<>();
              errorResponse.put("error", "Unexpected error: " + ex.getMessage());
              return errorResponse;
            });
  }

  private String createThumbnail(MultipartFile file) {
    return "thumbnail_" + file.getName();
  }
}
