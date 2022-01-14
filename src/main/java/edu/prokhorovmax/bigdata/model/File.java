package edu.prokhorovmax.bigdata.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "date")
    private LocalDateTime date;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof File)) {
            return false;
        }
        File anotherInstance = (File) obj;
        return this.id == anotherInstance.id &&
                Objects.equals(this.fileName, anotherInstance.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName);
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", fileName=" + '\'' + fileName + '\'' +
                ", date=" + '\'' + date + '\'' +
                "}";

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
