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
  private QuantityInfo quantity;

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

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "unitId", column = @Column(name = "unit_id")),
          @AttributeOverride(name = "numericValue", column = @Column(name = "quantity_used", precision = 19, scale = 3))
  })
  public QuantityInfo getQuantity() {
    return quantity;
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

