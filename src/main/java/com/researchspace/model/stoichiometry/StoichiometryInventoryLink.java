package com.researchspace.model.stoichiometry;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Access(AccessType.PROPERTY)
@Setter
@NoArgsConstructor
public class StoichiometryInventoryLink extends InventoryRecordConnectedEntity {

  private Long id;
  private StoichiometryMolecule stoichiometryMolecule;
  private String unit;
  private Double quantityUsed;

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

  @Column(length = 32)
  public String getUnit() {
    return unit;
  }

  @Column(name = "quantity_used")
  public Double getQuantityUsed() {
    return quantityUsed;
  }
}

