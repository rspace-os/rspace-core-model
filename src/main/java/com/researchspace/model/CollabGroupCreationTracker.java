package com.researchspace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

import com.researchspace.model.comms.MessageOrRequest;

/**
 * Stores information about the progress of CollaborationGroup creation -
 * invitations, replies and a reference to the group itself.
 *
 */
@Entity
public class CollabGroupCreationTracker {

	private Long id;

	private MessageOrRequest mor;

	private Group group;

	private Short numInvitations = 0;

	private Short numReplies = 0;

	private String initialGrpName;

	public String getInitialGrpName() {
		return initialGrpName;
	}

	public void setInitialGrpName(String initialGrpName) {
		this.initialGrpName = initialGrpName;
	}

	public Short getNumInvitations() {
		return numInvitations;
	}

	public void setNumInvitations(Short numInvitations) {
		this.numInvitations = numInvitations;
	}

	public void incrementReplies() {
		this.numReplies = (short) (numReplies + 1);
	}

	public Short getNumReplies() {
		return numReplies;
	}

	void setNumReplies(Short numReplies) {
		this.numReplies = numReplies;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(optional = false)
	public MessageOrRequest getMor() {
		return mor;
	}

	public void setMor(MessageOrRequest mor) {
		this.mor = mor;
	}

	@OneToOne
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Transient
	public boolean allReplied() {
		return this.numReplies >= this.numInvitations;
	}

}
