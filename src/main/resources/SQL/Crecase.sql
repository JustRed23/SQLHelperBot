IF EXISTS (SELECT * FROM sysobjects WHERE name='Schalen' and xtype='U')
	DROP TABLE Schalen;
GO

IF EXISTS (SELECT * FROM sysobjects WHERE name='Inschrijvingen' and xtype='U')
	DROP TABLE Inschrijvingen;
GO

IF EXISTS (SELECT * FROM sysobjects WHERE name='Uitvoeringen' and xtype='U')
	DROP TABLE Uitvoeringen;
GO

IF EXISTS (SELECT * FROM sysobjects WHERE name='Cursussen' and xtype='U')
	DROP TABLE Cursussen;
GO

IF EXISTS (SELECT * FROM sysobjects WHERE name='Afdelingen' and xtype='U')
	DROP TABLE Afdelingen;
GO

IF EXISTS (SELECT * FROM sysobjects WHERE name='Medewerkers' and xtype='U')
	DROP TABLE Medewerkers;
GO


IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Medewerkers' and xtype='U')
	CREATE TABLE Medewerkers
	(
		Mnr INT CONSTRAINT M_PK PRIMARY KEY,
		Naam VARCHAR(15) CONSTRAINT M_NAAM_NN NOT NULL,
		Voorn VARCHAR(12) CONSTRAINT M_VOORN_NN NOT NULL,
		Functie VARCHAR(10),
		Chef INT,
		Gbdatum DATE CONSTRAINT M_GBDATUM_NN NOT NULL,
		Maandsal DECIMAL(6, 2) CONSTRAINT M_MAANDSAL_NN NOT NULL,
		Comm DECIMAL(6, 2),
		Afd INT DEFAULT 10,
		CONSTRAINT M_CHEF_FK FOREIGN KEY (Chef) REFERENCES Medewerkers(Mnr),
		CONSTRAINT M_VERK_CHK CHECK((CASE Functie WHEN 'VERKOPER' THEN 0 ELSE 1 END + CASE Comm WHEN NULL THEN 0 ELSE 1 END) = 1)
	)
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Afdelingen' and xtype='U')
	CREATE TABLE Afdelingen
	(
		Anr INT CONSTRAINT A_PK PRIMARY KEY CONSTRAINT A_ANR_CHK CHECK((Anr % 10) = 0 ),
		Naam VARCHAR(15) 
			CONSTRAINT A_NAAM_NN NOT NULL 
			CONSTRAINT A_NAAM_UN UNIQUE 
			CONSTRAINT A_NAAM_CHK CHECK(Naam = UPPER(Naam)),
		Locatie VARCHAR(20) 
			CONSTRAINT A_LOC_NN NOT NULL
            CONSTRAINT A_LOC_CHK CHECK(Locatie = UPPER(Locatie)),
		Hoofd INT, 
		CONSTRAINT A_HOOFD_FK FOREIGN KEY (Hoofd) REFERENCES Medewerkers(Mnr),
	)
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Schalen' and xtype='U')
	CREATE TABLE Schalen
	(
		Snr INT CONSTRAINT S_PK PRIMARY KEY,
		Ondergrens DECIMAL(6, 2)
			CONSTRAINT S_ONDER_NN NOT NULL
            CONSTRAINT S_ONDER_CHK  CHECK(Ondergrens >= 0),
		Bovengrens DECIMAL(6, 2) CONSTRAINT S_BOVEN_NN NOT NULL,
		Toelage DECIMAL(6, 2) CONSTRAINT S_TOELG_NN NOT NULL,
		CONSTRAINT S_OND_BOV CHECK(Ondergrens <= Bovengrens)
	)
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Cursussen' and xtype='U')
	CREATE TABLE Cursussen
	(
		Code VARCHAR(4) CONSTRAINT C_PK PRIMARY KEY,
		Omschrijving VARCHAR(50) CONSTRAINT C_OMSCHR_NN NOT NULL,
		Type CHAR(3) CONSTRAINT C_TYPE_NN NOT NULL,
		Lengte INT CONSTRAINT C_LENGTE_NN NOT NULL,
		CONSTRAINT C_CODE_CHK CHECK(Code = UPPER(code)),
		CONSTRAINT C_TYPE_CHK CHECK(Type IN ('ALG', 'BLD', 'DSG'))
	)
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Uitvoeringen' and xtype='U')
	CREATE TABLE Uitvoeringen
	(
		Cursus VARCHAR(4)
			CONSTRAINT U_CURSUS_NN NOT NULL
            CONSTRAINT U_CURSUS_FK REFERENCES Cursussen,
		Begindatum DATE CONSTRAINT U_BEGIN_NN NOT NULL,
		Docent INT CONSTRAINT U_DOCENT_FK REFERENCES Medewerkers,
		Locatie VARCHAR(20),
		CONSTRAINT U_PK PRIMARY KEY (Cursus, Begindatum)
	)
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Inschrijvingen' and xtype='U')
	CREATE TABLE Inschrijvingen
	(
		Cursist INT 
			CONSTRAINT I_CURSIST_NN NOT NULL
			CONSTRAINT I_CURSIST_FK REFERENCES Medewerkers,
		Cursus VARCHAR(4) CONSTRAINT  I_CURSUS_NN NOT NULL,
		Begindatum DATE CONSTRAINT I_BEGIN_NN NOT NULL,
		Evaluatie INT CONSTRAINT I_EVAL_CHK CHECK(Evaluatie IN (1, 2, 3, 4, 5)),
		CONSTRAINT I_PK PRIMARY KEY (Cursist, Cursus, Begindatum),
		CONSTRAINT I_UITV_FK FOREIGN KEY (Cursus, Begindatum) REFERENCES Uitvoeringen
	)
GO