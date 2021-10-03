# Shared library

This project is responsible for
- Enable client security (decode JWT token etc...)
- Shared JSON/Java specification between all of service modules

# How to?

## Upload maven archives to staging nexus repository

`./gradlew clean build uploadArchives -Penv=staging`

## Upload maven archives to dev nexus repository

`./gradlew clean build uploadArchives`

## Upload maven archives to local developer repository

`./gradlew clean build install`

### ignore check `pmd`
- For Linux or Macos `./gradle clean build install -x pmdMain`
- For Window `./gradle clean build install -x :pmdMain`
