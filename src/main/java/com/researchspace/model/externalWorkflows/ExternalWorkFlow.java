package com.researchspace.model.externalWorkflows;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Getter
@EqualsAndHashCode(of = { "extId" })
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ExternalWorkFlow {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  /**
   * The ID provided by the external service for this workflow.
   */
  @NotNull
  private String extId;
  @NotNull
  private String name;
  private String description;

  @OneToMany(mappedBy = "externalWorkFlow")
  @Setter
  private Set<ExternalWorkFlowInvocation> externalWorkflowInvocations = new HashSet<>();
  @Builder()
  public ExternalWorkFlow( @NonNull String extId,  @NonNull String name,  @NonNull String description) {
    this.extId = extId;
    this.name = name;
    this.description = description;
  }
  protected ExternalWorkFlow() {
  }
}
