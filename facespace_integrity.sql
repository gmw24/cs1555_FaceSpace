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