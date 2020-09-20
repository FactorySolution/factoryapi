CREATE TABLE PESSOA (
	ID BIGINT AUTO_INCREMENT,
	CPF VARCHAR(20) NOT NULL,
	DATA_NASCIMENTO DATETIME NOT NULL,
	NOME VARCHAR(100) NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE CONTATO (
	ID BIGINT AUTO_INCREMENT,
	EMAIL VARCHAR(60) NOT NULL,
	NOME VARCHAR(100) NOT NULL,
	TELEFONE VARCHAR(20) NOT NULL,
	ID_PESSOA BIGINT NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_contato_pessoa foreign key (ID_PESSOA) references PESSOA (id)
);