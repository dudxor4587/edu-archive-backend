package com.backend.directory.service;

import com.backend.directory.domain.Directory;
import com.backend.directory.domain.mapper.DirectoryMapper;
import com.backend.directory.domain.repository.DirectoryRepository;
import com.backend.directory.dto.response.DirectoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final DirectoryRepository directoryRepository;

    @Transactional
    public void createDirectory(Long parentId, String directoryName) {
        if(parentId == null) {
            directoryRepository.save(Directory.builder()
                    .name(directoryName)
                    .build());
            return;
        }
        Directory parent = directoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent directory not found"));

        directoryRepository.save(Directory.builder()
                .name(directoryName)
                .parent(parent)
                .build());
    }

    @Transactional(readOnly = true)
    public List<DirectoryResponse> getDirectories() {
        List<Directory> directories = directoryRepository.findAllDirectories();
        return DirectoryMapper.toDirectoryResponse(directories);
    }

    @Transactional(readOnly = true)
    public Directory getDirectory(Long directoryId) {
        return directoryRepository.findById(directoryId)
                .orElseThrow(() -> new IllegalArgumentException("Directory not found"));
    }
}
