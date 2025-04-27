package async.test.demo.controller;

import async.test.demo.service.AsyncService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
   * => 클라이언트에게 업로드 성공을 주는 경우 <br>
   * 1. 여러 개의 이미지 업로드 <br>
   * 2. 각 이미지 병렬 업로드 <br>
   * 3. 위 과정을 병렬 처리 후 병합 <br>
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> upload(
      @RequestPart(value = "file", required = false) List<MultipartFile> multipartFileList) {

    List<CompletableFuture<Boolean>> futures =
        multipartFileList.stream().map(asyncService::uploadFile).toList();

    CompletableFuture<Void> allFutures =
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    allFutures.join(); // 모든 파일이 업로드될 때까지 대기

    boolean allSuccess = futures.stream().allMatch(CompletableFuture::join); // 실패 케이스가 있는지 확인

    String response = "file upload " + (allSuccess ? "success" : "fail");

    return ResponseEntity.ok(response);
  }
}
