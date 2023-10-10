EXEC sp_msforeachtable "ALTER TABLE ? NOCHECK CONSTRAINT ALL";
INSERT INTO Medewerkers VALUES (7369, 'CASPERS', 'JANA', 'TRAINER', 7902, '1985-12-17', 1800, NULL, 20);
INSERT INTO Medewerkers VALUES (7499, 'ALLARD', 'NELE', 'VERKOPER', 7698, '1981-02-20', 1600, 3000, 30);
INSERT INTO Medewerkers VALUES (7521, 'DEFOUR', 'THOMAS', 'VERKOPER', 7698, '1982-02-22', 2250, 5000, 30);
INSERT INTO Medewerkers VALUES (7566, 'JACOBS', 'EMMA', 'MANAGER', 7839, '1987-04-02', 4975, NULL, 20);
INSERT INTO Medewerkers VALUES (7654, 'MARTENS', 'RAF', 'VERKOPER', 7698, '1976-09-28', 2250, 3400, 30);
INSERT INTO Medewerkers VALUES (7698, 'BRIERS', 'ANDREA', 'MANAGER', 7839, '1983-11-01', 5850, NULL, 30);
INSERT INTO Medewerkers VALUES (7782, 'CLERCKX', 'AN', 'MANAGER', 7839, '1985-06-09', 3450, NULL, 10);
INSERT INTO Medewerkers VALUES (7788, 'SWINNEN', 'CHRIS', 'TRAINER',7566, '1979-11-26', 4000, NULL, 20);
INSERT INTO Medewerkers VALUES (7839, 'DE KONING', 'LIEVE', 'DIRECTEUR', NULL, '1972-11-17', 7000, NULL, 10);
INSERT INTO Medewerkers VALUES (7844, 'DEN RUYTER', 'JOACHIM', 'VERKOPER', 7698, '1988-09-28', 2500, 0, 30);
INSERT INTO Medewerkers VALUES (7876, 'SLECHTEN', 'TOM', 'TRAINER', 7788, '1986-11-30', 2700, NULL, 20);
INSERT INTO Medewerkers VALUES (7900, 'JACOBS', 'SIMON', 'BOEKHOUDER', 7698, '1989-11-03', 2800, NULL, 30);
INSERT INTO Medewerkers VALUES (7902, 'DE COOMAN', 'DORIEN', 'TRAINER', 7566, '1979-02-13', 4000, NULL, 20);
INSERT INTO Medewerkers VALUES (7934, 'WOUTERS', 'SVEN', 'BOEKHOUDER', 7782, '1982-01-23', 2300, NULL, 10);

INSERT INTO Afdelingen VALUES (10, 'HOOFDKANTOOR', 'MAASMECHELEN', 7782);
INSERT INTO Afdelingen VALUES (20, 'OPLEIDINGEN', 'HASSELT' ,7566);
INSERT INTO Afdelingen VALUES (30, 'VERKOOP', 'GENK', 7698);
INSERT INTO Afdelingen VALUES (40, 'PERSONEELSZAKEN', 'LEUVEN', 7839);

INSERT INTO Schalen VALUES (1, 1700, 2200,  0);
INSERT INTO Schalen VALUES (2, 2201, 2400, 50);
INSERT INTO Schalen VALUES (3, 2401, 4000, 100);
INSERT INTO Schalen VALUES (4, 4001, 5000, 200);
INSERT INTO Schalen VALUES (5, 5001, 9999, 500);

INSERT INTO Cursussen VALUES ('SQL', 'Introductie SQL en databanken', 'ALG', 4);
INSERT INTO Cursussen VALUES ('ORG', 'IT Organisatie', 'ALG', 1);
INSERT INTO Cursussen VALUES ('WEB', 'Ontwikkeling website', 'BLD', 4);
INSERT INTO Cursussen VALUES ('CIS', 'Cisco CCNA', 'BLD', 1);
INSERT INTO Cursussen VALUES ('WIN', 'Windows Server', 'BLD', 2);
INSERT INTO Cursussen VALUES ('LIN', 'Linux OS', 'DSG', 3);
INSERT INTO Cursussen VALUES ('PR2', 'Programmeren2 in Visual C#', 'DSG', 1);
INSERT INTO Cursussen VALUES ('WBA', 'Web applicaties', 'DSG', 2);
INSERT INTO Cursussen VALUES ('PR1', 'Programmeren1 Visual C#', 'DSG', 5);
INSERT INTO Cursussen VALUES ('NET', 'Netwerkbeheer', 'DSG',4);

INSERT INTO Uitvoeringen VALUES ('SQL', '2015-04-16', 7902, 'HASSELT');
INSERT INTO Uitvoeringen VALUES ('SQL', '2015-10-08', 7369, 'MAASEIK');
INSERT INTO Uitvoeringen VALUES ('SQL', '2015-12-17', 7369, 'HASSELT');
INSERT INTO Uitvoeringen VALUES ('ORG', '2015-08-10', 7566, 'GENK');
INSERT INTO Uitvoeringen VALUES ('ORG', '2015-09-27', 7902, 'HASSELT');
INSERT INTO Uitvoeringen VALUES ('WEB', '2015-12-17', 7566, 'MAASEIK');
INSERT INTO Uitvoeringen VALUES ('WEB', '2016-02-05', 7876, 'HASSELT');
INSERT INTO Uitvoeringen VALUES ('CIS', '2016-09-11', 7788, 'HASSELT');
INSERT INTO Uitvoeringen VALUES ('WIN', '2016-02-04', 7369, 'HASSELT');
INSERT INTO Uitvoeringen VALUES ('WIN', '2016-09-18', NULL, 'MAASEIK');
INSERT INTO Uitvoeringen VALUES ('LIN', '2017-01-13', NULL, NULL);
INSERT INTO Uitvoeringen VALUES ('PR1', '2017-02-17', NULL, 'HASSELT');
INSERT INTO Uitvoeringen VALUES ('WBA', '2017-02-24', 7788, 'GENK');

INSERT INTO Inschrijvingen VALUES (7499, 'SQL', '2015-04-16', 4);
INSERT INTO Inschrijvingen VALUES (7934, 'SQL', '2015-04-16', 5);
INSERT INTO Inschrijvingen VALUES (7698, 'SQL', '2015-04-16', 4);
INSERT INTO Inschrijvingen VALUES (7876, 'SQL', '2015-04-16', 2);
INSERT INTO Inschrijvingen VALUES (7788, 'SQL', '2015-10-08', NULL);
INSERT INTO Inschrijvingen VALUES (7839, 'SQL', '2015-10-08', 3);
INSERT INTO Inschrijvingen VALUES (7902, 'SQL', '2015-10-08', 4);
INSERT INTO Inschrijvingen VALUES (7902, 'SQL', '2015-12-17', NULL);
INSERT INTO Inschrijvingen VALUES (7698, 'SQL', '2015-12-17', NULL);
INSERT INTO Inschrijvingen VALUES (7521, 'ORG', '2015-08-10', 4);
INSERT INTO Inschrijvingen VALUES (7900, 'ORG', '2015-08-10', 4);
INSERT INTO Inschrijvingen VALUES (7902, 'ORG', '2015-08-10', 5);
INSERT INTO Inschrijvingen VALUES (7844, 'ORG', '2016-09-27', 5);
INSERT INTO Inschrijvingen VALUES (7499, 'WEB', '2015-12-17', 2);
INSERT INTO Inschrijvingen VALUES (7782, 'WEB', '2015-12-17', 5);
INSERT INTO Inschrijvingen VALUES (7876, 'WEB', '2015-12-17', 5);
INSERT INTO Inschrijvingen VALUES (7788, 'WEB', '2015-12-17', 5);
INSERT INTO Inschrijvingen VALUES (7839, 'WEB', '2015-12-17', 4);
INSERT INTO Inschrijvingen VALUES (7566, 'WEB', '2016-02-05', 3);
INSERT INTO Inschrijvingen VALUES (7788, 'WEB', '2016-02-05', 4);
INSERT INTO Inschrijvingen VALUES (7698, 'WEB', '2016-02-05', 5);
INSERT INTO Inschrijvingen VALUES (7900, 'WIN', '2016-02-04', 4);
INSERT INTO Inschrijvingen VALUES (7499, 'WIN', '2016-02-04', 5);
INSERT INTO Inschrijvingen VALUES (7566, 'CIS', '2016-09-11', NULL);
INSERT INTO Inschrijvingen VALUES (7499, 'CIS', '2016-09-11', NULL);
INSERT INTO Inschrijvingen VALUES (7876, 'CIS', '2016-09-11', NULL);

EXEC sp_msforeachtable "ALTER TABLE ? CHECK CONSTRAINT ALL";