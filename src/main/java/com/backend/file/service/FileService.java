package com.backend.file.service;

import com.backend.directory.domain.Directory;
import com.backend.directory.service.DirectoryService;
import com.backend.file.domain.File;
import com.backend.file.domain.repository.FileRepository;
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
    private final DirectoryService directoryService;

    public void uploadFile(MultipartFile file, Long directoryId) throws IOException {
        String fileName = file.getOriginalFilename();
        String url = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Directory directory = directoryService.getDirectory(directoryId);

        saveFile(file, url);
        fileRepository.save(File.builder()
                .name(fileName)
                .url(url)
                .directory(directory)
                .build());
    }

    private void saveFile(MultipartFile file, String url) throws IOException {
        Path filePath = Paths.get("/home/ubuntu/EduArchive/edu-archive-backend/files/", url);
        Files.copy(file.getInputStream(), filePath);
    }
}
