(ns couch-batch.core
  (:gen-class)
  (:import (org.apache.commons.cli Options Option
				   BasicParser UnrecognizedOptionException
				   MissingArgumentException)
	   (java.io Console))
  (:use clojure.contrib.condition)
  (:require [com.ashafa.clutch :as cdb]
	    [clojure.contrib.trace :as t]))

(defn- option
  ([opt hasarg desc]
     (Option. opt hasarg desc))
  ([opt longopt hasarg desc]
     (Option. opt longopt hasarg desc)))

(defn parse-opts
  [options args]
  (try 
    (let [opts (Options.)]
      (doseq [o options]
	(.addOption opts (apply option o)))
      (.parse (BasicParser.) opts (into-array args)))
    (catch MissingArgumentException mae
      (raise :type :missing-argument))
    (catch UnrecognizedOptionException uoe
      (raise :type :unknown-option))))

(defn usage []
  (println "Usage: couch-batch [OPTIONS] <host> <database>"))

(defn read-password [prompt]
  (if-let [cons (. System console)]
    (do
      (print prompt "")
      (flush)
      (String. (.readPassword cons)))
    nil))
    
(defn -main [& args]
  (handler-case :type
    (let [opts (parse-opts [["h" "help"   false "Help"]
			    ["r" "regex"  true  "Regex"]
			    ["d" "delete" false "Delete"]
			    ["u" "user"   true  "Username"]
			    ["p" "password" false "Password"]]
			   args)]
      ; -h specified
      (if (or (< (count (.getArgList opts)) 2)
	      (.hasOption opts "h"))
	(raise :type :help))
      ; go for it
      (let [dburl (first (.getArgs opts))
	    db    (second (.getArgs opts))
	    pass  (if (.hasOption opts "p") (read-password "Password:") nil)
	    user  (.getOptionValue opts "u")
	    reg   (re-pattern
		   (if (.hasOption opts "r")
		     (.getOptionValue opts "r")
		     ".*"))]
	(cdb/with-db (cdb/get-database {:host dburl :name db
					:username user
					:password pass})
	  (println "`" db)
	  (doseq [s (filter #(re-find reg %)
			    (map #(:key %)
				 (:rows (cdb/get-document "_all_docs"))))]
	    (print "  |" s)
	    (if (and (.hasOption opts "d")
		     (cdb/delete-document (cdb/get-document s)))
	      (print "  deleted"))
	    (println "")))))
    (handle :missing-argument
      (usage))
    (handle :help
      (usage))
    (handle :unknown-option
      (usage))))
