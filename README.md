# JPA Relationship Lab 1

## Instruction

You are given the following entity relationship model:

![Country capital one to one relationship diagram](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-jpa-lab1/lab1_erd.png)

There is a one-to-one relationship between `Country` and `Capital`.
A country has one capital city and a capital city belongs to one country. 

The lab repository has the required dependencies defined in the `pom.xml` file
and the database configuration is defined in the `persistence.xml` file.

Open the `Country` class.  Notice it has a field for the capital along with getter and setter methods.

```java
private Capital capital;
```

Open the `Capital` class.  It has a field for the country along with getter and setter methods.

```java
private Country country;
```

However, neither class is using JPA to establish the one-to-one relationship.  
You will update the code to use the `@OneToOne` annotation to implement the relationship.

1. Use **pgAdmin** to create a new database named `jpalab_db`:   
   ![create jpalab_db](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-jpa-lab1/jpalab_db.png)   
2. Check `persistence.xml` to make sure the `hibernate.hbm2ddl.auto` property is set to `create`.
3. Run the `JpaCreate.main` method to create the lab schema and populate the tables. 
   Unfortunately, the program will fail because the relationship
   between `Country` and `Capital` has not been established using JPA.  If you scroll through the
   error messages, you'll see an error message about the mapping between Capital and Country:  

```text
...
at org.example.JpaCreate.main(JpaCreate.java:30)
Caused by: org.hibernate.MappingException: Could not determine type for: org.example.model.Capital, at table: Country, for columns: [org.hibernate.mapping.Column(capital)]
....
```

4. Edit the `Country` class and add the `@OneToOne` annotation for the `capital` field.   
   Set the `fetch` property to `FetchType.LAZY` and set the `cascade` property to `CascadeType.REMOVE`.
5. Edit the `Capital` class and add the `@OneToOne` annotation for the `country` field.  
   Set the `mappedBy` property to `capital`.
6. Run the `JpaCreate.main` method.  The code should create two tables `COUNTRY` and `CAPITAL`.
7. Use the **pgAdmin** query tool to query the tables.

`SELECT * FROM CAPITAL;`

| ID  | NAME        |
|-----|-------------|
| 3   | Paris       |
| 4   | Mexico City |


`SELECT * FROM COUNTRY;`

| ID  | NAME    | CAPITAL_ID |
|-----|---------|------------|
| 1   | France  | null       |
| 2   | Mexico  | null       |


Notice the one-to-one association is stored in the `COUNTRY` table
as the column `CAPITAL_ID`.  However, the column contains null values
because we have not yet called the `setCapital` method in `JpaCreate`.

8. Edit `JpaCreate` to set the capital for each country as shown below:

```java
// create country-capital associations
country1.setCapital(capital1);
country2.setCapital(capital2);
```

9. Run the `JpaCreate.main` method to recreate the tables with the associations. 
10. Use **pgAdmin** to query the tables:

`SELECT * FROM CAPITAL;`

| ID  | NAME        |
|-----|-------------|
| 3   | Paris       |
| 4   | Mexico City |


`SELECT * FROM COUNTRY;`

| ID  | NAME    | CAPITAL_ID |
|-----|---------|------------|
| 1   | France  | 3          |
| 2   | Mexico  | 4          |



11. Change the `hibernate.hbm2ddl.auto` property in the `persistence.xml` to `none`
    before performing read operations. We will query data in the `JpaRead` class using the getter methods.
12. Edit the `JpaRead` class and add the following code to get and print the countries and capitals:

```java
package org.example;

import org.example.model.Capital;
import org.example.model.Country;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaRead {
    public static void main(String[] args) {
        // create EntityManager
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // get country data using primary key id=1  (France)
        Country country1 = entityManager.find(Country.class, 1);
        System.out.println(country1);
        // get the country's capital
        Capital capital1 = country1.getCapital();
        System.out.println(capital1);

        // get the capital using primary key id=4 (Mexico City)
        Capital capital2 = entityManager.find(Capital.class, 4);
        System.out.println(capital2);
        // get the capital's country
        Country country2 = capital2.getCountry();
        System.out.println(country2);

        // close entity manager and factory
        entityManager.close();
        entityManagerFactory.close();
    }
}
```

13. Run `JpaRead.main` and confirm the output:

```text
Hibernate: 
    select
        country0_.id as id1_1_0_,
        country0_.capital_id as capital_3_1_0_,
        country0_.name as name2_1_0_ 
    from
        Country country0_ 
    where
        country0_.id=?
Country{id=1, name='France'}
Hibernate: 
    select
        capital0_.id as id1_0_0_,
        capital0_.name as name2_0_0_,
        country1_.id as id1_1_1_,
        country1_.capital_id as capital_3_1_1_,
        country1_.name as name2_1_1_ 
    from
        Capital capital0_ 
    left outer join
        Country country1_ 
            on capital0_.id=country1_.capital_id 
    where
        capital0_.id=?
Capital{id=3, name='Paris'}
Hibernate: 
    select
        capital0_.id as id1_0_0_,
        capital0_.name as name2_0_0_,
        country1_.id as id1_1_1_,
        country1_.capital_id as capital_3_1_1_,
        country1_.name as name2_1_1_ 
    from
        Capital capital0_ 
    left outer join
        Country country1_ 
            on capital0_.id=country1_.capital_id 
    where
        capital0_.id=?
Capital{id=4, name='Mexico City'}
Country{id=2, name='Mexico'}
```

14. Edit `JpaDelete` to delete the country with id `1`.  This should also cascade the deletion of the capital with id `3`
    since you set the `cascade` property for the `capital` field to `CascadeType.REMOVE`.

```java
package org.example;


import org.example.model.Country;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaDelete {
    public static void main(String[] args) {
        // create EntityManager
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // get record
        Country country1 = entityManager.find(Country.class, 1);

        // access transaction object
        EntityTransaction transaction = entityManager.getTransaction();

        // create and use transaction to save updated value
        transaction.begin();
        entityManager.remove(country1);
        transaction.commit();

        // close entity manager
        entityManager.close();
        entityManagerFactory.close();
    }
}
```


```text
Hibernate: 
    select
        country0_.id as id1_1_0_,
        country0_.capital_id as capital_3_1_0_,
        country0_.name as name2_1_0_ 
    from
        Country country0_ 
    where
        country0_.id=?
Hibernate: 
    select
        capital0_.id as id1_0_0_,
        capital0_.name as name2_0_0_,
        country1_.id as id1_1_1_,
        country1_.capital_id as capital_3_1_1_,
        country1_.name as name2_1_1_ 
    from
        Capital capital0_ 
    left outer join
        Country country1_ 
            on capital0_.id=country1_.capital_id 
    where
        capital0_.id=?
Hibernate: 
    delete 
    from
        Country 
    where
        id=?
Hibernate: 
    delete 
    from
        Capital 
    where
        id=?
```

15. Use **pgAdmin** to query the tables:

`SELECT * FROM CAPITAL;`

| ID  | NAME        |
|-----|-------------|
| 4   | Mexico City |


`SELECT * FROM COUNTRY;`

| ID  | NAME    | CAPITAL_ID |
|-----|---------|------------|
| 2   | Mexico  | 4          |


