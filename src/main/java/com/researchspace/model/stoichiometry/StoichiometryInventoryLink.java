package com.researchspace.model.stoichiometry;

import com.researchspace.model.inventory.InventoryRecordConnectedEntity;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.ConstraintViolationException;

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

