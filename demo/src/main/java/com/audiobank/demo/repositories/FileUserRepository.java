package com.audiobank.demo.repositories;

import com.audiobank.demo.models.FileUser;
import com.audiobank.demo.models.FileUserCompositePK;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FileUserRepository extends JpaRepository<FileUser, FileUserCompositePK>{
    
    @Transactional
    @Modifying
    @Query(value="DELETE FROM FileUser ft WHERE ft.pk.id_file=:file_id")
    void deleteEntry(@Param("file_id") Long fileID);
}
