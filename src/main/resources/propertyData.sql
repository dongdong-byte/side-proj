

use onbid_db;

CREATE TABLE `properties` (
                              `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                              `cltr_no` VARCHAR(50) NOT NULL COMMENT '물건번호 (api)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `cltr_mnmt_no` VARCHAR(100) NULL DEFAULT NULL COMMENT '물건 관리번호 (하면 표시용)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `plnm_no` VARCHAR(50) NULL DEFAULT NULL COMMENT '공고번호 ' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `name` VARCHAR(500) NOT NULL COMMENT '물건명' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `goods_description` TEXT NULL DEFAULT NULL COMMENT '물건정보(GOODS_NM)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `category` VARCHAR(100) NULL DEFAULT NULL COMMENT '물건분류(CTGR_FULL_NM)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `location` VARCHAR(255) NULL DEFAULT NULL COMMENT '지번 주소(LDNM_ADRS)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `road_address` VARCHAR(255) NULL DEFAULT NULL COMMENT '도로명주소(NMRD_ADRS)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `pnu` VARCHAR(50) NULL DEFAULT NULL COMMENT '토지 고유 번호(LDNM_PNU)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `appraisal_price` DECIMAL(15,2) NULL DEFAULT NULL COMMENT '감정가격(APSL_ASES_AVG_AMT)',
                              `min_bid_price` DECIMAL(15,2) NULL DEFAULT NULL COMMENT '최저입찰가(MIN_BID_PRC)',
                              `current_price` DECIMAL(15,2) NULL DEFAULT NULL COMMENT '현재가',
                              `bid_method` VARCHAR(100) NULL DEFAULT NULL COMMENT '입찰방법(BID_MTD_NM)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `auction_start_time` DATETIME NULL DEFAULT NULL COMMENT '입찰 시작일시(PBCT_BEGN_DTM)',
                              `auction_end_time` DATETIME NULL DEFAULT NULL COMMENT '입찰 종료일시(PBCT_CLS_DTM)',
                              `pbct_status` VARCHAR(50) NULL DEFAULT NULL COMMENT '물건상태 (PBCT_CLTR_STAT_NM)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `sale_type` VARCHAR(20) NULL DEFAULT 'AUCTION' COMMENT '판매 방식(AUCTION/DIRECT)' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `status` VARCHAR(20) NULL DEFAULT 'AVAILABLE' COMMENT '시스템 상태' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `view_count` INT(11) NULL DEFAULT '0' COMMENT '조회수(IQRY_CNT)',
                              `bid_count` INT(11) NULL DEFAULT '0' COMMENT '입찰수',
                              `image_url` VARCHAR(500) NULL DEFAULT '/images/default-product.jpg' COLLATE 'utf8mb4_uca1400_ai_ci',
                              `created_at` DATETIME NULL DEFAULT current_timestamp(),
                              `updated_at` DATETIME NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                              `synced_at` DATETIME NULL DEFAULT NULL COMMENT 'api 마지막 동기화 시간',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `cltr_no` (`cltr_no`) USING BTREE,
                              INDEX `idx_cltr_no` (`cltr_no`) USING BTREE,
                              INDEX `idx_name` (`name`(100)) USING BTREE,
                              INDEX `idx_status` (`status`, `auction_end_time`) USING BTREE,
                              INDEX `idx_location` (`location`(100)) USING BTREE,
                              INDEX `idx_price` (`min_bid_price`) USING BTREE,
                              INDEX `idx_auction_time` (`auction_start_time`, `auction_end_time`) USING BTREE
)
    COMMENT='물건정보 마스터 테이블'
    COLLATE='utf8mb4_uca1400_ai_ci'
    ENGINE=InnoDB
;

CREATE TABLE `properties_bids_history` (
                                           `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                                           `property_id` BIGINT(20) NOT NULL COMMENT '물건 id',
                                           `cltr_no` VARCHAR(50) NOT NULL COMMENT '물건번호' COLLATE 'utf8mb4_uca1400_ai_ci',
                                           `pbct_no` VARCHAR(50) NOT NULL COMMENT '공매번호(회차별 고유)' COLLATE 'utf8mb4_uca1400_ai_ci',
                                           `pbct_seq` VARCHAR(10) NULL DEFAULT NULL COMMENT '공매 차수(PBCT_SEQ)' COLLATE 'utf8mb4_uca1400_ai_ci',
                                           `pbct_dgr` VARCHAR(10) NULL DEFAULT NULL COMMENT '공매 회차 (PBCT_DGR)' COLLATE 'utf8mb4_uca1400_ai_ci',
                                           `min_bid_price` DECIMAL(15,2) NULL DEFAULT NULL COMMENT '최저입찰가(MIN_BID_PRC)',
                                           `appraisal_price` DECIMAL(15,2) NULL DEFAULT NULL COMMENT '감정가격(APSL_ASES_AVG_AMT)',
                                           `fee_rate` VARCHAR(20) NULL DEFAULT NULL COMMENT '수수료율' COLLATE 'utf8mb4_uca1400_ai_ci',
                                           `auction_start_time` DATETIME NULL DEFAULT NULL COMMENT '입찰 시작일시(PBCT_BEGN_DTM)',
                                           `auction_end_time` DATETIME NULL DEFAULT NULL COMMENT '입찰 종료일시(PBCT_CLS_DTM)',
                                           `auction_result_time` DATETIME NULL DEFAULT NULL COMMENT '개찰일시(PBCT_EXCT_DTM)',
                                           `deposit_rate` DECIMAL(5,2) NULL DEFAULT NULL COMMENT '입찰 보증금 율 (TDPS_RT)',
                                           `bid_status` VARCHAR(50) NULL DEFAULT 'PENDING' COMMENT '입찰상태(PENDING/ONGOING/COMPLETED/FAILED)' COLLATE 'utf8mb4_uca1400_ai_ci',
                                           `result_status` VARCHAR(50) NULL DEFAULT NULL COMMENT '낙찰상태(SUCCESS/FAIL/CANCEL)' COLLATE 'utf8mb4_uca1400_ai_ci',
                                           `created_at` DATETIME NULL DEFAULT current_timestamp(),
                                           `updated_at` DATETIME NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE INDEX `uk_property_pbct` (`property_id`, `pbct_no`) USING BTREE,
                                           INDEX `idx_cltr_no` (`cltr_no`) USING BTREE,
                                           INDEX `idx_auction_time` (`auction_start_time`, `auction_end_time`) USING BTREE,
                                           INDEX `idx_bid_status` (`bid_status`) USING BTREE,
                                           CONSTRAINT `properties_bids_history_ibfk_1` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`) ON UPDATE RESTRICT ON DELETE CASCADE
)
    COMMENT='물건회차별 입찰정보'
    COLLATE='utf8mb4_uca1400_ai_ci'
    ENGINE=InnoDB
;
