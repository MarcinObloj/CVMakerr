CvMaker
=======
CvMaker to aplikacja webowa, która umożliwia użytkownikom tworzenie profesjonalnych CV i pobieranie ich w formacie PDF. Projekt składa się z frontendu (HTML, SCSS, JavaScript) oraz backendu (Spring Boot, PostgreSQL).


Funkcje
=======
Tworzenie CV: Użytkownik może wprowadzić swoje dane, takie jak imię, nazwisko, doświadczenie zawodowe, umiejętności itp.

Podgląd na żywo: Podgląd CV w czasie rzeczywistym podczas wprowadzania danych.

Pobieranie PDF: Możliwość pobrania CV w formacie PDF.

Responsywny design: Aplikacja działa na różnych urządzeniach (komputery, tablety, telefony).

Technologie
=======

Frontend
--------
<div style="display: flex; gap: 10px; align-items: center;"> <img src="https://img.icons8.com/color/48/000000/html-5.png" alt="HTML" title="HTML"/> <img src="https://img.icons8.com/color/48/000000/css3.png" alt="CSS" title="CSS"/> <img src="https://img.icons8.com/color/48/000000/sass.png" alt="SCSS" title="SCSS"/> <img src="https://img.icons8.com/color/48/000000/javascript.png" alt="JavaScript" title="JavaScript"/> </div>

Backend
--------
<div style="display: flex; gap: 10px; align-items: center;"> <img src="https://img.icons8.com/color/48/000000/java-coffee-cup-logo.png" alt="Java" title="Java"/> <img src="https://img.icons8.com/color/48/000000/spring-logo.png" alt="Spring Boot" title="Spring Boot"/> <img src="https://img.icons8.com/color/48/000000/postgreesql.png" alt="PostgreSQL" title="PostgreSQL"/> </div>

Jak uruchomić projekt lokalnie
=========
Wymagania wstępne
---------------
Java 17 (lub nowsza)

Node.js (opcjonalnie, do zarządzania zależnościami frontendu)

PostgreSQL (lub inna baza danych obsługiwana przez Spring Boot)

Maven (do zarządzania zależnościami backendu)

Backend (Spring Boot)
----------
Sklonuj repozytorium:


https://github.com/MarcinObloj/CVMakerr.git
Przejdź do folderu backendu:


cd CvMaker/backend
Skonfiguruj bazę danych:

Utwórz bazę danych PostgreSQL o nazwie cvmaker.

Zaktualizuj plik application.properties w folderze src/main/resources z danymi do połączenia z bazą danych:


spring.datasource.url=jdbc:postgresql://localhost:5432/cvmaker
spring.datasource.username=twoj-uzytkownik
spring.datasource.password=twoje-haslo
Uruchom aplikację Spring Boot:


mvn spring-boot:run
Frontend
-------
Przejdź do folderu frontendu:

cd CvMaker/frontend
Uruchom Live Server:
Otwórz plik index.html w przeglądarce lub użyj rozszerzenia Live Server w VS Code.
