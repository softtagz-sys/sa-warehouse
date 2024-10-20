# Reflectie Software Architecture

## V1

### Pre-Coaching

#### Geschatte Progress: 20%

#### Status
- De start ging zoals verwacht wat moeizaam. Terug in Java duiken en een nieuwe techstack implementeren/onder de knie krijgen, had wat tijd nodig.
- N-tier architecture is wel onder de knie.
- Analyse van projecten/implementaties verliep vlot.
- 75% opzet van Land/Water is klaar --> gemakkelijker om verder uit te breiden en Business Logica te implementeren.
- Enkele integratie testen waar nog enkele problemen in zitten

#### Stories
Geïmplementeerd:
- Land: 1, 2, 3, 4, 5
- Water 19, 20, 21
-> Refinement is nog verder nodig na verdiepende vragen bij coach.


#### Quality
Momenteel staan er nog geen concrete issues voor refinment op het programma. We proberen vanuit de eerste iteratie al zo SOLID te werken.

#### Vragen

- Technische vragen:
  - Hoe config values laden in een custom validator: uit de debug van jpaRepository.save blijkt dat de @Value niet ingeladen wordt, een @Autowire van de Config klasse wordt ook niet ingeladen
  - vervolg op de custom validation: de repo.save gooit een transactionSystemException als deze niet klopt —> catchen? 
    - Welke validation tool voor custom validation gebruiken bij jpa --> jakarta/hibernate?
- Business Logica:
  - hoe moet een niet geplande entry de gegevens doorgeven? 3 scenarios (gepland op tijd —> gegevens gekend, gepland niet op tijd —> gegevens gekend.. maar welke gegevens?, niet gepland —> gegevens doorgeven)
  - hoe moet de inspectie operatie uitgevoert worden? momenteel is er een REST API die je kan aanspreken on een succes door te geven, maar er staat in de opdracht dat een inspectie standaard succesvol is maar je moet wel de niet succesvolle kunnen ophalen. Wat moeten we hier juist implementeren?


### Post-Coaching

## V2

### Pre-Coaching

#### Geschatte Progress: 75%

#### Status
- Alle technologiëen verwerkt in huidige code
- Land: 95%
  - Paar zaken nog finetunen (validatie op messaging via rabbit, error pagina toevoegen)
  - Document herlezen om te zien of alle vereisten verwerkt zijn
  - Eventueel strategiëen toepassen
  - Extra logging voorzien voor diagnostische fouten
  - Controller advice

- Warehouse: 35%%
  
- Water: 

#### Stories
Geïmplementeerd:
  - Land: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 23
  - Warehouse: 11, 12, 14, 15
  - Water

#### Quality
Eerst na feedbacksessie de comments verwerken, vervolgens willen we tegen het einde van week 6 klaar zijn met alle requirements.
Hierna zullen we eventueel een refactor toevoegen om bv. de slot-strategy toe te passen.

#### Vragen
Create schema via hibernate? 