package com.researchspace.model.stoichiometry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.researchspace.model.RSChemElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "StoichiometryMolecule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "rsChemElement"})
@ToString(exclude = "stoichiometry")
@Audited
public class StoichiometryMolecule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "stoichiometry_id", nullable = false)
  @JsonBackReference
  private Stoichiometry stoichiometry;

  @OneToOne
  @JoinColumn(name = "rs_chem_id", nullable = false)
  private RSChemElement rsChemElement;

  @OneToOne(mappedBy = "stoichiometryMolecule")
  private StoichiometryInventoryLink inventoryLink;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MoleculeRole role;

  @Column private String formula;

  @Column private String name;

  @Column
  @Type(type = "text")
  private String smiles;

  @Builder.Default @Column private Double coefficient = 1.00;

  @Column(name = "molecular_weight")
  private Double molecularWeight;

  @Column private Double mass;

  @Column(name = "actual_amount")
  private Double actualAmount;

  @Column(name = "actual_yield")
  private Double actualYield;

  @Column(name = "limiting_reagent")
  private Boolean limitingReagent;

  @Column
  @Type(type = "text")
  private String notes;
}

