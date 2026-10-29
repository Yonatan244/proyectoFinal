### Sistema de Gestión de Inventario Junior (CLI)

Este proyecto es una aplicación de consola en Java 21 orientada a objetos (POO) diseñada para gestionar el inventario de una tienda. Implementa un proceso automatizado de Extracción, Transformación y Carga (ETL) que permite importar archivos masivos desnormalizados, limpiar los datos y distribuirlos de forma eficiente en una base de datos relacional. 

### 🚀 Características Principales

* **Arquitectura en Capas:** Separación estricta de responsabilidades (Presentación, Servicios, Configuración y Modelos).
* **Base de Datos Persistente:** Uso de H2 Database en modo embebido local (genera el archivo proyectoFinal.mv.db).
* **Flujo Circular (Idempotente):** Los datos creados manualmente o importados conviven en armonía y se pueden exportar e importar de vuelta indefinidamente sin perder información.
* **Robustez:** Control de errores en la consola mediante Scanner para evitar cuelgues si el usuario introduce datos erróneos.

### 🛠️ Stack Tecnológico

* **Lenguaje:** Java 21 LTS (Codificación UTF-8).
* **Gestor de Dependencias:** Maven.
* **Motor de Base de Datos:** H2 Database (Versión 2.2.224).

### 📐 Diseño de la Base de Datos Relacional

El sistema organiza los datos a través de 4 tablas para garantizar la integridad y evitar la duplicidad: 

1. **Tabla_general (Tránsito / Staging):** Almacena temporalmente la "sábana de datos" cruda extraída del archivo masivo. No tiene restricciones de integridad.
2. **Proveedores (Maestra):** Registra los datos de contacto únicos de los proveedores. Su clave primaria es un código alfanumérico (id_proveedor).
3. **Ficha_productos (Logística):** Enlaza cada código de producto único (id_producto) con su respectivo proveedor mediante una clave foránea. Controla fechas de lote.
4. **Catalogo_tienda (Comercial):** Controla exclusivamente el stock y el precio de venta al público (PVP). Se conecta directamente con Ficha_productos.

### 📊 Diagrama de Flujo del Sistema

A continuación se detalla visualmente cómo viaja la información y cómo interactúa el usuario con los menús de la aplicación: 

text

       [ Inicio de la Aplicación ]
                    │
                    ▼
     ┌──────────────────────────────┐
     │ InicializadorBaseDatos       │
     │ (Crea las 4 tablas en H2 si  │
     │  no existen en el archivo db)│
     └──────────────┬───────────────┘
                    │
                    ▼
       ┌──────────────────────────┐
┌────►│  MENÚ PRINCIPAL (1 al 9) │◄─────────────────────────────────────────────┐
│     └────────────┬─────────────┘                                              │
│                  │                                                            │
│                  ├───────────────────────────────┐                            │
│                  ▼                               ▼                            │
│       [Opción 1: Importar .csv]       [Opción 3: Agregar Manual]              │
│                  │                               │                            │
│                  ▼                               ▼                            │
│       ┌─────────────────────┐         ┌──────────────────────────────┐        │
│       │ ImportadorCSV       │         │ RegistradorProducto          │        │
│       │ (Lee tabulaciones,  │         │ (Captura datos por teclado   │        │
│       │  limpia e inserta   │         │  con Scanner protegido)      │        │
│       │  en Tabla_general)  │         └──────────┬───────────────────┘        │
│       └──────────┬──────────┘                    │                            │
│                  │                               ▼                            │
│                  ▼                    ┌──────────────────────────────┐        │
│       ┌─────────────────────┐         │ Transacción Segura (H2)      │        │
│       │ ProcesadorETL       │         │ (Inserta de golpe en:        │        │
│       │ (Filtra duplicados y│         │  - Tabla_general             │        │
│       │  pobla Proveedores, │         │  - Ficha_productos           │        │
│       │  Fichas y Catálogo) │         │  - Catalogo_tienda)          │        │
│       └──────────┬──────────┘         └──────────┬───────────────────┘        │
│                  │                               │                            │
│                  ▼                               ▼                            │
│       ┌──────────────────────────────────────────────────────────────┐        │
│       │       Los datos quedan consolidados en el archivo local      │        │
│       └──────────────────────────┬───────────────────────────────────┘        │
│                                  │                                            │
├──────────────────────────────────┴────────────────────────────────────────────┤
│                                                                               │
│                  ├───────────────────────────────┐                            │
│                  ▼                               ▼                            │
│       [Opción 2: Ver Catálogo]        [Opción 7: Exportar .csv]               │
│                  │                               │                            │
│                  ▼                               ▼                            │
│       ┌─────────────────────┐         ┌──────────────────────────────┐        │
│       │ ConsultorCatalogo   │         │ ExportadorCSV                │        │
│       │ (Hace SELECT a      │         │ (Lee Tabla_general, convierte│        │
│       │  Catalogo_tienda y  │         │  fechas a DD/MM/AAAA e       │        │
│       │  pinta tabla limpia)│         │  inserta índice consecutivo) │        │
│       └─────────────────────┘         └──────────┬───────────────────┘        │
│                                                  │                            │
│                                                  ▼                            │
│                                       ┌──────────────────────────────┐        │
│                                       │ Genera 'inventario_respaldo' │        │
│                                       │ listo para reimportar en opc1│        │
│                                       └──────────────────────────────┘        │
│                                                                               │
│                  ├───────────────────────────────┤                            │
│                  ▼                               ▼                            │
│       [Opciones 4, 5, 6 y 8]             [Opción 9: Salir]                    │
│       (Buscar, Editar, Eliminar           (Cierra el Scanner y termina        │
│        e Invalidad Tablas de               el proceso de forma segura)        │
│        forma transaccional)                                                   │
│                                                                               │
└───────────────────────────────────────────────────────────────────────────────┘

Usa el código con precaución.

### 🏃‍♂️ Guía de Uso Paso a Paso

Sigue estos sencillos pasos para probar todo el potencial circular del sistema: 

### Paso 1: Preparación del archivo inicial

Asegúrate de tener un archivo llamado inventario.csv en la raíz de tu proyecto (al mismo nivel que el pom.xml). Si lo exportas desde Excel, asegúrate de guardarlo como **Texto delimitado por tabulaciones** para evitar problemas de formato regional con las comas de los precios. 

### Paso 2: Ejecución del programa e Importación

1. Inicia la aplicación desde tu entorno de desarrollo.
2. Introduce la **Opción 1** para importar los productos del archivo plano.
3. El sistema procesará el archivo y distribuirá de forma automática los datos en el modelo relacional.

### Paso 3: Operaciones mixtas

1. Selecciona la **Opción 2** para comprobar que el catálogo muestra los productos formateados.
2. Selecciona la **Opción 3** para agregar un nuevo producto personalizado de forma manual (ej: ID 777, Café Premium).
3. Vuelve a revisar el catálogo con la **Opción 2** para verificar que el producto manual y los importados conviven juntos en la cuadrícula.

### Paso 4: Exportación e Interoperabilidad (Cierre de Ciclo)

1. Selecciona la **Opción 7** para exportar el inventario actual. Esto creará el archivo inventario_respaldo.csv con la estructura exacta que requiere el sistema.
2. Selecciona la **Opción 8** para vaciar por completo todas las tablas de la base de datos de forma segura.
3. Consulta la **Opción 2** para verificar que el catálogo ha quedado completamente vacío.
4. Cambia el archivo de lectura en tu código de la Opción 1 para que apunte a inventario_respaldo.csv, ejecuta la **Opción 1** y verás cómo todos los productos (tanto los originales como los que hiciste a mano) regresan a la vida perfectamente.