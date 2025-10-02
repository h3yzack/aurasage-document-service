package io.aurasage.document.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.aurasage.core.storage.model.StorageRequest;
import io.aurasage.document.config.FeignClientConfig;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(
    name = "aurasage-storage-service", 
    url = "${aurasage.client.storage-service.url}",
    configuration = FeignClientConfig.class)
public interface StorageServiceClient {

    @PostMapping("/storage/upload-url")
    String generateUploadUrl(@RequestBody StorageRequest request);

    @DeleteMapping("/storage")
    void deleteFile(@RequestParam String objectKey);

    @GetMapping("/storage/download-url")
    String generateDownloadUrl(@RequestParam String objectKey);
    
}
