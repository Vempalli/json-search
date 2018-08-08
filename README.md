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

###Features Available
* has feature 1
* has feature 2
* has feature 3

###Assumptions
* no.of fields are same for every object
* all id's in given data are unique and does not repeat across orgs 
* related information is only fetched for users and tickets search - not org search

###Design Decisions
little bit description
* **JSON Processing Library** json-p vs gson?
* **Stream Processing Vs Object Model** why not object model
* **Caching** why did we cache and what

####Process Flow
may be a flow chart or picture explaining different classes

####Future enhancements
* Support multiple requests - multi threading or build a restful web application
* Read the streaming data from url rather than a file - should need minimum code change
* other layer of caching

## Credits
Give proper credits. This could be a link to any repo which inspired you to build this project, any blogposts or links to people who contrbuted in this project. 
