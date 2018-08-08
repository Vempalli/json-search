# json-search
Command line application to search from given json data and return matched results

## Build status
Build status of continus integration on travis: https://travis-ci.org/Vempalli/json-search

[![Build Status](https://travis-ci.org/Vempalli/json-search.svg?branch=master)](https://travis-ci.org/Vempalli/json-search)

## Prerequisites
To run the tool we need the following

* gradle
* Java 8

## Building the project
* clone the project `git clone https://github.com/Vempalli/json-search.git`
* `cd json-search`
* `./gradlew build`

## Tests
From json-search folder execute command `./gradlew test`

## Running the project
From json-search folder execute command `./gradlew run --console=plain -q`

## Technical Details

### Features Available
* has feature 1
* has feature 2
* has feature 3

### Assumptions
* The fields are same for each object of specific category. Ex: All user objects have same fields, all ticket objects 
have same fields
* The id for each object type is unique. Meaning they don't overlap across organizations and are scoped
* While retrieving related information, I only retrieved for user and ticket search. For organization we could have retrieved
all users in org and all tickets created in the org, but I did not get chance to implement this

### Design Decisions
In order to design a robust solution, Apart from accuracy, I have considered efficiency, memory utilization and 
ease of maintenance as key factors. Few design choices I had to make are:

* **JSON Processing Library choice** I had to make a choice on which JSON processing library to use. The 2 choices that I debated 
are between javaee's json-p and Google's Gson. Both of these support stream processing. While json-p is native from javaee 
and works using pull model it is relatively new and not much of formatting options exist for this. So I've decided 
to use Gson as it is mature enough and there is lot of help online if I run into any issues

* **Stream Processing Vs Object Model** The two choices I had to decide for reading the JSON is Object model and Stream
model. In object model, JSON structures are represented as a Java class. We will have a POJO class which represents any
JSON object of specific type and we build this object during processing. Note that we are actually building an object
and holding it in memory while we are parsing it. This looks okay for small JSON objects but for complex object structure
the class will be maintenance nightmare.
The other choice is a Streaming model, where we will read the JSON one event after the other. We will have iterator to the
JSON which helps to process this. At any point we are only holding the piece of json object that is currently being processed
in the memory (but not the whole object as in object model). This takes away dependency from memory but we will need to
handle some exception logic in case of streaming exceptions.
I used streaming model for this project

* **Caching** Currently the application only performs search operation. So everything is read intensive and there is always
scope to speed up search by introducing caching layer. I used Guava's cache which is easy to integrate. I have only cached
list of fields that are present in each json object type. I could have applied the same logic to store dependency information
between different objects or actual search results for that matter using guava but I am constrained by time on submitting this project.

#### Process Flow
may be a flow chart or picture explaining different classes

#### Future enhancements
* Support multiple requests - multi threading or build a restful web application
* Read the streaming data from url rather than a file - should need minimum code change
* other layer of caching

## Credits
TBD