drop database if exists STOREDB;
create database STOREDB;
use STOREDB;

create table CUSTOMERS (
  ID bigint not null auto_increment primary key,
  NAME varchar(16) not null,
  AGE INT,
  ADDRESS varchar(255)
);

create table ORDERS (
  ID bigint not null auto_increment primary key,
  ORDER_NUMBER varchar(16) not null,
  PRICE FLOAT,
  CUSTOMER_ID bigint,
  foreign key(CUSTOMER_ID) references CUSTOMERS(ID)
);

insert into CUSTOMERS(ID,NAME,AGE,ADDRESS) values(1, 'С��',23, '����');
insert into CUSTOMERS(ID,NAME,AGE,ADDRESS) values(2,'С��',29, '���');
insert into CUSTOMERS(ID,NAME,AGE,ADDRESS) values(3,'С��',33, 'ɽ��');

insert into ORDERS(ID,ORDER_NUMBER,PRICE,CUSTOMER_ID) values(1, 'С��_001',100.12, 1);
insert into ORDERS(ID,ORDER_NUMBER,PRICE,CUSTOMER_ID) values(2, 'С��_002',200.32, 1);
insert into ORDERS(ID,ORDER_NUMBER,PRICE,CUSTOMER_ID) values(3, 'С��_001',88.44, 2);