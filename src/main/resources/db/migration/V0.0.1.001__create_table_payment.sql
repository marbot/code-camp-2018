create table T_PAYMENT
(
	ID NUMBER(18) not null,
	UUID VARCHAR2(50) not null,
	IID VARCHAR2(5) not null,
	BIC VARCHAR2(11) not null,
	AMOUNT NUMBER(20,2) not null,
	SUBMISSION_STAMP TIMESTAMP(6),
	PAYMENT_STATE VARCHAR2(50 char) not null,
	USE_CASE VARCHAR2(50 char) not null,
	MESSAGE CLOB not null,
	ENCRYPTED_MESSAGE CLOB not null,
	constraint UK_PAYMENT_SICDAYID_UUID unique (UUID)
)

