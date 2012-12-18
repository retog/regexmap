# RegexMap

A Map containing regular expressions as keys and returning a value matching one of them when retrieving a value.

This is designed to merge the key regexes to one efficient determinstic finite state automaton where the accept states are mapped to values.

## Motivation

There are multiple usecases where one has to consult a lot of regexes to see which one maps a specific String. One such usecase is a Webserver such as JAX-RS implementation that has resources registered with a regex and requests being matched against this regexes to see which resource class is to be used.

Searching for existing implementation of a Java Regex Map only lead me to implementations that iterate through all the key-regexes to find matching values.

Implementations like the one proposed on <http://codebuild.blogspot.ch/2012/04/regular-expression-hashmap.html> offer some efficieny by compiling the regexes beforehand but still time complexity for accessing a value grows linearly to the amount of values in the map.

This is not needed. The idea is that the regexes are merged together so that the common prefixes of various key-regexes need not to be evaluated multiple times when looking up a value. More technically speaking the regexes are converted to a single deterministic state automaton where the accept states are mapped to the vaues in the map. 

I developed this as a side project for [Software Composition Seminar](http://scg.unibe.ch/wiki/softwarecompositionseminar).

## Status

The code is to be seen as a demonstration of an idea. It certainly contains bug.

Besides regular characters only the following three regex operators are supported: ., | and * .

Some example regexes from the tests:

    h.*
    m*h
    .*o|u

## Future

I have no big ambition with this code, I just played around with the idea. Feel however free to create issues. Somebody might be inspired by them and solve them in a fork.
