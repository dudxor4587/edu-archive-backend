package com.backend.directory.presentation;

import com.backend.directory.dto.request.DirectoryCreateRequest;
import com.backend.directory.dto.response.DirectoryResponse;
import com.backend.directory.service.DirectoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.auth.HasRole;

@RestController
@RequestMapping("/api/directories")
@RequiredArgsConstructor
@Slf4j
public class DirectoryController {
    private final DirectoryService directoryService;

    @HasRole({"ADMIN", "MANAGER"})
    @PostMapping
    public ResponseEntity<String> createDirectory(@RequestBody @Valid DirectoryCreateRequest directoryRequest) {
        Long parentId = directoryRequest.parentId();
        String directoryName = directoryRequest.directoryName();
        directoryService.createDirectory(parentId, directoryName);
        return ResponseEntity.ok("디렉토리가 생성되었습니다.");
    }

    @GetMapping("/lists")
    public ResponseEntity<List<DirectoryResponse>> getDirectories() {
        return ResponseEntity.ok(directoryService.getDirectories());
    }
}
