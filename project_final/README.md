# MVC DAO JDBC — Guía de despliegue desde cero

Este proyecto implementa el patrón **MVC + DAO genérico** en Java Vanilla (sin Spring, sin Hibernate).
La guía a continuación te lleva desde una máquina vacía hasta la aplicación corriendo, paso a paso.

---

## Requisitos previos

Antes de clonar o descomprimir el proyecto, asegurate de tener instalado lo siguiente en tu máquina:

| Herramienta | Versión mínima | Para qué se usa |
|---|---|---|
| JDK | 17 | Compilar y ejecutar el proyecto |
| Maven | 3.8+ | Gestionar dependencias y construir el JAR |
| PostgreSQL **o** MySQL | Cualquier versión reciente | Motor de base de datos |
| IntelliJ IDEA (opcional) | Cualquiera | IDE recomendado para el curso |

Para verificar que JDK y Maven estén bien instalados, abrí una terminal y ejecutá:

```bash
java -version
mvn -version
```

Ambos comandos deben responder con su versión sin errores. Si alguno falla, revisá que la variable de entorno `JAVA_HOME` apunte al directorio de instalación del JDK.

---

## Estructura del proyecto

Antes de empezar es importante entender qué hay en cada carpeta, porque esto refleja directamente los principios de POO que se evalúan en el curso.

```
src/
└── main/
    ├── java/com/app/
    │   ├── Main.java                        ← Punto de entrada, aplica Factory para elegir la vista
    │   ├── config/
    │   │   └── AppConfig.java               ← Singleton que carga los .properties
    │   ├── db/
    │   │   └── ConnectionManager.java       ← Singleton para conexiones JDBC
    │   ├── model/entity/
    │   │   ├── Usuario.java                 ← Entidad Usuario (POJO)
    │   │   └── Producto.java                ← Entidad Producto (POJO) — nueva
    │   ├── dao/
    │   │   ├── GenericDAO.java              ← Interfaz genérica con T e ID
    │   │   ├── UsuarioDAO.java              ← Interfaz específica de Usuario
    │   │   ├── ProductoDAO.java             ← Interfaz específica de Producto — nueva
    │   │   └── impl/
    │   │       ├── GenericDAOImpl.java      ← Clase abstracta con CRUD genérico
    │   │       ├── UsuarioDAOImpl.java      ← Implementación concreta de Usuario
    │   │       └── ProductoDAOImpl.java     ← Implementación concreta de Producto — nueva
    │   ├── controller/
    │   │   └── UsuarioController.java       ← Orquesta DAO y Vista
    │   └── view/
    │       ├── View.java                    ← Interfaz de contrato para toda vista
    │       ├── BaseView.java                ← Clase abstracta con formato compartido
    │       ├── ConsoleView.java             ← Vista por consola
    │       └── SwingView.java               ← Vista gráfica con Swing
    └── resources/
        ├── app.properties                   ← Nombre de la app y tipo de vista
        ├── database.properties              ← Perfil activo de BD (el que la app lee)
        ├── database-postgres.properties     ← Configuración lista para PostgreSQL
        ├── database-mysql.properties        ← Configuración lista para MySQL
        └── schema.sql                       ← Script DDL para crear las tablas
```

---

## Paso 1 — Elegir y configurar la base de datos

El proyecto puede conectarse a **PostgreSQL** o **MySQL**. El mecanismo es simple: la clase `AppConfig` siempre carga el archivo llamado `database.properties`, así que para cambiar de motor solo tenés que copiar el perfil que querés usar sobre ese archivo.

### Opción A — PostgreSQL

```bash
# Desde la raíz del proyecto
cp src/main/resources/database-postgres.properties src/main/resources/database.properties
```

El contenido por defecto del perfil PostgreSQL es:

```properties
db.url      = jdbc:postgresql://localhost:5432/appdb
db.user     = postgres
db.password = 123456
db.driver   = org.postgresql.Driver
```

Ajustá `db.user` y `db.password` a los valores de tu instalación local antes de continuar.

### Opción B — MySQL

```bash
cp src/main/resources/database-mysql.properties src/main/resources/database.properties
```

El contenido por defecto del perfil MySQL es:

```properties
db.url      = jdbc:mysql://localhost:3306/appdb?serverTimezone=UTC&useSSL=false
db.user     = root
db.password = 123456
db.driver   = com.mysql.cj.jdbc.Driver
```

El parámetro `serverTimezone=UTC` es obligatorio en MySQL 8+; sin él el driver lanza un error de zona horaria al iniciar la conexión.

---

## Paso 2 — Crear la base de datos y las tablas

El proyecto **no crea la base de datos automáticamente**. Vos debés crearla manualmente una sola vez y luego ejecutar el script DDL del proyecto.

### En PostgreSQL

Abrí `psql` (o pgAdmin) y ejecutá:

```sql
CREATE DATABASE appdb;
```

Luego conectate a la nueva base de datos y ejecutá el schema:

```bash
# Desde la terminal, en la raíz del proyecto
psql -U postgres -d appdb -f src/main/resources/schema.sql
```

### En MySQL

Abrí tu cliente MySQL (terminal o Workbench) y ejecutá:

```sql
CREATE DATABASE IF NOT EXISTS appdb;
USE appdb;
```

Luego buscá en `schema.sql` la sección comentada de MySQL, copiá y pegá ese bloque en tu cliente. Los comentarios en el archivo están marcados claramente con `-- MySQL Schema (Referencia)`.

Las tablas que se crean son `usuarios` y `productos`, con sus índices correspondientes. La columna `precio` de productos usa `NUMERIC(10,2)` en PostgreSQL y `DECIMAL(10,2)` en MySQL, que son equivalentes y garantizan precisión monetaria sin errores de redondeo.

---

## Paso 3 — Construir el proyecto con Maven

Desde la raíz del proyecto (donde está el `pom.xml`) ejecutá:

```bash
mvn clean compile
```

Maven descargará automáticamente las dependencias declaradas en el `pom.xml`:

- `org.postgresql:postgresql:42.7.2` — driver JDBC para PostgreSQL
- `com.mysql:mysql-connector-j:8.3.0` — driver JDBC para MySQL
- `org.junit.jupiter:junit-jupiter:5.10.2` — para tests

No necesitás agregar ningún JAR manualmente; Maven se encarga de todo.

Si la compilación termina con `BUILD SUCCESS`, el proyecto está listo.

---

## Paso 4 — Ejecutar la aplicación

### Desde IntelliJ IDEA

Abrí el proyecto con **File → Open** y seleccioná la carpeta raíz (donde está el `pom.xml`). IntelliJ detecta automáticamente que es un proyecto Maven. Luego buscá la clase `Main.java` en `src/main/java/com/app/` y hacé clic derecho → **Run 'Main'**.

### Desde la terminal

```bash
mvn exec:java -Dexec.mainClass="com.app.Main"
```

O bien, si preferís generar el JAR primero:

```bash
mvn package
java -cp target/mvc-dao-jdbc-1.0.0.jar com.app.Main
```

> **Nota:** si usás el JAR, asegurate de incluir las dependencias en el classpath. La forma más sencilla para desarrollo es usar `mvn exec:java` directamente.

---

## Paso 5 — Cambiar el tipo de vista

La aplicación soporta dos vistas: consola y Swing (ventana gráfica). Para cambiar entre ellas editá `src/main/resources/app.properties`:

```properties
# Para vista por consola (por defecto)
view.type = console

# Para vista gráfica con Swing
view.type = swing
```

No es necesario recompilar si ejecutás con Maven, ya que los recursos se leen del classpath en tiempo de ejecución.

---

## Principios POO aplicados en el proyecto

Este apartado resume los conceptos de orientación a objetos que están implementados, útil para la defensa ante el evaluador.

**Encapsulamiento** — todos los campos de las entidades (`Usuario`, `Producto`) son `private` con acceso únicamente a través de getters y setters públicos. Ningún campo se expone directamente.

**Abstracción** — `GenericDAO<T, ID>` define el contrato de las operaciones CRUD sin exponer cómo se implementan. El Controller solo conoce `UsuarioDAO` (una interfaz), nunca `UsuarioDAOImpl`.

**Herencia** — `GenericDAOImpl<T, ID>` es una clase abstracta que implementa el CRUD genérico. `UsuarioDAOImpl` y `ProductoDAOImpl` la extienden y solo aportan el SQL y el mapeo específico de cada entidad, evitando duplicación de código.

**Polimorfismo** — el `UsuarioController` recibe un `View` (interfaz) por constructor. En tiempo de ejecución puede ser una `ConsoleView` o una `SwingView` sin que el Controller cambie una sola línea.

**Patrón Singleton** — `AppConfig` y `ConnectionManager` garantizan que solo exista una instancia de cada uno durante toda la ejecución, centralizando la configuración y la gestión de conexiones.

**Inyección de dependencias** — el Controller recibe sus dependencias (vista y DAO) por constructor, no las instancia internamente. Esto lo hace fácilmente testeable y desacoplado.

---

## Cómo cambiar de base de datos en el futuro

El diseño de `database.properties` + perfiles separados permite cambiar de motor en tres pasos sin tocar código Java:

1. Copiás el perfil deseado sobre `database.properties`.
2. Creás la base de datos y ejecutás `schema.sql` en el nuevo motor.
3. Ejecutás la aplicación normalmente.

Eso es todo. El `ConnectionManager` lee el driver y la URL del `AppConfig`, así que el cambio es completamente transparente para el resto de la aplicación.

---

## Errores comunes y cómo resolverlos

**`ClassNotFoundException: org.postgresql.Driver`** — Maven no descargó las dependencias. Ejecutá `mvn dependency:resolve` y revisá tu conexión a internet.

**`Connection refused` al iniciar** — la base de datos no está corriendo. Verificá que el servicio PostgreSQL o MySQL esté activo en tu sistema operativo.

**`FATAL: password authentication failed`** — las credenciales en `database.properties` no coinciden con las de tu instalación local. Revisá `db.user` y `db.password`.

**`Table 'appdb.usuarios' doesn't exist`** — no se ejecutó el `schema.sql`. Seguí el Paso 2 de esta guía.

**`No se encontró: database.properties`** — olvidaste copiar el perfil activo. Seguí el Paso 1 de esta guía.
