--facespace schema, group project cs1555,milestone 1

DROP TABLE Profiles CASCADE CONSTRAINTS;
DROP TABLE Friendships CASCADE CONSTRAINTS;
DROP TABLE Groups CASCADE CONSTRAINTS;
DROP TABLE Messages CASCADE CONSTRAINTS;
DROP TABLE Members CASCADE CONSTRAINTS;
DROP TABLE Recipients CASCADE CONSTRAINTS;

CREATE TABLE Profiles 
(	userId number(10) PRIMARY KEY,
	fname	varchar2(32),
	lname	varchar2(32),
	email 	varchar2(32),
	dobDay 	number(2),
	dobMonth	number(2),
	dobYear 	number(4),
	lastLogin TIMESTAMP
);

CREATE TABLE Groups
(	groupId number(10) PRIMARY KEY,
	name varchar2(32),
	description varchar2(100),
	membershipLimit number(10)
);

CREATE TABLE Friendships
(
	friendshipId number(10) PRIMARY KEY,
	senderId number(10),
	receiverId number(10),
	approved number(1),
	dateEstablished TIMESTAMP DEFAULT NULL,
	CONSTRAINT fSender_FK FOREIGN KEY (senderId) REFERENCES Profiles(userId),
	CONSTRAINT fReceiver_FK FOREIGN KEY (receiverId) REFERENCES Profiles(userId)
);

CREATE TABLE Members
(
	groupId number(10),
	userId number(10),
	CONSTRAINT group_FK FOREIGN KEY (groupId) REFERENCES Groups(groupId),
	CONSTRAINT userM_FK FOREIGN KEY (userId) REFERENCES Profiles(userId)
);

CREATE TABLE Messages
(
	messageId number(10) PRIMARY KEY,
	senderId number(10),
	subject varchar2(32),
	messageText varchar2(140),
	dateSent TIMESTAMP,
	groupId number(10),
	CONSTRAINT msgSender_FK FOREIGN KEY (senderId) REFERENCES Profiles(userId)
);

CREATE TABLE Recipients
(
	messageId number(10),
	userId number(10),
	CONSTRAINT message_FK FOREIGN KEY (messageId) REFERENCES Messages(messageId),
	CONSTRAINT userR_FK FOREIGN KEY (userId) REFERENCES Profiles(userId)
);