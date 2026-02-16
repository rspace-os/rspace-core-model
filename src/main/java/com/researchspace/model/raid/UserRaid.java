package com.researchspace.model.raid;

import com.researchspace.model.Group;
import com.researchspace.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = {"raidServerAlias", "raidIdentifier"})
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(
    columnNames = {"raid_server_alias", "raid_natural_key"}))
@ToString(of = {"raidServerAlias", "raidIdentifier"})
public class UserRaid {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "raid_natural_key")
  private String raidIdentifier;

  @Column(name = "raid_title")
  private String raidTitle;

  @Column(name = "raid_server_alias")
  private String raidServerAlias;

  @ManyToOne
  @JoinColumn(name = "owner_id", referencedColumnName = "id")
  private User owner;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "raid")
  private Group groupAssociated;

  public UserRaid(User owner, Group groupAssociated, String raidServerAlias, String raidTitle,
      String raidIdentifier) {
    this.owner = owner;
    this.groupAssociated = groupAssociated;
    this.raidServerAlias = raidServerAlias;
    this.raidTitle = raidTitle;
    this.raidIdentifier = raidIdentifier;
  }

}
