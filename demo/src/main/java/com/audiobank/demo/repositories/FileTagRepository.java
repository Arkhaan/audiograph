package com.audiobank.demo.repositories;

import com.audiobank.demo.models.FileTag;
import com.audiobank.demo.models.FileTagCompositePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileTagRepository extends JpaRepository<FileTag, FileTagCompositePK>{
    
}
