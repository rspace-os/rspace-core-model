package com.researchspace.model.stoichiometry;

import com.researchspace.model.inventory.InventoryRecordConnectedEntity;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.ConstraintViolationException;

import com.researchspace.model.inventory.Sample;
import com.researchspace.model.units.QuantityInfo;
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
    if (getInventoryRecord() instanceof Sample){
      Sample sample = (Sample) getInventoryRecord();
      if(sample.isTemplate()){
        throw new ConstraintViolationException("Cannot link stoichiometry to sample template", null);
      }
    }
  }
}

