package com.audiobank.demo.repositories;

import com.audiobank.demo.models.FileUser;
import com.audiobank.demo.models.FileUserCompositePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUserRepository extends JpaRepository<FileUser, FileUserCompositePK>{
    
}
