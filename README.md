## common-redis
> A common util library for the  redis operations

## Deploy jar
```bash
## for scala 2.11
mvn clean deploy -Prelease-sign-artifacts -Pdisable-java8-doclint

## for scala 2.12

mvn clean deploy -Prelease-sign-artifacts -Pdisable-java8-doclint -Pscala-2.12
```