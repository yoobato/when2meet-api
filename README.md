# when2meet-api
When2Meet Clone (API)

## Requirements
- JDK 17 (Eclipse Temurin OpenJDK 17.0.6)

## Deployment
- Make `application-prod.properties` using `application-dev.properties` (Update below configurations.)
  ```yaml
  ## Database (in-memory or file)
  # spring.datasource.url=jdbc:h2:mem:test|jdbc:h2:file:/{path}/data
  # DB user and password
  spring.datasource.username=sa
  spring.datasource.password=

  spring.jpa.show-sql=true|false
  
  # update : local, development only
  # validate : production
  spring.jpa.hibernate.ddl-auto=validate
  
  # Hide H2 Console
  spring.h2.console.enabled=false

  ## Logging (log level)
  logging.level.root=warn
  ```
- TODO: Docker

## Authors
- [Daeyeol Ryu](https://github.com/yoobato)
- [Inyoung Aum](https://github.com/InyoungAum)

## License
- [Unlicense](./UNLICENSE.md) (Public Domain)
