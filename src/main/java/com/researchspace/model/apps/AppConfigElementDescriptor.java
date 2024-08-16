package com.researchspace.model.apps;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.researchspace.model.PropertyDescriptor;
import com.researchspace.model.preference.SettingsType;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
@Setter
@EqualsAndHashCode(of={"descriptor"})
@ToString
@NoArgsConstructor
public class AppConfigElementDescriptor implements Serializable {

	private static final long serialVersionUID = 8510816500531846867L;
	
	@ManyToOne
	private PropertyDescriptor descriptor;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(value=AccessLevel.PACKAGE)
	private Long id;
	
	@ManyToOne
	private App app;

	public AppConfigElementDescriptor(PropertyDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public void validate(String value) {
		if (getDescriptor() != null) { 
			SettingsType.validate(getDescriptor().getType(), value);
		}
	}

}
