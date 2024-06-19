package com.researchspace.model.comms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.researchspace.model.Group;
import com.researchspace.model.User;

public class CommsTestUtils {
	

	public static MessageOrRequest createARequest(User originator) throws ParseException {
		return createRequestOfType(originator, MessageType.REQUEST_JOIN_EXISTING_COLLAB_GROUP);
	}
	
	public static MessageOrRequest createSimpleMessage(User originator) {
		return createRequestOfType(originator, MessageType.SIMPLE_MESSAGE);
		
	}
	
	public static MessageOrRequest createRequestOfType(User originator, MessageType type) {
		RequestFactory rf = createARequestFactory();
		MsgOrReqstCreationCfg config = new MsgOrReqstCreationCfg();
		config.setMessageType(type);
		MessageOrRequest message=rf.createMessageOrRequestObject(config,
				null,null,originator);
		
		message.setLatest(true);
		message.setMessage("Please look at line 250");
	
	//	message.setRequestedCompletionDate(getAFutureDate());
		return message;
	}

	private static RequestFactory createARequestFactory() {
		RequestFactory rf = new RequestFactory();
		return rf;
	}
	
	public static GroupMessageOrRequest createAGroupRequest(User originator, Group grp) throws ParseException {
		RequestFactory rf =  createARequestFactory();
		MsgOrReqstCreationCfg config = new MsgOrReqstCreationCfg();
		config.setMessageType(MessageType.REQUEST_JOIN_EXISTING_COLLAB_GROUP);
		
		GroupMessageOrRequest message=(GroupMessageOrRequest)rf.createMessageOrRequestObject(config,
				null,grp,originator);
		
		message.setLatest(true);
		message.setMessage("Please join the group");
	
	
	//	message.setRequestedCompletionDate(getAFutureDate());
		return (GroupMessageOrRequest)message;
	}
	
	static Date getAFutureDate() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse("2025-01-01");
	}
	
	public static Notification createAnyNotification(User originator) {
		
		Notification message = new Notification();
		message.setMessage("A notification");
		message.setOriginator(originator);
		message.setPriority(CommunicationPriority.URGENT);
		message.setNotificationType(NotificationType.NOTIFICATION_DOCUMENT_EDITED);
		return message;
	}
}
