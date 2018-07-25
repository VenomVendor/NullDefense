# NullDefense | Gson
Removes invalid objects during Gson parsing which are marked as required, yet null/empty.

[![Build Status](https://travis-ci.org/VenomVendor/NullDefense.svg?branch=master)](https://travis-ci.org/VenomVendor/NullDefense) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/VenomVendor/NullDefense) [ ![Download](https://api.bintray.com/packages/venomvendor/maven/NullDefense/images/download.svg) ](https://bintray.com/venomvendor/maven/NullDefense/_latestVersion)

# Code Quality
[![codecov](https://codecov.io/gh/VenomVendor/NullDefense/branch/master/graph/badge.svg)](https://codecov.io/gh/VenomVendor/NullDefense) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f067e5c9a9c14c53843bc56f0669d993)](https://www.codacy.com/app/VenomVendor/NullDefense?utm_source=github.com&utm_medium=referral&utm_content=VenomVendor/NullDefense&utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/f067e5c9a9c14c53843bc56f0669d993)](https://www.codacy.com/app/VenomVendor/NullDefense?utm_source=github.com&utm_medium=referral&utm_content=VenomVendor/NullDefense&utm_campaign=Badge_Coverage) [![codebeat badge](https://codebeat.co/badges/ef5996c2-d284-454e-a497-f5438f8867e7)](https://codebeat.co/projects/github-com-venomvendor-nulldefense-master) [![CodeFactor](https://www.codefactor.io/repository/github/venomvendor/nulldefense/badge)](https://www.codefactor.io/repository/github/venomvendor/nulldefense) [![Maintainability](https://api.codeclimate.com/v1/badges/12788f6de414f39eb749/maintainability)](https://codeclimate.com/github/VenomVendor/NullDefense/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/12788f6de414f39eb749/test_coverage)](https://codeclimate.com/github/VenomVendor/NullDefense/test_coverage)

-------------------

## Installation
In `build.gradle` add the following dependencies

```coffeescript
repositories {
    maven {
        url  'https://dl.bintray.com/venomvendor/maven'
    }
}

dependencies {
    compile 'com.venomvendor.library:gson-nulldefense:1.0.0-beta'
}
```

### Usage
```java
TypeAdapterFactory nullDefenceAdapter = new NullDefenseTypeAdapterFactory(Mandatory.class);

Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(nullDefenceAdapter)
        .enableComplexMapKeySerialization()
        .setPrettyPrinting()
        .setLenient()
        .serializeNulls()
        .create();
```

### To Retain Empty Collection
```java
TypeAdapterFactory typeAdapter = new NullDefenseTypeAdapterFactory(Mandatory.class)
         // To retain empty collection
         .retainEmptyCollection();
```

### To Remove Empty Collection
```java
TypeAdapterFactory typeAdapter = new NullDefenseTypeAdapterFactory(Mandatory.class)
         // To remove empty collection, this is default
         .removeEmptyCollection();
```

-------------------


# Tests

#### POJO

```java
public class Parent {

    @Mandatory
    private String name;

    @Mandatory
    private CustomList<Child> children;

    // Setter/Getter
}

public class Child {

    @Mandatory
    private String name;
    // I am optional
    private Boolean isMale;

    // Has no effect on primitive
    @Mandatory
    private int age;

    @Mandatory
    private Language language;

    // Setter/Getter
}

public class Language {

    @Mandatory
    private List<String> knownLanguages;

    // I am optional
    private List<String> learning;

    // Setter/Getter
}
```

#### parent-valid.json

```json
{
  "name": "VenomVendor",
  "children": [
    {
      "name": "Queen",
      "isMale": false,
      "age": 26,
      "language": {
        "knownLanguages": [
          "Telugu",
          "Tamil"
        ],
        "learning": [
          "French"
        ]
      }
    },
    {
      "name": "Prince",
      "isMale": true,
      "age": 5,
      "language": {
        "knownLanguages": [
          "English",
          "Sanskrit"
        ],
        "learning": []
      }
    },
    {
      "name": "Princess",
      "isMale": false,
      "age": 3,
      "language": {
        "knownLanguages": [
          "Kannada"
        ],
        "learning": null
      }
    }
  ]
}
```

##### Result
```java
assertEquals(3, parent.getChildren().size());
```

#### valid-missing-primitive.json

> Annotation has no effect on primitive fields

```json
{
  "name": "VenomVendor",
  "children": [
    {
      "name": "Queen",
      "isMale": false,
      "age": 26,
      "language": {
        "knownLanguages": [
          "Telugu",
          "Tamil"
        ],
        "learning": [
          "French"
        ]
      }
    },
    {
      "name": "Prince",
      "isMale": true,
      "age": null,
      "language": {
        "knownLanguages": [
          "English",
          "Sanskrit"
        ],
        "learning": []
      }
    },
    {
      "name": "Princess",
      "isMale": false,
      "language": {
        "knownLanguages": [
          "Kannada"
        ],
        "learning": null
      }
    }
  ]
}
```

##### Result
```java
assertEquals(3, parent.getChildren().size());
```

#### missing-name-in-child.json

> Should generate one child, as rest doesn't have mandatory `name`

```json
{
  "name": "VenomVendor",
  "children": [
    {
      /* Missing Mandatory Field */
      "isMale": false,
      "age": 26,
      "language": {
        "knownLanguages": [
          "Telugu",
          "Tamil"
        ],
        "learning": [
          "French"
        ]
      }
    },
    {
      "name": null, // Null Mandatory Field
      "isMale": true,
      "age": 5,
      "language": {
        "knownLanguages": [
          "English",
          "Sanskrit"
        ],
        "learning": []
      }
    },
    {
      "name": "Princess",
      "isMale": false,
      "age": 3,
      "language": {
        "knownLanguages": [
          "Kannada"
        ],
        "learning": null
      }
    }
  ]
}
```

##### Result
```java
assertEquals(1, parent.getChildren().size());
```

#### missing-known-language-no-child.json

> Should not generate any children due to missing `knownLanguages`, hence parent is also `null`, as it doesn't have mandatory children

```json
{
  "name": "VenomVendor",
  "children": [
    {
      "name": "Queen",
      "isMale": false,
      "age": 26,
      "language": {
        "knownLanguages": null,
        "learning": [
          "French"
        ]
      }
    },
    {
      "name": "Prince",
      "isMale": true,
      "age": 5,
      "language": {
        "knownLanguages": [],
        "learning": []
      }
    },
    {
      "name": "Princess",
      "isMale": false,
      "age": 3,
      "language": {
        "learning": null
      }
    }
  ]
}
```

##### Result
```java
assertNull(parent);
```


### [More Tests here](https://github.com/VenomVendor/NullDefense/tree/master/src/test "Unit Tests")
 
-------------------

## Code Coverage
<a href="https://codecov.io/gh/VenomVendor/NullDefense/list/master/"><img src="https://codecov.io/gh/VenomVendor/NullDefense/branch/master/graphs/sunburst.svg" width="250" /><a/>

## Java Docs
<a target="_blank" href="https://venomvendor.github.io/NullDefense/docs/javadoc/">Click here for Java documentation<a/>
