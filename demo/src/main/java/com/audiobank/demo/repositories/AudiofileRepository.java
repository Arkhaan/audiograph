package com.audiobank.demo.repositories;

import com.audiobank.demo.models.Audiofile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AudiofileRepository extends JpaRepository<Audiofile, Long> {

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags FROM (SELECT DISTINCT file_id, file_name, date, uploader, description, title, CONCAT(first_name, ' ', last_name) AS full_name, value FROM audiobank.fileusers as fileusers INNER JOIN filetags ON fileusers.id_file = filetags.id_file INNER JOIN audiofile ON fileusers.id_file = audiofile.file_id INNER JOIN user ON fileusers.id_user = user.id INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    List<Audiofile> findAll();

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, "
        + "GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, "
        + "GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags "
        + "FROM (SELECT DISTINCT audiofile.file_id, file_name, date, uploader, description, title, CONCAT(first_name, ' ', last_name) AS full_name, value "
        + "FROM (SELECT * from audiobank.audiofile "
            + "INNER JOIN filetags ON audiofile.file_id = filetags.id_file "
            + "WHERE filetags.id_tag = :tag) as audiofile "
        + "INNER JOIN filetags ON audiofile.file_id = filetags.id_file "
        + "INNER JOIN fileusers ON fileusers.id_file = audiofile.file_id "
        + "INNER JOIN user ON fileusers.id_user = user.id "
        + "INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    List<Audiofile> findAllByTagID(@Param("tag")Long tag);

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, "
        + "GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, "
        + "GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags "
        + "FROM (SELECT DISTINCT audiofile.file_id, file_name, date, uploader, description, title, CONCAT(first_name, ' ', last_name) AS full_name, value "
        + "FROM (SELECT * from audiobank.audiofile "
            + "INNER JOIN fileusers ON audiofile.file_id = fileusers.id_file "
            + "WHERE fileusers.id_user = :user) as audiofile "
        + "INNER JOIN filetags ON audiofile.file_id = filetags.id_file "
        + "INNER JOIN fileusers ON fileusers.id_file = audiofile.file_id "
        + "INNER JOIN user ON fileusers.id_user = user.id "
        + "INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    List<Audiofile> findAllByUserID(@Param("user")Long user);

}