Lein Barrier
============

_ALPHA, this project is WORK IN PROGRESS. Use at you own peril!_

Lein Barrier is a Leiningen plugin that makes sure specific parts of
you code does not call functions in other parts.  Lein Barrier will
detect code boundry violations and either make you build fail or print
warnings during compile.


Install
-----

Lein Barrier requires Leiningen 2.0 or higher.

To use Lein Barrier you need to add it to your Leiningen
project. Simply add the following to `project.clj` file in the
`:project` vector:

    [triforkse/lein-barier "x.x.x"]

Where `"x.x.x"` is the latest version of Barrier which is available on
[Clojars](http://clojars.org/lein-barrier).


Usage
-----

Modify your `project.clj` file to include a `:barriers` key with the
constrains you want enforced in you project, e.g.:

    :barriers {'acme.view   ['acme.model]
               'acme        ['acme.util]
               'datomic.api ['acme.util 'acme.model]}

Thie would make define the following code barriers:

- The ns `'acme.view` or all of its decendents may _NOT_ be called from
  `'acme.model` or any of its decendents.
- The ns `'acme` or all of its decendents may _NOT_ be called from
  `'acme.model` or any of its decendents.
- The ns `'datomic.api` or all of its decendents may _NOT_ be called from
  `'acme.model` or any of its decendents.

Execute:

    $ lein barrier


License
-------

Lein Barrier is released under the
[Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
