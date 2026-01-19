package com.researchspace.model.stoichiometry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.researchspace.model.RSChemElement;
import com.researchspace.model.record.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "Stoichiometry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "molecules")
@Audited
public class Stoichiometry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "parent_reaction_id", nullable = true)
  private RSChemElement parentReaction;

  @ManyToOne
  @JoinColumn(name = "record_id", nullable = false)
  private Record record;

  @Builder.Default
  @OneToMany(
          mappedBy = "stoichiometry",
          cascade = CascadeType.ALL,
          fetch = javax.persistence.FetchType.EAGER)
  private List<StoichiometryMolecule> molecules = new ArrayList<>();

  @Column(name = "last_modified")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  public void addMolecule(StoichiometryMolecule molecule) {
    molecules.add(molecule);
    molecule.setStoichiometry(this);
  }

  @PrePersist
  @PreUpdate
  private void updateTimestamp() {
    this.lastModified = new Date();
  }

  /*
  Update the lastModified timestamp for audit purposes when a child StoichiometryMolecule is updated
   */
  public void touchForAudit() {
    this.lastModified = new Date();
  }
}
