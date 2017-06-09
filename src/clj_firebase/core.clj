(ns clj-firebase.core
  #_(:require [com.google.firebase.database :refer :all])
  (:require [clojure.java.io :as io]
            [clojure.core.reducers :as r])
  (:import (com.google.firebase FirebaseOptions FirebaseOptions$Builder FirebaseApp)
           (com.google.firebase.auth FirebaseCredential FirebaseCredentials)
           (java.io FileInputStream)
           (com.google.firebase.database FirebaseDatabase DatabaseReference ValueEventListener DataSnapshot DatabaseError DatabaseReference$CompletionListener)))

(defonce db
         (with-open
           [service-account (io/input-stream
                              "ogtest-5bd9c-firebase-adminsdk-e19h3-46a0b800fc.json")]
           (let [opts (->
                        (FirebaseOptions$Builder.)
                        (.setCredential (FirebaseCredentials/fromCertificate service-account))
                        (.setDatabaseUrl "https://shining-inferno-4546.firebaseio.com")
                        (.build))]
             (FirebaseApp/initializeApp opts "ogtest")
             (-> (FirebaseDatabase/getInstance)
                 (.getReference)))))

(defn db-ref-get-in [^DatabaseReference db refs]
  "Returns a db ref to the new place in db with get-in syntax"
  (r/reduce (fn
              [acc e]
              (-> acc
                  (.child e)))
            db
            refs))

(defn db-ref->value [^DatabaseReference db cb]
  "Returns the value snapshot for a db ref"
  (do (-> db
          (.addValueEventListener (reify ValueEventListener
                                    (^void onDataChange [this ^DataSnapshot snapshot]
                                      (println "Got data for cb")
                                      (cb snapshot))
                                    (^void onCancelled [this ^DatabaseError error]
                                      (println "Error retrieving data: " error)))))))

(defn data-snapshot->value [^DataSnapshot snapshot]
  (.getValue snapshot))

(defn create [{:keys [^DatabaseReference db v cb]}
              & {:keys [k]}]
  (let [completion (reify DatabaseReference$CompletionListener
                     (^void onComplete [this ^DatabaseError err ^DatabaseReference ref]
                       (println "Create complete: " err ref)
                       (cb err ref)))]
    (do (case k
          nil
          (do
            (println "No key provided")
            (-> db
                (.push)
                (.setValue v completion)))
          (do
            (println "Key provided: " k)
            (-> db
                (.child k)
                (.setValue v completion)))))))

