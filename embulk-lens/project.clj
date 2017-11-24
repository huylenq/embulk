(defproject embulk-lens "0.1.0-SNAPSHOT"
  :description "Embulk Lens"
  :url "https://embulk.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [clojure-lanterna "0.9.7"]]
  :aot [org.embulk.lens.Repl org.embulk.lens.Tracker])
