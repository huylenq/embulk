(ns org.embulk.lens.Tracker
  (:require
    [clojure.tools.nrepl.server :refer [start-server stop-server]]))

(gen-class :name org.embulk.lens.Tracker)

(import org.embulk.lens.Tracker)
(gen-class
  :name org.embulk.lens.Tracker
  :methods [#^{:static true} [instance [] org.embulk.lens.Tracker]
            [captureIn [Class String] void]
            [captureIn [String String] void]
            [captureOut [Class String] void]
            [captureOut [String String] void]])

;; TODO: thread-safety
(defonce instance (Tracker.))

(defn -instance [] instance)

(def timeline (atom []))
(def stack (atom []))

(defn capture
  [event]
  ;; Timeline
  (swap! timeline #(conj % event))
  ;; Event Stacks
  ;; TODO: this can be done in a cleaner way
  (case (:direction event)
    :in (swap! stack #(conj % event))
    :out (swap! stack #(let [tail (last %)]
                         (if (and (= (:component tail) (:component event))
                                  (= (:event tail) (:event event))
                                  (pop %))
                           (throw (IllegalStateException. "Last frame on stack isn't the popping event!")))))))

(defn -captureIn [self component event]
  (capture
    {:component component
     :event (symbol event)
     :direction :in
     :thread (.getName (Thread/currentThread))
     }))

(defn -captureOut [self component event]
  (capture
    {:component component
     :event (symbol event)
     :direction :out
     :thread (.getName (Thread/currentThread))}))

