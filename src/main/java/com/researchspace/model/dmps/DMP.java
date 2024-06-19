package com.researchspace.model.dmps;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * An imported DMP. Not an entity, just a collection of DMP attributes we want to store in RSpace
 */
@Data
@NoArgsConstructor
public class DMP {

    /**
     * A DMP identifier for an imported DMP, usually a DOI
     */
    private String dmpId;
    private String title;

    public DMP (String dmpId, String title) {
        this.dmpId = dmpId;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DMP dmp = (DMP) o;
        return Objects.equals(dmpId, dmp.dmpId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dmpId);
    }
}
