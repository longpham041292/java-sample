CREATE PROCEDURE `init_data`()
BEGIN
	declare gold_uuid varchar(50);
    declare plantium_uuid varchar(50);
    declare diamond_uuid varchar(50);
    declare vi_lang char(2);
    declare en_lang char(2);
    
    set vi_lang = 'vi';
    set en_lang = 'en';
    set gold_uuid = uuid();
    set plantium_uuid = uuid();
    set diamond_uuid = uuid();
    
    INSERT INTO `level` (`uuid`,`code`) VALUES (gold_uuid, 'GOLD');
	INSERT INTO `level` (`uuid`,`code`) VALUES (plantium_uuid, 'PLATINUM');
	INSERT INTO `level` (`uuid`,`code`) VALUES (diamond_uuid, 'DIAMOND'); 
    
    INSERT INTO data_localized(`entity_uuid`,`lang_code`,`localized_key`,`localized_value`) 
		VALUES (gold_uuid, en_lang, 'code', 'Gold'); 
	INSERT INTO data_localized(`entity_uuid`,`lang_code`,`localized_key`,`localized_value`) 
		VALUES (plantium_uuid, en_lang, 'code', 'Platinum');
    INSERT INTO data_localized(`entity_uuid`,`lang_code`,`localized_key`,`localized_value`) 
		VALUES (diamond_uuid, en_lang, 'code', 'Diamond'); 
	
	INSERT INTO data_localized(`entity_uuid`,`lang_code`,`localized_key`,`localized_value`) 
		VALUES (gold_uuid, vi_lang, 'code', 'Vàng'); 
	INSERT INTO data_localized(`entity_uuid`,`lang_code`,`localized_key`,`localized_value`) 
		VALUES (plantium_uuid, vi_lang, 'code', 'Bạch kim');
    INSERT INTO data_localized(`entity_uuid`,`lang_code`,`localized_key`,`localized_value`) 
		VALUES (diamond_uuid, vi_lang, 'code', 'Kim cương'); 	
	
END$$

call init_data()$$