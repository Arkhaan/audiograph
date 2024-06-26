package com.audiobank.demo.repositories;

import com.audiobank.demo.models.Audiofile;
import com.audiobank.demo.models.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AudiofileRepository extends JpaRepository<Audiofile, Long> {

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, file_format, GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags FROM (SELECT DISTINCT file_id, file_name, date, uploader, description, title, file_format, CONCAT(first_name, ' ', last_name) AS full_name, value FROM audiograph.fileusers as fileusers INNER JOIN filetags ON fileusers.id_file = filetags.id_file INNER JOIN audiofiles ON fileusers.id_file = audiofiles.file_id INNER JOIN users ON fileusers.id_user = users.id INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    List<Audiofile> findAll();

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, file_format, "
        + "GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, "
        + "GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags "
        + "FROM (SELECT DISTINCT audiofiles.file_id, file_name, date, uploader, description, title, file_format, CONCAT(first_name, ' ', last_name) AS full_name, value "
        + "FROM (SELECT * from audiograph.audiofiles "
            + "INNER JOIN filetags ON audiofiles.file_id = filetags.id_file "
            + "WHERE filetags.id_tag = :tag) as audiofiles "
        + "INNER JOIN filetags ON audiofiles.file_id = filetags.id_file "
        + "INNER JOIN fileusers ON fileusers.id_file = audiofiles.file_id "
        + "INNER JOIN users ON fileusers.id_user = users.id "
        + "INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    List<Audiofile> findAllByTagID(@Param("tag")Long tag);

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, file_format, "
        + "GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, "
        + "GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags "
        + "FROM (SELECT DISTINCT audiofiles.file_id, file_name, date, uploader, description, title, file_format, CONCAT(first_name, ' ', last_name) AS full_name, value "
        + "FROM (SELECT * from audiograph.audiofiles "
            + "INNER JOIN fileusers ON audiofiles.file_id = fileusers.id_file "
            + "WHERE fileusers.id_user = :user) as audiofiles "
        + "INNER JOIN filetags ON audiofiles.file_id = filetags.id_file "
        + "INNER JOIN fileusers ON fileusers.id_file = audiofiles.file_id "
        + "INNER JOIN users ON fileusers.id_user = users.id "
        + "INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    List<Audiofile> findAllByUserID(@Param("user")Long user);

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, file_format, "
        + "GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, "
        + "GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags "
        + "FROM (SELECT DISTINCT audiofiles.file_id, file_name, date, uploader, description, title, file_format, CONCAT(first_name, ' ', last_name) AS full_name, value "
        + "FROM (SELECT * from audiograph.audiofiles as audiofiles "
            + "WHERE uploader = :uploader) as audiofiles "
        + "INNER JOIN filetags ON audiofiles.file_id = filetags.id_file "
        + "INNER JOIN fileusers ON fileusers.id_file = audiofiles.file_id "
        + "INNER JOIN users ON fileusers.id_user = users.id "
        + "INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    List<Audiofile> findAllByUploaderID(@Param("uploader")Long uploader);

    @Query(value="SELECT file_id, file_name, date, uploader, description, title, file_format, "
    + "GROUP_CONCAT( DISTINCT full_name SEPARATOR ', ') as full_names, "
    + "GROUP_CONCAT( DISTINCT value SEPARATOR ', ' ) as tags "
    + "FROM (SELECT DISTINCT audiofiles.file_id, file_name, date, uploader, description, title, file_format, CONCAT(first_name, ' ', last_name) AS full_name, value "
    + "FROM (SELECT * from audiograph.audiofiles "
        + "INNER JOIN filetags ON audiofiles.file_id = filetags.id_file "
        + "WHERE audiofiles.file_id = :fileID) as audiofiles "
    + "INNER JOIN filetags ON audiofiles.file_id = filetags.id_file "
    + "INNER JOIN fileusers ON fileusers.id_file = audiofiles.file_id "
    + "INNER JOIN users ON fileusers.id_user = users.id "
    + "INNER JOIN tags ON filetags.id_tag = tags.id) as concat group by file_id", nativeQuery = true)
    Optional<Audiofile> findByID(@Param("fileID")Long fileID);

    @Transactional
    @Modifying
    @Query(value="DELETE FROM Audiofile a WHERE a.file_id=:file_id and a.uploader=:uploader")
    void deleteFile(@Param("file_id") Long fileID, @Param("uploader") Long uploader);

    @Query(value="select file_name from audiograph.audiofiles where audiofiles.file_id=:file_id", nativeQuery = true)
    String getFileName(@Param("file_id") Long fileID);

    @Query(value="select file_format from audiograph.audiofiles where audiofiles.file_id=:file_id", nativeQuery = true)
    String getExtension(@Param("file_id") Long fileID);

    @Query(value="select title from audiograph.audiofiles where audiofiles.file_id=:file_id", nativeQuery = true)
    String getTitle(@Param("file_id") Long fileID);

    @Query(value="select description from audiograph.audiofiles where audiofiles.file_id=:file_id", nativeQuery = true)
    String getDescription(@Param("file_id") Long fileID);

    @Query(value="SELECT CONCAT(first_name, ' ', last_name) as full_names from audiograph.audiofiles "
                + "INNER JOIN fileusers ON fileusers.id_file = audiofiles.file_id "
                + "INNER JOIN users ON fileusers.id_user = users.id "
                + "WHERE audiofiles.file_id=:file_id", nativeQuery = true)
    List<String> getNames(@Param("file_id") Long fileID);

    @Query(value="SELECT value as tags from audiograph.audiofiles "
                + "INNER JOIN filetags ON filetags.id_file = audiofiles.file_id "
                + "INNER JOIN tags ON filetags.id_tag = tags.id "
                + "WHERE audiofiles.file_id=:file_id", nativeQuery = true)
    List<String> getTags(@Param("file_id") Long fileID);

}