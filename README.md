**You need to create application.properties in resources with the following properties:**

spring.datasource.url=\<database URL\>

spring.datasource.username=\<database login\>

spring.datasource.password=\<database password\>

files.path=/audio/

files.fullpath=\<absolutepath\>/audio/

spring.mail.host=\<ex: smtp.gmail.com\>

spring.mail.port=\<ex: 465\>

spring.mail.username=\<mail username\>

spring.mail.password=\<mail password\>

spring.mail.properties.mail.smtp.auth=true

spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory

spring.mail.properties.mail.smtp.socketFactory.fallback=false


**Database schema:**

    create database audiograph;

    create table audiograph.audiofiles (
        file_id int auto_increment primary key,
        file_name varchar(90),
        `date` DATE NOT NULL,
        uploader varchar(45) not null,
        title varchar(45),
        `description` varchar(256)
        );
    
    create table audiograph.users (
        id int auto_increment primary key,
        email varchar(45),
        first_name varchar(45) not null,
        last_name varchar(45) not null,
        join_date DATE,
        `password` varchar(45),
        apikey varchar(45)
        );
    
    create table audiograph.tags (
        id int auto_increment primary key,
        `value` varchar(45)
    );

    INSERT INTO `audiograph`.`tags` (`id`, `value`) VALUES ('1', 'Non catégorisé');

    create table audiograph.filetags (
        id_file int,
        id_tag int,
        primary key(id_file, id_tag)
    );

    create table audiograph.fileusers (
        id_file int,
        id_user int,
        primary key(id_file, id_user)
    );

**Notes**

    mvn clean package spring-boot:repackage
    java -jar target/spring-boot-ops.war
