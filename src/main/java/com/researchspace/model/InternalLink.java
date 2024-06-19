package com.researchspace.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.researchspace.model.record.BaseRecord;
import com.researchspace.model.record.Record;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity behind internal links between RSpace documents.
 */
@Entity
@Data
@EqualsAndHashCode(of = { "target", "source" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InternalLink implements Serializable {

	private static final long serialVersionUID = 1234321L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Setter(AccessLevel.PACKAGE)
	private Long id;

	/** source document, the one containing the link */
	@ManyToOne
	private Record source;

	/** target document pointed by the link */
	@ManyToOne	
	private BaseRecord target;

	public InternalLink(Record source, BaseRecord target) {
		this.source = source;
		this.target = target;
	}

	
}
