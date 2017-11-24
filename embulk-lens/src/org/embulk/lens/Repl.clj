(ns org.embulk.lens.Repl
  (:require
    [clojure.tools.nrepl.server :refer [start-server stop-server]])
  (:gen-class
    :methods [[start [] void]
              [stop [] void]]))

(defn -start [self]
  (defonce server (start-server :port 7888))
  (println "Started a REPL on port 7888"))

(defn -stop [self]
  (if (not (nil? server))
    (stop-server server)
    (println "It seems you didn't start any REPL server!")))
