# WireMock – API Mock

Este directorio contiene:

- `/mappings` → configuraciones de los stubs
- `/__files` → respuestas JSON asociadas
- `/postman` → colección de Postman con todos los ejemplos de pruebas y el environment
- `README.md` → este archivo

## Ejecutar WireMock

```bash
java -jar wiremock-standalone-3.13.2.jar
```

Por defecto la API mock se levantará en: http://localhost:8080

## Usar la colección Postman
- Abrir Postman e importar la colección desde: `wiremock/postman/Wiremock.postman_collection.json`
- Importar también el environment `wiremockLocal` en la esquina superior derecha de Postman.  Ahora todas las variables `{{baseURL}}` de la colección se reemplazarán automáticamente por http://localhost:8080.



