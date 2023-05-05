## Message syncer
***
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

Syncer-spring-boot-starter provides an easy way to push/pull message to/from middlewares
 like elasticsearch, redis etc. by using java annotations or basic mappers. 

No templates will be autowired after importing syncer!

### Related dependencies versions
***
* 2.7.5 - springboot starter parent
* 2.0.29 - fastjson2
* 5.8.18 - hutool core

### Installation
***
It is a maven project, make sure you have prepared a maven environment in your local machine.
If not, install maven firstly. Download maven [here](https://maven.apache.org/download.cgi).

Select a branch and pull source code to your machine, execute maven command in project root directory:
```html
mvn clean install
```

Then project has been packaged and submitted to local maven repository.

### Usage
***
Import maven project in your own project:
```html
<dependency>
  <groupId>cn.maolin.syncer</groupId>
  <artifactId>syncer-spring-boot-starter</artifactId>
  <version>${syncer-spring-boot-starter-version}</version>
</dependency>
```

### License
Syncer-spring-boot-starter is released under [MIT license](https://opensource.org/licenses/MIT)

