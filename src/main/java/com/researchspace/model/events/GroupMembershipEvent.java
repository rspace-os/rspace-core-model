package com.researchspace.model.events;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.researchspace.model.Group;
import com.researchspace.model.User;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Event log of Group membership - related actions. This is a table rather than in the audit log
 *  as this information will be queried more frequently (in group and profile pages), will
 *   be quite low-volume and therefore will be more efficient than parsing log files.
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = { "user", "timestamp", "groupEventType", "group" })
@ToString
public class GroupMembershipEvent implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -9065064321226583664L;

	/*
	 * Package scoped for testing
	 */
	GroupMembershipEvent(Long id, User user, Group group, GroupEventType groupEventType, Date timestamp) {
		super();
		this.id = id;
		this.user = user;
		this.timestamp = timestamp;
		this.groupEventType = groupEventType;
		this.group = group;
	}

	/**
	 * Public constructor sets timestamp internally
	 */
	public GroupMembershipEvent(@NotNull User user, @NotNull Group group, @NotNull GroupEventType groupEventType) {
		this(null, user, group, groupEventType, new Date());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private Long id;

	@ManyToOne(optional = false)
	@Getter
	@Setter
	@NotNull
	private User user;
	
	@ManyToOne(optional = false)
	@Getter
	@Setter
	@NotNull
	private Group group;

	@CreationTimestamp()
	@Getter
	@Setter(AccessLevel.PRIVATE) // for hibernate
	@Column(nullable=false)
	@NotNull
	private Date timestamp;

	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	@Column(nullable=false, length=30)
	@NotNull
	private GroupEventType groupEventType;

}
