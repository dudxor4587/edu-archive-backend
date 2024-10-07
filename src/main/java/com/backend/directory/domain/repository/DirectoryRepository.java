package com.backend.directory.domain.repository;

import com.backend.directory.domain.Directory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    @Query("SELECT d FROM Directory d LEFT JOIN FETCH d.files")
    List<Directory> findAllDirectories();
}
