(ns org.embulk.lens.core
  (:require [lanterna.screen :as s]))

(defn main []
  (let [screen (s/get-screen :text)]
    (s/in-screen screen
                 (s/put-string screen 0 0 "Welcome to Embulk Lens")
                 (s/redraw screen)
                 (s/get-key-blocking screen))))

(defn message)
