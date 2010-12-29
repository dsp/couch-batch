(defproject couch-batch "0.1.0-SNAPSHOT"
  :description "delete multiple couchdb documents selected by a regex"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [com.ashafa/clutch "0.2.3-SNAPSHOT"]
		 [commons-cli/commons-cli "1.2"]]
  :dev-dependencies [[swank-clojure "1.2.0"]]
  :main couch-batch.core
  :repositories ["commons" "http://mirrors.ibiblio.org/pub/mirrors/maven2"])
