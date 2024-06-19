package com.researchspace.model.dmps;

import com.researchspace.model.EcatDocumentFile;
import com.researchspace.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

/**
 * Join table for User to DMP, storing date of import.
 */
@Entity
@Data
@NoArgsConstructor
public class DMPUser {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private Instant timestamp;

    @ManyToOne
    private EcatDocumentFile dmpDownloadPdf;

    /**
     * A DMP identifier for an imported DMP, usually a DOI
     */
    private String dmpId;
    private String title;

    public DMPUser(User user, DMP dmp) {
        this.user = user;
        this.dmpId = dmp.getDmpId();
        this.title = dmp.getTitle();
        this.timestamp = Instant.now();
    }
}
