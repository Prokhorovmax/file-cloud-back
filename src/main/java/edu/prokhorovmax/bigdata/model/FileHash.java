package edu.prokhorovmax.bigdata.model;


import javax.persistence.*;
import java.util.Objects;

@Entity()
@Table(name = "filehash")
public class FileHash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file", referencedColumnName = "id")
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hash", referencedColumnName = "hash")
    private Hash hash;

    @Column(name = "number")
    private int number;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Hash getHash() {
        return hash;
    }

    public void setHash(Hash hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FileHash)) {
            return false;
        }
        FileHash anotherInstance = (FileHash) obj;
        return this.id == anotherInstance.id &&
                this.number == anotherInstance.number &&
                Objects.equals(this.hash, anotherInstance.hash) &&
                Objects.equals(this.file, anotherInstance.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hash, file, number);
    }

    @Override
    public String toString() {
        return "FileHash{" +
                "id=" + id +
                ", file=" + file.toString() +
                ", hash=" + hash.toString() +
                ", number=" + number +
                "}";

    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
