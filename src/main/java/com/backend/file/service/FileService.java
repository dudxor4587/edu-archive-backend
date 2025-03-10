package com.backend.file.service;

import com.backend.config.FileProperties;
import com.backend.directory.domain.Directory;
import com.backend.directory.domain.repository.DirectoryRepository;
import com.backend.directory.exception.DirectoryNotFoundException;
import com.backend.file.domain.File;
import com.backend.file.domain.repository.FileRepository;
import com.backend.file.dto.response.FileResponse;
import com.backend.file.exception.FileNotFoundException;
import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.exception.UserNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final FileProperties fileProperties;

    @Transactional
    public FileResponse uploadFile(MultipartFile file, Long directoryId, Long userId) throws IOException {
        String fileName = file.getOriginalFilename();
        String url = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Directory directory = directoryRepository.findById(directoryId).orElseThrow(
                () -> new DirectoryNotFoundException("디렉토리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        saveFile(file, url);
        File savedFile = fileRepository.save(File.builder()
                .name(fileName)
                .url(url)
                .directory(directory)
                .user(user)
                .build());

        log.info("user {} uploaded file {}", userId, fileName);

        return new FileResponse(savedFile.getFileId(), savedFile.getName(), savedFile.getUrl());
    }

    private void saveFile(MultipartFile file, String url) throws IOException {
        Path filePath = Paths.get(fileProperties.getPath(), url);
        Files.copy(file.getInputStream(), filePath);
    }

    @Transactional
    public java.io.File getFile(String url) throws FileNotFoundException {
        String filePath = fileProperties.getPath() + url;
        java.io.File file = new java.io.File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }

        return file;
    }
}
