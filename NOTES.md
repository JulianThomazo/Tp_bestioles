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
