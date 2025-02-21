package com.researchspace.model.comms;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.researchspace.model.Group;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Extension of MessageOrRequest to store information about requests for 
 * joining the group (REQUEST_JOIN_LAB_GROUP or REQUEST_JOIN_EXISTING_COLLAB_GROUP).
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMessageOrRequest extends MessageOrRequest implements Serializable {

	private static final long serialVersionUID = 2026986015989808191L;

	private Group group;

	public GroupMessageOrRequest(MessageType type) {
		super(type);
	}

	@OneToOne
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}
