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

alter table expenses add column comment varchar(255);

-- Extract categories to its own table
create table categories (
    id              serial primary key,
    name            varchar(50)     not null,
    description     varchar(255)
);

alter table expenses add column category_id integer;

INSERT INTO categories (name)
SELECT DISTINCT category
FROM expenses;

update expenses e
set category_id = (select id from categories c where c.name = e.category);

alter table expenses alter column category_id set not null;
alter table expenses drop column category;
-- End

-- Create budget structure
alter table expenses alter column comment type varchar(255);

create table budget_categories (
	id			    serial          primary key,
	category_id		serial4     not null,
	budget_month          numeric(6,0)    not null,
	amount 		    numeric(6, 2) 	not null
);
-- End

alter table categories
add column default_amount numeric(6, 2);
