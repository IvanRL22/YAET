-- Contains scripts for recreating current DB structure

create table expenses (
	id			serial primary key,
	category 	varchar(25) 	not null,
	payee		varchar(50),
	amount 		numeric(6, 2) 	not null,
	date		date			not null
);

create table incomes (
	id			serial primary key,
	payer		varchar2(50),
	amount 		numeric(6, 2) 	not null,
	date		date			not null
);