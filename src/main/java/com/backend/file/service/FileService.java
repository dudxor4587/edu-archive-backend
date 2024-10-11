package com.backend.file.service;

import com.backend.directory.domain.Directory;
import com.backend.directory.domain.repository.DirectoryRepository;
import com.backend.directory.service.DirectoryService;
import com.backend.file.domain.File;
import com.backend.file.domain.repository.FileRepository;
import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.service.UserService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;

    public void uploadFile(MultipartFile file, Long directoryId, Long userId) throws IOException {
        String fileName = file.getOriginalFilename();
        String url = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Directory directory = directoryRepository.findById(directoryId).orElseThrow(
                () -> new IllegalArgumentException("Directory not found"));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found"));

        saveFile(file, url);
        fileRepository.save(File.builder()
                .name(fileName)
                .url(url)
                .directory(directory)
                .user(user)
                .build());
    }

    private void saveFile(MultipartFile file, String url) throws IOException {
        Path filePath = Paths.get("/home/ubuntu/EduArchive/edu-archive-backend/files/", url);
        Files.copy(file.getInputStream(), filePath);
    }
}
