--facespace schema, group project cs1555, milestone 1

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
	senderId number(10) FOREIGN KEY REFERENCES Profiles(userId),
	receiverId number(10) FOREIGN KEY REFERENCES Profiles(userId),
	approved number(1),
	dateEstablished TIMESTAMP DEFAULT NULL
);

CREATE TABLE Members
(
	groupId number(10) FOREIGN KEY REFERENCES Groups(groupId),
	userId number(10) FOREIGN KEY REFERENCES Profiles(userId)
);

CREATE TABLE Messages
(
	messageId number(10) PRIMARY KEY,
	senderId number(10) FOREIGN KEY REFERENCES Profiles(userId),
	subject varchar2(32),
	messageText varchar2(140),
	dateSent TIMESTAMP,
	groupId number(10)
);

CREATE TABLE Recipients
(
	messageId number(10) FOREIGN KEY REFERENCES Messages(messageId),
	userId number(10) FOREIGN KEY REFERENCES Profiles(userId)
);