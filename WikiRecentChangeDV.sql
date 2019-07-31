/**
 * В качестве источника данных используется поток Server-Sent Events из Wikimedia, который содержит данные об изменениях страниц всех проектов 
 * Wikimedia. 
 * Данные из SSE потока захватываются с помощью сервиса на Java Spring Boot + WebFlux (https://github.com/sagaranin/wiki-sse-listener) 
 * и записываются в топик Kafka в формате JSON.
 * 
 * SSE URL: https://stream.wikimedia.org/v2/stream/recentchange
 * JSON Schema: https://github.com/wikimedia/mediawiki-event-schemas/tree/master/jsonschema/mediawiki/recentchange
 */

-- создаем схему OTUS
CREATE SCHEMA IF NOT EXISTS otus;

-- Создаем stage-таблицу для импорта событий из Kafka
CREATE FLEX TABLE IF NOT EXISTS otus.wiki_recentchange_stg();

-- Заполняем stage-таблицу новыми данными из топика Kafka
TRUNCATE TABLE otus.wiki_recentchange_stg;
COPY otus.wiki_recentchange_stg SOURCE KafkaSource(
	stream='wikistream|0|-3,wikistream|1|-3,wikistream|2|-3,wikistream|3|-3,wikistream|4|-3,wikistream|5|-3,wikistream|6|-3,wikistream|7|-3', -- 8 partitions, читаем с последнего оффсета
	brokers='server.larnerweb.ru:9092',
	stop_on_eof=TRUE)
PARSER KafkaJSONParser(flatten_maps=TRUE, key_separator='_')
DIRECT;


-- Создаем ODS таблицу
-- DROP table otus.wiki_recentchange_ods;
-- TRUNCATE table otus.wiki_recentchange_ods;
CREATE table if NOT EXISTS otus.wiki_recentchange_ods (
	meta_id 			VARCHAR(36) NOT NULL, 
	meta_uri			VARCHAR(1000), 
	meta_request_id		VARCHAR(36), 
	meta_dt				TIMESTAMP NOT NULL, 
	meta_domain			VARCHAR(255), 
	meta_stream			VARCHAR(255), 
	id 					NUMBER, 
	c_type 				VARCHAR(20), 
	title 				VARCHAR(1000), 
	namespace 			NUMBER, 
	comment 			VARCHAR(10000), 
	parsedcomment		VARCHAR(10000),
	ts					TIMESTAMP,
	username			VARCHAR(255),
	bot					BOOLEAN,
	server_url			VARCHAR(255),
	server_name			VARCHAR(255),
	server_script_path	VARCHAR(255),
	wiki				VARCHAR(255),
	minor				BOOLEAN,
	patrolled			BOOLEAN,
	length_old			NUMBER,
	length_new			NUMBER,
	revision_old		NUMBER,
	revision_new		NUMBER,
	PRIMARY KEY (meta_id) ENABLED
)
ORDER BY meta_dt, meta_domain
SEGMENTED BY hash(meta_id) ALL NODES
PARTITION BY meta_dt::DATE GROUP BY CALENDAR_HIERARCHY_DAY(meta_dt::DATE, 1,2)
;


-- Заполняем ODS таблицу новыми данными из stage
INSERT into otus.wiki_recentchange_ods  
SELECT DISTINCT
	meta_id				::varchar, 
	meta_uri			::varchar, 
	meta_request_id		::varchar, 
	meta_dt				::timestamp, 
	meta_domain			::varchar, 
	meta_stream			::varchar, 
	id					::number, 
	type				::varchar, 
	title				::varchar, 
	namespace			::number, 
	comment				::varchar, 
	parsedcomment		::varchar,
	"timestamp"			::timestamp,
	"user"				::varchar,
	bot					::boolean,
	server_url			::varchar,
	server_name			::varchar,
	server_script_path	::varchar,
	wiki				::varchar,
	minor				::boolean,
	patrolled			::boolean,
	length_old			::number,
	length_new			::number,
	revision_old		::number,
	revision_new		::number
FROM wiki_recentchange_stg 
	WHERE meta_id NOT IN (SELECT meta_id FROM wiki_recentchange_ods); -- исключаем повторное добавление имеющихся данных

-- Создаем DDS HUB таблицу
-- DROP table otus.hub_recentchange;
CREATE TABLE IF NOT EXISTS otus.hub_recentchange(
	hk_meta_id NUMBER NOT NULL, 	-- hash(business_key)
	load_ts TIMESTAMP NOT NULL, 	-- load time
	meta_id VARCHAR(36) NOT NULL, 	-- business_key
	PRIMARY KEY (hk_meta_id) ENABLED
)
ORDER BY hk_meta_id
SEGMENTED BY hk_meta_id ALL NODES
;

-- заполняем HUB 
INSERT into otus.hub_recentchange (
	hk_meta_id, 
	load_ts, 
	meta_id
) SELECT 
	hash(meta_id) as hk_meta_id,
	now() as load_ts,
	meta_id
FROM wiki_recentchange_ods
	WHERE hash(meta_id) NOT IN (SELECT hk_meta_id FROM hub_recentchange);


-- Создаем DDS SAT таблицу
-- DROP table otus.sat_recentchange_props;
CREATE TABLE IF NOT EXISTS otus.sat_recentchange_props (
	--
	hk_meta_id 			NUMBER NOT NULL, 		-- hash(business_key)
	load_ts 			TIMESTAMP NOT NULL, 	-- load time
	hashdiff 			NUMBER NOT NULL,		-- hash([props])
	--
	meta_dt				TIMESTAMP NOT NULL, 
	meta_domain			VARCHAR(255), 
	c_type 				VARCHAR(20), 
	title 				VARCHAR(1000), 
	namespace 			NUMBER, 
	username			VARCHAR(255),
	bot					BOOLEAN,
	server_name			VARCHAR(255),
	wiki				VARCHAR(255),
	minor				BOOLEAN,
	patrolled			BOOLEAN,
	length_old			NUMBER,
	length_new			NUMBER,
	PRIMARY KEY (hk_meta_id, hashdiff) ENABLED
)
ORDER BY hk_meta_id
SEGMENTED BY hk_meta_id ALL NODES
;

INSERT INTO otus.sat_recentchange_props
SELECT 
	--
	hash(src.meta_id) hk_meta_id,
	now() load_ts,
	hash(src.meta_dt, src.meta_domain, src.c_type, src.title, src.namespace, src.username,src.bot,src.server_name,src.wiki,src.minor,src.patrolled,src.length_old,src.length_new) hashdiff,
	--
	src.meta_dt, 
	src.meta_domain,  
	src.c_type, 
	src.title, 
	src.namespace, 
	src.username,
	src.bot,
	src.server_name,
	src.wiki,
	src.minor,
	src.patrolled,
	src.length_old,
	src.length_new
FROM otus.wiki_recentchange_ods src 
	LEFT JOIN otus.sat_recentchange_props tgt ON tgt.hk_meta_id = hash(src.meta_id)
		AND tgt.hashdiff = hash(src.meta_dt, src.meta_domain, src.c_type, src.title, src.namespace, src.username,src.bot,src.server_name,src.wiki,src.minor,src.patrolled,src.length_old,src.length_new)
			WHERE tgt.hk_meta_id IS NULL;
			
			
-- RecentChange View
CREATE OR REPLACE VIEW otus.v_recentchange AS 
SELECT 
	hub.meta_id,
	sat.load_ts,
	sat.meta_dt, 
	sat.meta_domain,  
	sat.c_type, 
	sat.title, 
	sat.namespace, 
	sat.username,
	sat.bot,
	sat.server_name,
	sat.wiki,
	sat.minor,
	sat.patrolled,
	sat.length_old,
	sat.length_new
FROM hub_recentchange hub 
	JOIN sat_recentchange_props sat ON hub.hk_meta_id = sat.hk_meta_id
LIMIT 1 OVER (PARTITION BY hub.meta_id ORDER BY sat.load_ts DESC);


-- Marts

-- Most active users
SELECT 
	username,
	server_name,
	sum(CASE WHEN bot THEN 1 ELSE 0 END) as_bot,
	sum(CASE WHEN NOT bot THEN 1 ELSE 0 END) as_not_bot,
	count(1) total
FROM v_recentchange
GROUP by username, server_name
ORDER BY total DESC

-- Most changed articles in ru.wikipedia.org
SELECT 
	title,
	server_name,
	count(1)
FROM v_recentchange
	WHERE wiki = 'ruwiki'
GROUP BY 1,2
ORDER BY count(1) DESC;

