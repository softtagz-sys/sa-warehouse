# Reflectie Software Architecture

## V1

### Pre-Coaching

#### Geschatte Progress: 20%

#### Status
- De start ging zoals verwacht wat moeizaam. Terug in Java duiken en een nieuwe techstack implementeren/onder de knie krijgen, had wat tijd nodig.
- N-tier architecture is wel onder de knie.
- Analyse van projecten/implementaties verliep vlot.
- 75% opzet van Land/Water is klaar --> gemakkelijker om verder uit te breiden en Business Logica te implementeren.

#### Stories
Geïmplementeerd:
- Land: 1, 2, 3, 4, 5
  - Refinement is nog verder nodig na verdiepende vragen bij coach.

#### Quality
Momenteel staan er nog geen concrete issues voor refinment op het programma. We proberen vanuit de eerste iteratie al zo SOLID te werken.

#### Vragen

- Technische vragen:
  - Hoe config values laden in een custom validator: uit de debug van jpaRepository.save blijkt dat de @Value niet ingeladen wordt, een @Autowire van de Config klasse wordt ook niet ingeladen
  - vervolg op de custom validation: de repo.save gooit een transactionSystemException als deze niet klopt —> catchen? 
    - Welke validation tool voor custom validation gebruiken bij jpa --> jakarta/hibernate?
- Business Logica:
  - hoe moet een niet geplande entry de gegevens doorgeven? 3 scenarios (gepland op tijd —> gegevens gekend, gepland niet op tijd —> gegevens gekend.. maar welke gegevens?, niet gepland —> gegevens doorgeven)


### Post-Coaching

