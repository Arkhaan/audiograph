package com.audiobank.demo.repositories;

import com.audiobank.demo.models.FileTag;
import com.audiobank.demo.models.FileTagCompositePK;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FileTagRepository extends JpaRepository<FileTag, FileTagCompositePK>{
 
    @Transactional
    @Modifying
    @Query(value="DELETE FROM FileTag ft WHERE ft.pk.id_file=:file_id")
    void deleteEntry(@Param("file_id") Long fileID);
}
