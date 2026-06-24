package com.researchspace.model.stoichiometry;

import com.researchspace.model.inventory.InventoryRecord;
import com.researchspace.model.inventory.InventoryRecordConnectedEntity;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Access(AccessType.PROPERTY)
@Setter
@NoArgsConstructor
@Audited
public class StoichiometryInventoryLink extends InventoryRecordConnectedEntity {

  private Long id;
  private StoichiometryMolecule stoichiometryMolecule;
  private boolean stockDeducted = false;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  @OneToOne
  @JoinColumn(name = "stoichiometry_molecule_id", nullable = false)
  public StoichiometryMolecule getStoichiometryMolecule() {
    return stoichiometryMolecule;
  }

  @Column(name = "stock_deducted", nullable = false)
  public boolean isStockDeducted() {
    return stockDeducted;
  }

  @Override
  public void validateBeforeSave() {
    super.validateBeforeSave();
    // isSampleTemplate() rather than instanceof: a lazily-loaded reference is a proxy typed to
    // the abstract root (never the concrete subclass), but virtual dispatch reaches the real type
    InventoryRecord invRec = getInventoryRecord();
    if (invRec != null && invRec.isSampleTemplate()) {
      throw new ConstraintViolationException("Cannot link stoichiometry to sample template", null);
    }
  }
}

