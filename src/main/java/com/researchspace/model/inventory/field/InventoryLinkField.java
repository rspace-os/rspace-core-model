package com.researchspace.model.inventory.field;

import com.researchspace.model.field.FieldType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * A structured Sample template field of type 'link'. It holds a single optional {@link
 * InventoryLink} value (one target + relation + optional version pin) and a pipe-delimited whitelist
 * of permitted DataCite relation types. A null/empty whitelist means all relation types are allowed.
 *
 * <p>Distinct from {@link ExtraLinkField}, which is the record-level extra-field link; this is part
 * of the {@link InventoryEntityField} single-table hierarchy used to define template fields.
 */
@Entity
@Audited
@DiscriminatorValue("link")
@Setter
public class InventoryLinkField extends InventoryEntityField {

  private static final long serialVersionUID = 1L;

  private InventoryLink link;
  private String allowedRelationTypes;

  public InventoryLinkField() {
    super(FieldType.LINK, "");
  }

  /*
   * Annotations are placed on the getter (not the field) because the InventoryEntityField hierarchy
   * uses PROPERTY access (@Id is on getId()). With field-level annotations Hibernate ignores
   * @JoinColumn and falls back to the property name as the column name. Mirrors ExtraLinkField.
   */
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "link_id", unique = true)
  public InventoryLink getLink() {
    return link;
  }

  /**
   * Pipe-delimited whitelist of permitted DataCite relation types; null or empty means all relation
   * types are allowed.
   */
  @Column(name = "allowed_relation_types", length = 1024)
  public String getAllowedRelationTypes() {
    return allowedRelationTypes;
  }

  /** A mandatory link field requires a populated link target, not data-column content. */
  @Override
  @Transient
  public boolean isValidValueForMandatoryField(String fieldData) {
    return link != null && link.getTargetGlobalId() != null;
  }

  @Override
  public InventoryLinkField shallowCopy() {
    InventoryLinkField copy = new InventoryLinkField();
    copy.setAllowedRelationTypes(getAllowedRelationTypes());
    if (link != null) {
      copy.setLink(copyLink(link));
    }
    copyFields(copy);
    return copy;
  }

  /**
   * Deep-copies the link into a brand-new unsaved row (no id, timestamps set on persist), so a
   * sample instantiated from a template never shares the template field's link row.
   */
  private static InventoryLink copyLink(InventoryLink src) {
    InventoryLink copy = new InventoryLink();
    copy.setTargetGlobalId(src.getTargetGlobalId());
    copy.setTargetPrefix(src.getTargetPrefix());
    copy.setTargetDbId(src.getTargetDbId());
    copy.setVersionPin(src.getVersionPin());
    copy.setTargetRevisionId(src.getTargetRevisionId());
    copy.setRelationType(src.getRelationType());
    copy.setDeleted(src.isDeleted());
    return copy;
  }
}
