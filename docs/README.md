# NullDefense | Gson
Removes invalid objects during Gson parsing which are marked as required, yet null/empty.

[![Build Status](https://travis-ci.org/VenomVendor/NullDefense.svg?branch=master)](https://travis-ci.org/VenomVendor/NullDefense) [![codecov](https://codecov.io/gh/VenomVendor/NullDefense/branch/master/graph/badge.svg)](https://codecov.io/gh/VenomVendor/NullDefense) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f067e5c9a9c14c53843bc56f0669d993)](https://www.codacy.com/app/VenomVendor/NullDefense?utm_source=github.com&utm_medium=referral&utm_content=VenomVendor/NullDefense&utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/f067e5c9a9c14c53843bc56f0669d993)](https://www.codacy.com/app/VenomVendor/NullDefense?utm_source=github.com&utm_medium=referral&utm_content=VenomVendor/NullDefense&utm_campaign=Badge_Coverage) [![codebeat badge](https://codebeat.co/badges/ef5996c2-d284-454e-a497-f5438f8867e7)](https://codebeat.co/projects/github-com-venomvendor-nulldefense-master) [![CodeFactor](https://www.codefactor.io/repository/github/venomvendor/nulldefense/badge)](https://www.codefactor.io/repository/github/venomvendor/nulldefense) [![Maintainability](https://api.codeclimate.com/v1/badges/12788f6de414f39eb749/maintainability)](https://codeclimate.com/github/VenomVendor/NullDefense/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/12788f6de414f39eb749/test_coverage)](https://codeclimate.com/github/VenomVendor/NullDefense/test_coverage)
---

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

## Code Coverage
<a href="https://codecov.io/gh/VenomVendor/NullDefense"><img src="https://codecov.io/gh/VenomVendor/NullDefense/branch/master/graphs/sunburst.svg" width="250" /><a/>

## Java Docs
<a target="_blank" href="https://venomvendor.github.io/VenomVendor/javadoc/">Click here for Java documentation<a/>
