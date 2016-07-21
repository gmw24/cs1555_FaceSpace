--checks membership limits before adding member to group
CREATE OR REPLACE TRIGGER MembershipCheck
BEFORE INSERT ON Members
REFERENCING NEW AS newRow
FOR EACH ROW
declare
	cnt integer := 0;
	memLimit integer := 0;
BEGIN
	SELECT COUNT(*) INTO cnt FROM Members WHERE groupId = :newRow.groupId;
	SELECT membershipLimit INTO memLimit FROM Groups WHERE groupId = :newRow.groupId;
	IF cnt >= memLimit THEN 
		raise_application_error( -20000, 'Membership limit reached!');
	END IF;
END;
/

--when user is deleted, it removes all their friendships and group memberships, as well as all the messages they have received
--it also sets the the senderId of all the messages they sent to be null
--after this is triggered, a check must be done if a message's sender is null and it has no recipients in recipients table. this is done in jdbc
CREATE OR REPLACE TRIGGER RemoveFromGroup
BEFORE DELETE ON Profiles
REFERENCING OLD AS oldRow
FOR EACH ROW
BEGIN
	DELETE FROM Friendships WHERE senderId = :oldRow.userId;
	DELETE FROM Friendships WHERE receiverId = :oldRow.userId;
	DELETE FROM Members WHERE userId = :oldRow.userId; -- remove user from all groups
	UPDATE Messages SET senderId = NULL WHERE senderId = :oldRow.userId; -- set all the messages they send to a null sender
	DELETE FROM Recipients WHERE userId = :oldRow.userId; -- delete all the messages they received
END;
/
