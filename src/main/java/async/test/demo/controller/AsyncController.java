package async.test.demo.controller;

import async.test.demo.service.AsyncService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

  /**
   * 비동기 연습 2 <br>
   * 1. 여러 개의 이미지 업로드 <br>
   * 2. 각 이미지에 대해서 썸네일 생성 <br>
   * 3. 위 과정을 병렬 처리 후 병합 <br>
   */
  @PostMapping("/upload")
  public CompletableFuture<ResponseEntity<?>> upload(
      @RequestPart(value = "file", required = false) List<MultipartFile> multipartFileList) {
    List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();

    for (MultipartFile file : multipartFileList) {
      CompletableFuture<Map<String, Object>> future =
          asyncService
              .processImage(file)
              .handle(
                  (result, ex) -> {
                    if (ex != null) {
                      Map<String, Object> errorResponse = new HashMap<>();
                      errorResponse.put("error", "Failed to process image: " + ex.getMessage());
                      return errorResponse;
                    }
                    return result;
                  });
      futures.add(future);
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApply(
            v -> {
              Map<String, Object> response = new HashMap<>();
              response.put("status", "All images processed successfully");
              return ResponseEntity.ok(response);
            });
  }
}
