# 📚 Guia de l'Estudiant: Validació de Formularis amb HTML5 i Servlets (MVC)

Aquest projecte serveix com a base per aprendre a implementar sistemes de registre segurs i robustos, utilitzant les eines modernes de validació de HTML5 al costat del client i Jakarta EE al costat del servidor.

> **Important:** Per evitar problemes quan treballeu amb fitxers JSP, instal·leu l'extensió **JSP Language Support** de **Samuel Weinhardt** a Visual Studio Code.

## 🧩 1. JSTL i Expression Language (EL)

Aquest projecte utilitza **Expression Language** i **JSTL** a les JSP per separar millor la vista de la lògica Java i fer les pàgines més llegibles.

*   **Expression Language** (`${...}`) s'utilitza per accedir a les propietats dels beans que el servlet envia a la vista.
    * Exemple a `Register.jsp`: `value="${user.name}"` posa el valor del nom d'usuari a l'input.
    * Exemple a `Login.jsp`: `L'usuari <strong>${user.name}</strong>` mostra el nom de l'usuari registrat.
*   **JSTL** (`<c:...>`) s'utilitza per iterar i construir contingut dinàmic de forma segura en el JSP.
    * `Register.jsp` fa servir:
      ```jsp
      <%@ taglib prefix="c" uri="jakarta.tags.core"%>
      <script>
          const serverErrors = {
              <c:forEach var="error" items="${errors}">
                  "${error.propertyPath}": "${error.message}",
              </c:forEach>
          };
      </script>
      ```
    * Això converteix la llista d'errors del servidor en un objecte JavaScript que després l'script `validation.js` utilitza per mostrar missatges personalitzats.

> Nota: JSTL i EL són eines de la capa de vista. No executen lògica de negoci; només presenten dades i iteracions a les JSP.

## 🎨 2. Patrons de Disseny Utilitzats

Per garantir un codi net, escalable i professional, aquest projecte fa servir tres patrons clau de manera consistent. Abans d'explicar els patrons, aquí tens una descripció dels components principals i què fa cada un:

### 📋 Components del Sistema
*   **`Register.jsp`**: La interfície d'usuari (Vista). Conté el formulari HTML5. Aquí definim les restriccions bàsiques com `required`, `minlength` o `pattern`.
*   **`js/validation.js`**: El cervell de la validació al client. Utilitza la **Constraint Validation API** per gestionar missatges d'error personalitzats (com la comparació de contrasenyes) i per "traduir" els errors que venen del servidor cap al navegador.
*   **`User.java`**: El Model de dades. Representa l'usuari i pot contenir anotacions de **Jakarta Bean Validation** (depenent de la branca en la qual ens trobem). És l'objecte que viatja entre la vista i la base de dades.
*   **`UserRepository.java`**: La capa de dades. És un **Singleton** que s'encarrega exclusivament d'interactuar amb la base de dades (sigui manualment o via ORM depenent de la branca).
*   **`UserService.java`**: La capa de servei. És un **Singleton** on resideix la lògica de registre i validació.
*   **`Register.java`**: Controlador (servlet) que utilitza el servei respectiu per gestionar el flux de la petició.
*   **`DBManager.java`**: Gestiona la connexió amb SQLite mitjançant un **Singleton**.

### 📐 A. MVC (Model-Vista-Controlador)
És l'esquelet de la nostra aplicació. El seu objectiu és la **separació de preocupacions**:
*   **Model**: Les dades i les regles de validació (`User`). No sap res de la interfície.
*   **Vista**: El que l'usuari veu i toca (`JSP->HTML`, `CSS`,`JS`). Només rep, mostra i valida dades bàsicament al client.
*   **Controlador**: El Servlet (`Register`) que actua com a "director d'orquestra". Rep la petició de l'usuari, parla amb el servei corresponent i decideix la següent vista.

### 🔐 B. Singleton (Instància Única)
S'utilitza per a components que han de ser globals i únics en tota l'aplicació (`DBManager`, `UserRepository`, `UserService`):
*   **Per què?** Garanteix que **només existeixi una sola instància** de cada component compartida per tot el servidor. Això estalvia recursos (com no haver de re-inicialitzar la connexió a la BD o la factoria de validació en cada clic) i evita conflictes de concurrència.

### 📦 C. Patró Repository & Service
Aquest sistema serveix per desacoblar on es guarden les dades de com es processen:
*   **Repository**: És l'únic que coneix el SQL. No sap res de regles de negoci; només guarda i recupera dades.
*   **Service**: Aquí va la lògica "intel·ligent". El servei coordina la validació i, si tot és correcte, demana al Repositori que desi les dades. El controlador mai parla directament amb el Repositori; sempre ho fa a través del Servei.

## 🔐 3. Validació al client i validació al servidor

En aquest projecte fem validació en dos punts diferents i complementaris:

*   **Validació al client**: es fa amb HTML5 (`required`, `minlength`, `pattern`) i JavaScript (`validation.js`).
    * Ofereix feedback immediat a l'usuari abans d'enviar el formulari.
    * Millora l'experiència d'usuari: redueix errors simples i evita enviaments innecessaris.
    * No és suficient per si sola perquè l'usuari pot modificar o ometre el codi JavaScript.

*   **Validació al servidor**: es fa dins del servei/servlet, ja sigui manualment o utilitzant Jakarta Bean Validation depenent de la branca de l'aplicació.
    * És la validació definitiva que garanteix la seguretat i consistència de les dades.
    * Protegeix contra entrades malicioses, peticions directes i clients que no segueixen les regles del navegador.
    * És obligatòria sempre, encara que ja hi hagi validació al client.

> És important fer-ho als dos llocs: el client per facilitar l'ús i l'experiència, i el servidor per garantir la integritat i la seguretat de l'aplicació.

## 🎯 4. Constraint Validation API i Feedback Visual (Client)

Aquesta API de HTML5 és la que permet que el navegador gestioni l'estat de validació de cada camp de manera nativa.

### 💻 JavaScript (`validation.js`)
Els mètodes clau que fem servir són:
*   **`setCustomValidity(missatge)`**: Crucial per personalitzar la validació. Si el missatge és text, el navegador marca el camp com a invàlid. Si és buit, el marca com a vàlid.
*   **`checkValidity()`**: Comprova tots els controls del formulari contra les restriccions HTML5 i les validacions personalitzades definides amb `setCustomValidity()`. Retorna `true` si el formulari és vàlid.
*   **`reportValidity()`**: Fa el mateix que `checkValidity()` i, a més, mostra les bafarades d'error natives del navegador per als camps invàlids.

### 🎨 CSS (Pseudoclases)
Perquè l'usuari sàpiga si està escrivint correctament abans fins i tot d'enviar el formulari, fem servir pseudoclasses de CSS que reaccionen automàticament a l'API de validació:
*   **`:valid`**: S'aplica automàticament quan l'input compleix totes les restriccions (HTML5 + `setCustomValidity`). Al projecte, pintem un marge verd.
*   **`:invalid`**: S'aplica quan el camp no és vàlid. Al projecte, pintem un marge vermell per alertar l'usuari.

## ✅ 5. Branques del Projecte

L'aprenentatge d'aquest projecte s'ha estructurat en **tres branques** de Git progressives:

1.  **Branca `main` (Tot a mà / Validació Manual)**:
    * En aquesta branca, tota la validació es fa manualment.
    * La validació es realitza camp per camp dins del codi Java (`UserService`), sense utilitzar cap framework de validació extern.
    * L'objectiu principal d'aquesta opció és aprendre el funcionament intern de les regles de validació pas a pas.
    * La interacció amb la base de dades es fa mitjançant consultes SQL i JDBC directe a `UserRepository`.

2.  **Branca `jakarta-validation`**:
    * Aquesta branca introdueix **Jakarta Bean Validation** (implementat per Hibernate Validator).
    * S'utilitza **validació declarativa** mitjançant anotacions (`@NotBlank`, `@Size`, `@Email`, etc.) dins de la classe del model `User`.
    * És la forma moderna i habitual quan treballem amb aplicacions Java/Jakarta EE, ja que separa la definició de les regles de validació de la lògica del servei. Tanmateix, a vegades cal continuar creant anotacions o validacions personalitzades quan les opcions estàndard no són suficients (podeu trobar un exemple d'això al paquet `validator`).

3.  **Branca `orm-hibernate`**:
    * Manté tota la validació declarativa de l'anterior branca i introdueix un **ORM (Object-Relational Mapping)**: Hibernate.
    * L'ORM s'encarrega d'interactuar amb la base de dades a la classe `UserRepository` i gestiona la persistència.
    * Ja no s'escriuen sentències SQL manuals a `UserRepository`, sinó que es fa un mapejat directe entre la classe Java `User` i les taules de la base de dades gràcies a l'entorn de JPA i Hibernate.

## 🚀 Objectiu del Lab 2

> [!IMPORTANT]
> L'objectiu final d'aquest laboratori és **construir el formulari de registre complet** que vau dissenyar durant el Seminari 1.
> 
> Heu d'implementar tots els camps que vau definir (nom, cognoms, data de naixement, correu, etc.) i assegurar-vos que:
> 1. El formulari dona **feedback immediat** a l'usuari abans d'enviar-se.
> 2. El servidor comprova **totes les regles de validació** abans d'emmagatzemar l'usuari.
> 3. Les dades només es guarden si passen **ambdues validacions**.

---

## ❓ Preguntes Freqüents (FAQ)

### 1. Quin dels tres camins (branques) es recomana per l'assignatura?
L'enfocament principal i que recomanem per aprendre i superar l'assignatura és la **branca `main` (Tot a mà)**. Tot i que expliquem les altres opcions (`jakarta-validation` i `orm-hibernate`) per motius il·lustratius i perquè veieu el potencial de l'entorn modern de Java EE, per a les pràctiques no recomanem el seu ús, ja que creiem que fer les validacions i el JDBC manualment aporta un major aprenentatge dels fonaments.

### 2. Per què no validem als Mutadors (Setters)?
Una pregunta habitual és per què no posem la validació directament dins dels mètodes `set...` del Model. Ho evitem per bona praxi de disseny:
*   **Separació de Responsabilitats**: El Model només ha de guardar dades. La validació és una lògica de negoci que correspon als Serveis.
*   **Flexibilitat**: Un camp pot ser obligatori en un formulari de registre però opcional en un de modificació de perfil. Si posem la validació al *setter*, el Model es torna massa rígid.
*   **Acoblament**: Validacions com "comprovar si l'usuari existeix" requereixen accés a la base de dades. Posar SQL dins d'un *setter* crearia un acoblament molt perillós entre el Model i la Persistència.
*   **Neteja**: Moltes llibreries de Java esperen que els *setters* siguin mètodes simples. Posar-hi lògica complexa pot provocar errors inesperats en frameworks d'automatització.
