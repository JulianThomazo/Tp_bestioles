# TP 01 – Bestioles

Toutes les commandes sont sur le port 8080, qui était libre sur mon poste. J'ai tout lancé sous PowerShell (`.\mvnw.cmd`, `curl.exe`).

## Étape 2 – Dépendances du pom

Je ne trouvais pas Actuator dans la recherche de start.spring.io, donc je l'ai mis à la main dans le pom (le starter + le starter de test, comme l'Initializr le fait).

| artifactId | scope |
|---|---|
| spring-boot-starter-webmvc | – |
| spring-boot-devtools | runtime (optional) |
| spring-boot-starter-webmvc-test | test |
| spring-boot-starter-actuator | – |
| spring-boot-starter-actuator-test | test |

Aucune dépendance n'a de balise `<version>` (0).

C'est le parent `spring-boot-starter-parent` 4.1.2 qui donne les versions : il importe le BOM `spring-boot-dependencies`, et c'est son `<dependencyManagement>` qui fixe la version de chaque dépendance.

## Étape 4 – Test généré

Prédiction : `contextLoads()` est vide, il sert juste à vérifier que le contexte Spring démarre sans erreur.

```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Étape 5 – Démarrage

Le port 8080 était libre, donc pas d'échec.

```
Started BestiolesApplication in 2.935 seconds (process running for 3.456)
```

## Étape 6 – Santé

```
{"groups":["liveness","readiness"],"status":"UP"}
```

## Étape 8 – Trois appels

| Appel | Prédiction | Obtenu |
|---|---|---|
| `/hello?name=Lea` | `Bonjour, Lea !` | `Bonjour, Lea !` |
| `/hello` | erreur 400 (paramètre manquant) | `Bonjour, monde !` |
| `/hello?name=` | erreur 400 | `Bonjour, monde !` |

Je me suis trompé deux fois : avec `defaultValue` le paramètre devient facultatif, donc il n'y a pas de 400. Et quand le paramètre est là mais vide, Spring prend aussi la valeur par défaut.

## Étape 9 – Redémarrage DevTools

```
Started BestiolesApplication in 0.523 seconds (process running for 479.722)
```
Réponse : `Salut, Lea !`

Le PID est le même qu'au lancement (3728), donc c'est la même JVM. DevTools relance seulement le contexte Spring, avec un classloader qui recharge uniquement nos classes. Les jars des dépendances ne sont pas rechargés, c'est pour ça que ça prend 0,5 s. `process running for` compte depuis le démarrage de la JVM, qui datait d'environ 8 min.

Au passage, j'avais écrit `@GetMapping("/Hello")` et j'avais une 404 sur `/hello` : les chemins sont sensibles à la casse.

## Étape 10 – Contrôleur hors du package racine

Prédiction : 500 Internal Server Error.

```
HTTP/1.1 404
Content-Type: application/json

{"timestamp":"2026-11-17T10:14:18.458Z","status":404,"error":"Not Found","trace":"org.springframework.web.servlet.resource.NoResourceFoundException: No static resource hello for request '/hello'. ...","message":"No static resource hello.","path":"/hello"}
```
(j'ai coupé la trace, `...`)

Comme `@SpringBootApplication` scanne uniquement `org.sebsy.bestioles` et ses sous-packages, `org.sebsy.web.HelloController` n'est jamais enregistré. Il n'y a donc aucune route `/hello`, d'où la 404 et pas une 500.

---

## Étape 11 – Rapport `--debug`

DispatcherServletAutoConfiguration :
```
DispatcherServletAutoConfiguration matched:
   - @ConditionalOnClass found required class 'org.springframework.web.servlet.DispatcherServlet' (OnClassCondition)
   - found 'session' scope (OnWebApplicationCondition)
```

JacksonAutoConfiguration est déclenchée par la classe `tools.jackson.databind.json.JsonMapper` (Jackson 3, ce n'est plus `com.fasterxml`).

DataSourceAutoConfiguration :
- Prédiction : dans les Negative matches, vu qu'il n'y a pas de driver ni de classe DataSource.
- Trouvé : rien du tout, `Select-String -Pattern "DataSource"` ne sort aucune ligne.
- Correction : depuis Boot 4, les autoconfigurations sont découpées par module. `DataSourceAutoConfiguration` fait partie de `spring-boot-jdbc`, et aucun de nos starters ne l'amène. Comme la classe n'est pas sur le classpath, elle n'est jamais évaluée et ne se retrouve dans aucune section.

## Étape 12 – Contenu du jar

Prédiction : oui, on trouve DevTools dans `BOOT-INF/lib/`.

- Taille : 22 042 872 octets (≈ 21 Mo)
- Jars dans `BOOT-INF/lib/` : 42
- DevTools : pas là (`Select-String "devtools"` ne trouve rien)

Ma prédiction était fausse : comme DevTools est un outil de dev, le repackage du plugin Maven l'enlève par défaut. Par contre on retrouve bien `tomcat-embed-core-11.0.27.jar`, le serveur est inclus dans le jar.

(Comme `jar` n'était pas dans le PATH, j'ai fait un alias vers le `bin\jar.exe` du JDK 25.)

## Étape 13 – Manifeste

```
Main-Class: org.springframework.boot.loader.launch.JarLauncher
Start-Class: org.sebsy.bestioles.BestiolesApplication
```

- `JarLauncher` : c'est elle que `java -jar` lance. Elle crée un classloader capable de lire `BOOT-INF/classes` et les jars imbriqués dans `BOOT-INF/lib` (ce que Java ne sait pas faire tout seul), puis elle appelle la `Start-Class`.
- `BestiolesApplication` : c'est notre `main()`, qui appelle `SpringApplication.run` pour démarrer le contexte et Tomcat.

## Étape 14 – java -jar

```
Started BestiolesApplication in 8.713 seconds (process running for 9.345)
```
Réponse : `Bonjour, Julian !`

À l'étape 5 le thread était `restartedMain`, ici c'est `main`.
Avec `spring-boot:run`, DevTools relance l'appli dans un thread `restartedMain` qui a un classloader rechargeable, et c'est ça qui permet le redémarrage à chaud. Dans le jar il n'y a pas DevTools, donc l'appli tourne directement dans `main` et rien n'est rechargé.

---

## TP 02

On reste sur le port 8080, sous PowerShell. Comme `javap` et `jar` ne sont pas dans le PATH, j'ai fait des alias vers le `bin\` du JDK 25.

### Étape 3 – Démarrage sans Clock

Prédiction : l'appli démarre, avec une `Clock` système fournie par défaut par Spring.

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 1 of constructor in org.sebsy.bestioles.service.AdoptionService required a bean of type 'java.time.Clock' that could not be found.


Action:

Consider defining a bean of type 'java.time.Clock' in your configuration.
```

Ma prédiction était fausse, Boot ne donne pas de `Clock`. Le paramètre 1 correspond au 2e paramètre du constructeur (on commence à 0), donc `Clock clock`. Le paramètre 0, c'est `Notifier notifier`.

Une fois `HorlogeConfig` ajoutée (`@Bean Clock` sur `Europe/Paris`) : `Started BestiolesApplication in 2.952 seconds (process running for 3.511)`.

### Étape 5 – Test sans Spring

Prédiction : 31/12/2026.

```
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Ma prédiction était fausse, on obtient le 01/01/2027. `23:30Z` est une heure UTC, et en décembre Paris est à UTC+1, donc il est 00:30 le 1er janvier. `LocalDate.now(clock)` prend la date dans le fuseau de l'horloge. Le test sans Spring prend 0,017 s, alors que `BestiolesApplicationTests` prend environ 5 s.

### Étape 6 – /actuator/beans

| Bean | dependencies | type |
|---|---|---|
| `adoptionService` | `emailNotifier`, `clock` | `org.sebsy.bestioles.service.AdoptionService` |
| `clock` | `horlogeConfig` | `java.time.Clock$SystemClock` |

Si `clock` dépend de `horlogeConfig`, c'est parce que le bean vient de la méthode `@Bean clock()`, qui est une méthode d'instance de `HorlogeConfig`. Pour pouvoir l'appeler, Spring doit donc d'abord créer la config. Quant au type réel, `Clock$SystemClock`, c'est la classe concrète que renvoie `Clock.system(...)`.

### Étape 7 – Deux Notifier

Prédiction : l'appli démarre et les deux `Notifier` sont donnés à `AdoptionService`.

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in org.sebsy.bestioles.service.AdoptionService required a single bean, but 2 were found:
        - emailNotifier: defined in file [...\target\classes\org\sebsy\bestioles\notif\EmailNotifier.class]
        - smsNotifier: defined in file [...\target\classes\org\sebsy\bestioles\notif\SmsNotifier.class]

This may be due to missing parameter name information

Action:

Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed

Ensure that your compiler is configured to use the '-parameters' flag.
You may need to update both your build tool settings as well as your IDE.
(See https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-6.1-Release-Notes#parameter-name-retention)
```

Ma prédiction était fausse : le paramètre n'attend qu'un seul `Notifier`, or il y en a deux, et Spring ne fait pas le choix.

(Au premier essai l'appli avait démarré sans erreur avec `Nothing to compile`, parce que `SmsNotifier` n'était pas compilé. Je l'ai relancé avec `clean`.)

### Étape 9 – La piste `-parameters`

```
>     MethodParameters:
        Name                           Flags
        notifier
        clock

>     MethodParameters:
        Name                           Flags
        animal
        email
```

On trouve bien la section avec les vrais noms, donc `-parameters` est déjà actif : c'est le parent Boot qui le configure (le `parameters` dans `[debug parameters release 25]`). Ce n'est donc pas ça qui explique l'erreur. Spring connaît bien le nom `notifier`, mais comme aucun bean ne porte ce nom (`emailNotifier`, `smsNotifier`), le repli par nom ne permet pas de choisir.

### Étape 10 – Trois façons de lever le conflit

- `AdoptionService` : pas modifié, c'est `EmailNotifier` qui est `@Primary` (le canal par défaut).
- `RappelService` : on met `@Qualifier("smsNotifier")` sur le paramètre.
- `AlerteService` : il reçoit une `List<Notifier>` et envoie sur tous les canaux.

### Étape 11 – Dépendances relevées

| Service | Prédiction | Obtenu |
|---|---|---|
| `adoptionService` | `notifier`, `clock` | `emailNotifier`, `clock` |
| `rappelService` | `smsNotifier` | `smsNotifier` |
| `alerteService` | `notifiers` | `emailNotifier`, `smsNotifier` |

Là où je me suis trompé : `dependencies` affiche les noms des beans injectés, et pas ceux des paramètres. Et pour la `List`, on a un bean par élément.

### Étape 13 – verify et commit du socle

```
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### Étape 8 – Quand le conflit casse le build

J'ai mis `@Primary` en commentaire. Prédiction : on n'a pas de jar, parce qu'un test échoue et que Maven s'arrête avant.

`.\mvnw.cmd clean package` (avec clean pour ne pas récupérer le jar du verify d'avant) :

```
[ERROR]   BestiolesApplicationTests.contextLoads » IllegalState Failed to load ApplicationContext ...
[ERROR] Tests run: 2, Failures: 0, Errors: 1, Skipped: 0
[ERROR] BUILD FAILURE
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:3.5.6:test (default-test) on project bestioles
```

`dir target\*.jar` ne trouve rien.

Ce n'est pas une erreur de compilation, vu que `javac` ne connaît pas Spring. Par contre, `package` passe d'abord par la phase `test`, et `BestiolesApplicationTests` (`@SpringBootTest`) lance tout le contexte. Le conflit fait échouer ce test, donc Maven s'arrête avant `package`. J'ai remis `@Primary` après.

### Étape 12 – Qualifier avec majuscule

`@Qualifier("SmsNotifier")`. Prédiction : ça démarre, et `RappelService` reçoit `emailNotifier` (celui qui est `@Primary`).

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in org.sebsy.bestioles.service.RappelService required a bean of type 'org.sebsy.bestioles.notif.Notifier' that could not be found.

The injection point has the following annotations:
        - @org.springframework.beans.factory.annotation.Qualifier("SmsNotifier")

The following candidates were found but could not be injected:
        - User-defined bean
        - User-defined bean


Action:

Consider revisiting the entries above or defining a bean of type 'org.sebsy.bestioles.notif.Notifier' in your configuration.
```

Ma prédiction était fausse : `@Qualifier` fait un filtre strict, et le nom tient compte de la casse. Comme le bean s'appelle `smsNotifier`, aucun candidat ne passe le filtre. Et `@Primary` sert seulement à départager les candidats qui sont passés. J'ai remis `"smsNotifier"`.

### Étape 14 – Dépendance circulaire

`Refuge` reçoit `Veterinaire` par constructeur, et inversement. Prédiction : erreur de compilation.

```
***************************
APPLICATION FAILED TO START
***************************

Description:

The dependencies of some of the beans in the application context form a cycle:

┌─────┐
|  refuge defined in file [...\target\classes\org\sebsy\bestioles\cycle\Refuge.class]
?     ?
|  veterinaire defined in file [...\target\classes\org\sebsy\bestioles\cycle\Veterinaire.class]
└─────┘


Action:

Relying upon circular references is discouraged and they are prohibited by default. Update your application to remove the dependency cycle between beans. As a last resort, it may be possible to break the cycle automatically by setting spring.main.allow-circular-references to true.
```

(Les `?` viennent des flèches du dessin, que la console PowerShell affiche mal.)

Ma prédiction était fausse, ça compile (`Compiling 11 source files`) : pour Java, deux classes qui se référencent, ça ne pose pas de problème. C'est au moment de créer les beans que ça bloque.

### Étape 15 – allow-circular-references

Prédiction : ça ne démarre toujours pas, parce qu'on ne peut pas casser un cycle par constructeur.

```
Action:

Despite circular references being allowed, the dependency cycle between beans could not be broken. Update your application to remove the dependency cycle.
```

Ce que fait la propriété, c'est exposer une référence anticipée vers un bean qui est déjà instancié mais pas encore complètement injecté (injection par champ ou par setter, donc après le `new`). Ici tout se fait par le constructeur : on a besoin d'un `Veterinaire` pour faire `new Refuge(...)`, et d'un `Refuge` pour faire `new Veterinaire(...)`. Aucun des deux ne peut être créé en premier, donc il n'y a rien à exposer.

### Étape 16 – Cycle supprimé par la conception

J'ai mis le besoin commun (la liste des pensionnaires) dans un bean `Registre`. `Refuge` reçoit maintenant `Registre` et `Veterinaire`, et `Veterinaire` reçoit `Registre`.

| Bean | dependencies |
|---|---|
| `refuge` | `registre`, `veterinaire` |
| `veterinaire` | `registre` |
| `registre` | (aucune) |

### Étape 17 – Retrait

```
677973c (HEAD -> main) Retrait de l'exercice du cycle
6100be3 Résolution du cycle Refuge/Veterinaire par un Registre
d4eda83 TP 02 socle : Notifier, services, HorlogeConfig, test
```

Après le retrait, `verify` donne `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`.
