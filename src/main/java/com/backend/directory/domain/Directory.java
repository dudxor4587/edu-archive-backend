package com.backend.directory.domain;

import com.backend.file.domain.File;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "directory")
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long directoryId;

    private String name;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "directoryId")
    private Directory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<Directory> children = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private List<File> files = new ArrayList<>();

    @Builder
    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }
}
